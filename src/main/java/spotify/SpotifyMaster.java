/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import settings.Settings;
//import javafxapplication3.Utilities;
import javafx.concurrent.Task;

/**
 * Provides the initial access point for the spotify services. Provides a way to
 * retrieve a spotify api.
 *
 */
public class SpotifyMaster {

    //private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://localhost:8888");
    private String accessToken;
    private String authCode;
    /**
     * 0-unauthorized, 1-authorized, 2-unauthorized(timeout), 3-authorization
     * failed
     */
    //public AtomicInteger atomicAuth = new AtomicInteger(0);
    //public IntegerProperty authStatus = new SimpleIntegerProperty();

    final String clientId;
    final String clientSecret;
    private SpotifyApi globalApi;
    private BooleanProperty isAuth = new SimpleBooleanProperty();
    final String redirectURI;
    private String refreshToken;
    private Task refresher = null;

    private ObjectProperty<LoginState> status = new SimpleObjectProperty<>(LoginState.UNAUTHORIZED);
    private UserStatus userStatus;

    /**
     * Default constructor for the SpotifyMaster. Initializes with default keys
     * for authorization flow.
     */
    public SpotifyMaster() {
        this("5105529d072043268947875be2d03d6f", "70b23fb6632a4719a4c08b81773e280d", "http:///localhost:" + Settings.
                getString("spotify.auth.portNum", "8888"));
    }

    /**
     * Constructor for the SpotifyMaster.
     *
     * @param cId  Client ID - Public application ID
     * @param cSec Client secret - Private application ID
     * @param red  Redirect URI - URI to redirect authorization to
     */
    public SpotifyMaster(String cId, String cSec, String red) {
        clientId = cId;
        clientSecret = cSec;
        redirectURI = red;
    }

    /**
     * Begins authorization flow. Navigates to spotify web accounts service for
     * login, starts server for code retrieval, and waits to receive a value
     * from the
     * communication file.
     */
    public void attemptLogin() throws URISyntaxException, IOException, Exception {
        getStatus().set(LoginState.AUTHORIZING);
        try {
            globalApi = SpotifyApi.builder().setClientId(clientId).
                    setClientSecret(clientSecret).
                    setRedirectUri(new URI(redirectURI)).build();
            final String expState = genKey(30);
            AuthorizationCodeUriRequest authUriReq = globalApi.
                    authorizationCodeUri()
                    .state(expState)
                    .scope("user-read-private,user-read-email,playlist-read-private,playlist-read-collaborative,user-read-playback-state,user-read-currently-playing,user-modify-playback-state").
                    show_dialog(Settings.
                            getBoolean("spotify.auth.show_dialog", true)).
                    build();
            URI reqUri = authUriReq.execute();
            //System.out.println("uri: " + reqUri.toString());

            //URI uri = new URI(reqUri + (Settings.getBoolean("spotify.auth.show_dialog", false) ? "" : "&show_dialog=true"));
            Desktop.getDesktop().browse(reqUri);
            String response = runAuthServer();
            String retCode = response.substring(7, response.indexOf("&"));
            String retState = response.substring(response.indexOf("state=") + 6);
            if (!retState.equals(expState)) {
                //System.out.println(retState);
                //System.out.println(expState);
                throw new Exception("States do not match!");
            }
            //System.out.println("code:\n" + retCode);
            authCode = retCode;
            AuthorizationCodeCredentials acc = globalApi.
                    authorizationCode(authCode).build().execute();
            accessToken = acc.getAccessToken();
            refreshToken = acc.getRefreshToken();
            //System.out.println("Access token:\n" + accessToken);
            //System.out.println("Refresh token:\n" + refreshToken);
            int time = acc.getExpiresIn();
            //System.out.println("expires in: " + time + " seconds");
            globalApi.setAccessToken(accessToken);
            globalApi.setRefreshToken(refreshToken);
            System.out.println("auth? " + Settings.
                    getBoolean("spotify.auth.auto_refresh", true));
            if (Settings.getBoolean("spotify.auth.auto_refresh", true)) {
                startAutoRefresh(refreshToken, time);
            }

            isAuth.set(true);
            userStatus = new UserStatus(this);
            Settings.pushSetting("spotify.auth.show_dialog", false);
            this.getStatus().set(LoginState.AUTHORIZED);
        } catch (Exception ex) {
            this.getStatus().set(LoginState.AUTHFAILED);
            throw ex;
            //Logger.getLogger(SpotifyMaster.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void attemptLoginSync() {

    }

    /**
     * Creates and starts the task that waits for a response to be retrieved
     * from the
     * communication file.
     *
     * @param timeout The amount of time it takes
     * @param state   They key sent to spotify in a preceding operation that is
     *                passed back along with the authorization key to ensure that the
     *                application retrieves a valid code.
     * @return A reference to the task object
     * @throws IOException
     * @throws InterruptedException
     */
    @Deprecated
    private Task awaitResponse(final long timeout, String state) throws IOException, InterruptedException {
        //TODO check parameters

        Task tsk = new Task() {

            @Override
            protected Object call() throws Exception {
                File f = new File(Settings.resDir + "web");
                final Path path = f.toPath();
                System.out.println(path);
                long startTime = System.currentTimeMillis();
                try (final WatchService watchService = FileSystems.getDefault().
                        newWatchService()) {
                    final WatchKey watchKey = path.
                            register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    while (System.currentTimeMillis() - startTime < timeout) {
                        final WatchKey wk = watchService.take();
                        for (WatchEvent<?> event : wk.pollEvents()) {
                            if (System.currentTimeMillis() - startTime > timeout) {
                                System.err.println("Waiting time expired");
                                return null;
                            }
                            //we only register "ENTRY_MODIFY" so the context is always a Path.
                            final Path changed = (Path) event.context();
                            System.out.println(changed);
                            if (changed.endsWith("response.txt")) {
                                System.out.println("My file has changed");

                                String[] vals = getValues();
                                if (vals != null) {
                                    if (vals[1].equals(state)) {
                                        System.out.
                                                println("Authorization code retrieved successfully!");
                                        authCode = vals[0];

                                        //isAuth.set(true);
                                        //atomicAuth.set(1);
                                        //authStatus.set(1);
                                    } else {
                                        System.err.
                                                println("States do not match!");
                                        //throw new RuntimeException("States do not match");
                                    }
                                    return null;
                                } else {
                                    System.err.
                                            println("Could not do something. Continuing until timeout");
                                    //throw new RuntimeException("Could not do something");
                                }
                            }
                        }

                        // reset the key
                        boolean valid = wk.reset();
                        if (!valid) {
                            System.out.println("Key has been unregistered");
                        }
                    }
                    System.err.println("Waiting time expired");
                    throw new TimeoutException("Waiting time expired!");
                    //atomicAuth.set(3);
                } catch (Exception ex) {
                    throw ex;
                    //Logger.getLogger(SpotifyMaster.class.getName()).log(Level.SEVERE, null, ex);
                }

                //return null;
            }
        };

        Thread t = new Thread(tsk);
        t.start();
        tsk.setOnSucceeded(( e ) -> {
            isAuth.set(true);
            this.getStatus().set(LoginState.AUTHORIZED);
        });
        return tsk;
    }

    public boolean canAuthorize() {
        return status.get() == LoginState.AUTHFAILED || status.get() == LoginState.UNAUTHORIZED;
    }

    private String genKey(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int rand = (int) (Math.random() * 52);
            builder.append((char) (rand > 25 ? rand + 65 - 26 : 97 + rand));
        }
        return builder.toString();
    }

    /**
     * Gets the spotify API
     *
     * @return the Api
     */
    public SpotifyApi getApi() {
        return globalApi;

    }

    public SpotifyApi getGlobalApi() {
        return globalApi;
    }

    public BooleanProperty getIsAuth() {
        return isAuth;
    }

    /**
     * @return the status
     */
    public ObjectProperty<LoginState> getStatus() {
        return status;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    /**
     * Retrieves the status and authorization code from the file.
     *
     * @return The status and the code in an easily usable format
     */
    @Deprecated
    private String[] getValues() {
        String[] out = null;
        File fl = new File(Settings.resDir + "web/response.txt");
        System.out.println(fl.getPath() + " exists? " + fl.exists());
        try (Scanner scanner = new Scanner(fl)) {
            scanner.reset();

            String st = scanner.nextLine();
            String a = st.substring(st.indexOf("code=") + 5, st.indexOf("&"));
            String b = st.substring(st.indexOf("state=") + 6);

            System.out.println("code: " + a);
            System.out.println("state: " + b);
            out = new String[]{a, b};
        } catch (FileNotFoundException e) {
            System.out.println("here");
            //e.printStackTrace();
        } catch (NoSuchElementException e) {
            System.out.println("ici");
            //e.printStackTrace();
            return null;
        }

        return out;
    }

    public boolean isFullyAuthorized() {
        return status.get() == LoginState.AUTHORIZED;
    }

    /**
     * Completes the authorization of the api object and begins the refresh
     * countdown.
     */
    @Deprecated
    private void loginPart2() throws IOException, SpotifyWebApiException {
        AuthorizationCodeCredentials acc = globalApi.authorizationCode(authCode).
                build().execute();
        accessToken = acc.getAccessToken();
        refreshToken = acc.getRefreshToken();
        System.out.println("Access token:\n" + accessToken);
        System.out.println("Refresh token:\n" + refreshToken);
        int time = acc.getExpiresIn();
        System.out.println("expires in: " + time + " seconds");
        globalApi.setAccessToken(accessToken);
        globalApi.setRefreshToken(refreshToken);
        System.out.println("auth? " + Settings.
                getBoolean("spotify.auth.auto_refresh", true));
        if (Settings.getBoolean("spotify.auth.auto_refresh", true)) {
            startAutoRefresh(refreshToken, time);
        }

    }

    /**
     * Manually logs the user out (drops api references and authorizations) and
     * disables automatic login
     */
    public void logout() {
        if (refresher != null) {
            boolean cc = refresher.cancel();
            if (!cc) {
                System.err.println("Could not cancel refresh thread!");
            }

            //} else {
            //}
        }
        getStatus().set(LoginState.UNAUTHORIZED);
        //getStatus().set(LoginState.UNAUTHORIZED);
        //if (cc || true) {
        refresher = null;
        isAuth.set(false);
        //authStatus.set(0);

        authCode = null;
        accessToken = null;
        refreshToken = null;
        Settings.pushSetting("spotify.auth.show_dialog", true);

        if (userStatus != null) {
            userStatus.stopQuery();

            userStatus = null;
        }
    }

    /**
     * Runs the server for authorization key retrieval.
     *
     * @return The combined key and code received from spotify
     */
    private String runAuthServer() throws Exception {
        //String[] str = {"cd C:\\Users\\Aaron\\Documents\\Synesthesia resources\\web", "node authorize.js"};
        try {
            File fl = new File(Settings.resDir + "web/authorizer.js");
            if (!fl.exists()) {
                throw new FileNotFoundException("Could not locate authorizer file!");
            }
            ProcessBuilder pb = new ProcessBuilder("node", fl.getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();
            String str;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.
                    getInputStream()))) {
                //while (process.isAlive() );
                process.waitFor(60, TimeUnit.SECONDS);
                boolean isSuccessful = process.exitValue() == 1;
                //boolean isSuccessful = process.waitFor(30, TimeUnit.SECONDS);
                System.out.println("success? " + isSuccessful);
                str = reader.readLine();
            }
            return str;

        } catch (IOException | InterruptedException ex) {
            System.out.println("heck!");
            //ex.printStackTrace();
            throw ex;
            //return null;
        }
    }

    /**
     * Configures and starts the thread that will refresh the authorization key
     * for prolonged (over 1 hr) use of the application.
     *
     * @param refreshToken The token needed to get a new authorization code from
     *                     the spotify api.
     * @param exp          The expiration time in... Milliseconds, I think?
     */
    private void startAutoRefresh(final String refreshToken, long exp) {
        final long nanoExp = (exp - ((exp - 600) > 0 ? 600 : 0)) * 1000;
        //long nanoExp = 10000;
        globalApi.setRefreshToken(refreshToken);
        ZonedDateTime expDate = ZonedDateTime.now().plusSeconds(exp);
        System.out.println("Time: " + ZonedDateTime.now().
                format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        System.out.println("The first token will expire at: " + expDate.
                format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        System.out.
                println("The first token will be refreshed at: " + ZonedDateTime.
                        now().plus(nanoExp, ChronoUnit.MILLIS).
                        format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        Task tsk = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {
                long startTime;

                //String currRefeshToken = refreshToken;
                while (true) {
                    startTime = System.currentTimeMillis();
                    System.out.println("Starting refresh countdown...");
                    while (System.currentTimeMillis() - startTime < nanoExp) {
                        //System.out.println(Settings.getBoolean("Spotify.auth.auto_refresh", false));
                        if (isCancelled() || !Settings.
                                getBoolean("spotify.auth.auto_refresh", true)) {
                            return 0;
                        }
                        //updateProgress(System.currentTimeMillis() - startTime, nanoExp);
                    }
                    if (Settings.getBoolean("spotify.auth.auto_refresh", true)) {
                        System.out.println("Refreshing access token...");
                        AuthorizationCodeCredentials newAuth = getGlobalApi().
                                authorizationCodeRefresh().build().execute();
                        //newToken = globalApi.refreshAccessToken().refreshToken(refreshToken).build().get().getAccessToken();
                        getGlobalApi().
                                setRefreshToken(newAuth.getRefreshToken());
                        getGlobalApi().setAccessToken(newAuth.getAccessToken());
                        //newAuth.get
                        System.out.println("New token: " + newAuth.
                                getAccessToken());
                    } else {
                        System.out.
                                println("Auto refresh cancelled due to setting!");
                        return 1;
                    }

                }
                //System.err.println("Done");
            }
        };
        refresher = tsk;
        //th.setDaemon(true);
        Thread th = new Thread(tsk);
        th.setName("spotify refresh");
        th.start();

    }

}
