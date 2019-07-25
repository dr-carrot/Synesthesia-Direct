/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.AudioAnalysis;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import effects.simple.EffectManager;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javax.sound.sampled.Mixer;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import output.*;
import settings.Settings;
import spotify.LoginState;
import spotify.PlayingData;
import spotify.UserStatus;
import streaming.Streamer;
import spotify.SpotifyMaster;

/**
 *
 * @author 'Aaron Lomba'
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private CheckMenuItem syn;
    @FXML
    private CheckMenuItem slv;
    @FXML
    private CheckMenuItem eff;
    @FXML
    private Tab inputTab;
    @FXML
    private Label label;
    @FXML
    private Label busyText;
    @FXML
    private ProgressIndicator busyIndicator;
    @FXML
    private TabPane tPane;
    @FXML
    private ListView<Port> devices;
    @FXML
    private ListView<Mixer> inputs;
    @FXML
    private VBox outputVBox;
    @FXML
    private ListView<Output> allOutputList;
    @FXML
    private Tab spotifyTab;
    @FXML
    private AnchorPane spotifyPane;
    @FXML
    private ImageView spotifyAlbumImage;
    @FXML
    private TableView propTable;
    @FXML
    private Label spotName;
    @FXML
    private Label spotArtist;
    @FXML
    private Label spotArtistTitle;
    @FXML
    private Label spotAlbum;
    @FXML
    private Label spotAlbumTitle;
    @FXML
    private TableView<TableViewPair> spotDatView;
    @FXML
    private TableColumn spotDatKey;
    @FXML
    private TableColumn spotDatVal;
    private int pseudoCount = 0;
    private List<Integer> windowIDs = new ArrayList<>();
    int windowIndex = 0;

    //List<Port> devices.getItems() = new ArrayList<>();
    private final int FADE_TIME = 4;
    private Task fader = null;
    //@Deprecated
    //private Mixer selectedMixer = null;
    private Stage stage;
    private Scene scene;

    private Streamer streamer;
    private SpotifyMaster spotifyMaster;

    @FXML
    private void handleLaunchGraph() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                    getResource("/fxml/AudioGraph.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stg = new Stage();
            stg.initOwner(stage);
            AudioGraphController ctrlr = (AudioGraphController) fxmlLoader.
                    getController();
            //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
            //stg.initModality(Modality.NONE);
            //stg.initStyle(StageStyle.DECORATED);
            //stg.in

            stg.setTitle("Graphic Analysis Tool");
            stg.setScene(new Scene(root1));
            stg.setResizable(true);
            ctrlr.setup(stg, streamer);
            stg.showAndWait();
            //streamer.removeOutput(graphHook);

            //System.out.println("out");
            //songTable.
        } catch (Exception h) {
            //ErrorHandler.standard(h);
            h.printStackTrace();
        }
    }

    @FXML
    private void handleOpenInSpotify() {
        try {

            Desktop.getDesktop().browse(null);
        } catch (Exception e) {

        }
    }

    @FXML
    private void handleSpotifyLogout() {
        if (spotifyMaster != null) {
            spotifyMaster.logout();
            if (tPane.getSelectionModel().getSelectedIndex() == 3) {
                tPane.getSelectionModel().clearAndSelect(0);
            }
            spotName.setText("No data!");
            spotAlbum.setText("No data!");
            spotArtist.setText("No data!");
            spotifyAlbumImage.setImage(null);

        }
    }

    @FXML
    private void handleLaunchGradientController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                    getResource("/fxml/GradientControllerGUI.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stg = new Stage();
            stg.initOwner(stage);
            GradientController ctrlr = (GradientController) fxmlLoader.
                    getController();
            //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
            //stg.initModality(Modality.NONE);
            //stg.initStyle(StageStyle.DECORATED);
            //stg.in
            stg.setTitle("Gradient Editor <3");
            stg.setScene(new Scene(root1));
            stg.setResizable(true);
            ctrlr.setup(stg, null);
            stg.showAndWait();

            //System.out.println("out");
            //songTable.
        } catch (Exception h) {
            //ErrorHandler.standard(h);
            h.printStackTrace();
        }
    }

    @FXML
    private void handleSpotifyIntegration() {
        if (spotifyMaster == null || spotifyMaster.canAuthorize()) {

            Task<SpotifyMaster> authorizer = new Task<SpotifyMaster>() {
                @Override
                protected SpotifyMaster call() throws Exception {
                    if (spotifyMaster == null) {
                        spotifyMaster = new SpotifyMaster();
                    }
                    spotifyMaster.attemptLogin();
                    return spotifyMaster;
                }
            };

            working("Connecting to Spotify accounts service...", ( Event e ) ->
            {
                authorizer.cancel();
                displayError("Connection cancelled!");

            });
            authorizer.setOnFailed(( WorkerStateEvent we ) -> {
                doneWorking(authorizer.getException().getMessage());
                System.err.println(authorizer.getException().getMessage());
            });

            authorizer.setOnSucceeded(( WorkerStateEvent we ) -> {
                try {
                    //spotifyTab.setContent(spotifyPane);
                    String name = spotifyMaster.getApi().
                            getCurrentUsersProfile().build().execute().
                            getDisplayName();
                    doneWorking("Successfully authorized " + name);
                    System.out.println("Logged in " + name);
                    // new UserStatus(spotifyMaster);
                    UserStatus user = spotifyMaster.getUserStatus();
                    user.eternalQuery(Settings.
                            getLong("spotify.query_delay", 100));
                    user.setOnEternalQueryFailed(( WorkerStateEvent eh ) -> {
                        displayError("Cannot connect to spotify!");
                    });
                    tPane.getSelectionModel().clearAndSelect(3);
                    user.playingDataProperty().
                            addListener(( ObservableValue<? extends PlayingData> obs, PlayingData b, PlayingData npd ) ->
                            {
                                //System.out.println("song changed");
                                if (npd.getPlayingArtists().get().size() > 1) {
                                    spotArtist.setText("Artists:");
                                } else {
                                    spotArtist.setText("Artist:");
                                }
                                spotDatView.getItems().clear();
                                //try {
                                Task<Pair<AudioAnalysis, AudioFeatures>> tsk = new Task<Pair<AudioAnalysis, AudioFeatures>>() {
                                    @Override
                                    protected Pair<AudioAnalysis, AudioFeatures> call() throws Exception {
                                        AudioAnalysis aa = spotifyMaster.
                                                getApi().
                                                getAudioAnalysisForTrack(npd.
                                                        getPlayingId().get()).
                                                build().execute();
                                        AudioFeatures af = spotifyMaster.
                                                getApi().
                                                getAudioFeaturesForTrack(npd.
                                                        getPlayingId().get()).
                                                build().execute();
                                        return new Pair<>(aa, af);
                                    }
                                };
                                tsk.setOnSucceeded(( p ) -> {
                                    if (npd.getPlaying().get().getItem().getId().
                                            equals(spotifyMaster.getUserStatus().
                                                    getplayingData().get().
                                                    getPlayingId().get())) {
                                        npd.getAnalysis().set(tsk.getValue().
                                                getKey());
                                        npd.getFeatures().set(tsk.getValue().
                                                getValue());
                                        //System.out.println("getting analysis...");
                                        spotDatView.getItems().setAll(npd.
                                                getBasicAnalysis());
                                        spotDatView.getItems().addAll(npd.
                                                getBasicFeatures());
                                    }
                                });
                                tsk.setOnFailed(( p ) -> {
                                    System.err.
                                            println("could not retrieve data!");
                                    tsk.getException().printStackTrace();
                                });
                                Thread datThr = new Thread(tsk);
                                datThr.start();
                                spotName.
                                        setOnMouseClicked(( MouseEvent me ) ->
                                        {
                                            if (me.getClickCount() == 2) {
                                                try {
                                                    Desktop.getDesktop().
                                                            browse(new URI(npd.
                                                                    getPlayingContext().
                                                                    get().
                                                                    getItem().
                                                                    getUri()));
                                                } catch (URISyntaxException | IOException ex) {
                                                }
                                            }
                                        });
                                spotAlbum.
                                        setOnMouseClicked(( MouseEvent me ) ->
                                        {
                                            if (me.getClickCount() == 2) {
                                                try {
                                                    Desktop.getDesktop().
                                                            browse(new URI(npd.
                                                                    getPlayingContext().
                                                                    get().
                                                                    getItem().
                                                                    getAlbum().
                                                                    getUri()));
                                                } catch (URISyntaxException | IOException ex) {
                                                }
                                            }
                                        });
                                spotArtist.
                                        setOnMouseClicked(( MouseEvent me ) ->
                                        {
                                            if (me.getClickCount() == 2) {
                                                try {
                                                    Desktop.getDesktop().
                                                            browse(new URI(npd.
                                                                    getPlayingContext().
                                                                    get().
                                                                    getItem().
                                                                    getArtists()[0].
                                                                    getUri()));
                                                } catch (URISyntaxException | IOException ex) {
                                                }
                                            }
                                        });

                                spotName.setText(npd.getPlayingTrack().get());
                                spotArtist.
                                        setText(npd.getPlayingArtists().get().
                                                toString().substring(1, npd.
                                                        getPlayingArtists().
                                                        get().toString().
                                                        length() - 1));
                                com.wrapper.spotify.model_objects.specification.Image albImg = spotifyMaster.
                                        getUserStatus().nextImage();
                                spotAlbum.setText(npd.getPlayingAlbum().get());
                                Image alb = new Image(albImg.
                                        getUrl(), spotifyAlbumImage.
                                                getFitWidth(), spotifyAlbumImage.
                                                getFitHeight(), true, true, true);

                                //Image alb = new Image(npd.getPlaying().get().getItem().getAlbum().getImages()[0].getUrl(), spotifyAlbumImage.getFitWidth(), spotifyAlbumImage.getFitHeight(), true, true, true);
                                spotifyAlbumImage.setImage(alb);

                            });

                    /*
                     * user.getplayingData().addListener(( obs, o, n ) -> {
                     * System.out.println("----------------------------------");
                     * System.out.println("title: " +
                     * user.getplayingData().get().getPlayingTrack().get());
                     * System.out.println("artist: " +
                     * user.getplayingData().get().getPlayingArtists().get());
                     * System.out.println("album: " +
                     * user.getplayingData().get().getPlayingAlbum().get());
                     * System.err.println("id: " +
                     * user.getplayingData().get().getPlayingId().get());
                     * System.out.println("isPlaying: " +
                     * user.getplayingData().get().getIsPlaying().get());
                     * try {
                     * Thread.sleep(100);
                     * } catch (InterruptedException ex) {
                     * Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE,
                     * null, ex);
                     * }
                     * try {
                     * AudioAnalysis analysis =
                     * spotifyMaster.getApi().getAudioAnalysisForTrack(user.getplayingData().get().getPlayingId().get()).build().execute();
                     * AudioFeatures features =
                     * spotifyMaster.getApi().getAudioFeaturesForTrack(user.getplayingData().get().getPlayingId().get()).build().execute();
                     *
                     * System.out.println("\nTempo: " +
                     * analysis.getTrack().getTempo());
                     * //System.out.println("Rhythm string: " +
                     * analysis.getTrack().getRhythmString());
                     * System.out.println("Loudness: " +
                     * analysis.getTrack().getLoudness());
                     * System.out.println("Time signature: " +
                     * analysis.getTrack().getTimeSignature());
                     * System.out.println("Key: " +
                     * analysis.getTrack().getKey());
                     * System.out.println("\nAcousticness: " +
                     * features.getAcousticness());
                     * System.out.println("Dancability: " +
                     * features.getDanceability());
                     * System.out.println("Energy: " + features.getEnergy());
                     * System.out.println("Instrumentalness: " +
                     * features.getInstrumentalness());
                     * System.out.println("Liveness: " +
                     * features.getLiveness());
                     * System.out.println("Speechiness: " +
                     * features.getSpeechiness());
                     * System.out.println("Valence: " + features.getValence());
                     * } catch (IOException ex) {
                     * Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE,
                     * null, ex);
                     * } catch (SpotifyWebApiException ex) {
                     * Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE,
                     * null, ex);
                     * }
                     *
                     * });
                     */
                } catch (Exception ex) {
                    displayError(ex);
                    Logger.getLogger(FXMLDocumentController.class.getName()).
                            log(Level.SEVERE, null, ex);
                    //System.err.println("Could not retrieve name");
                    // doneWorking("No username available!");
                } finally {
                    stage.requestFocus();

                }
            });

            Thread th = new Thread(authorizer);
            th.start();

        }

    }

    private void saveFXConfig(Output item) {
        File def = new File(Settings.getString("syn.last_dir", Settings.docDir));
        FileChooser chooser = new FileChooser();
        if (def.exists()) {
            chooser.setInitialDirectory(def);
        }
        chooser.setTitle("Choose a config");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Config file", "*.efc"),
                new FileChooser.ExtensionFilter("All files", "*"));
        File f = chooser.showSaveDialog(stage);
        if (f == null) {
            return;
        }

    }

    private void loadFXConfig(Output out) {
        File def = new File(Settings.getString("syn.last_dir", Settings.docDir));
        FileChooser chooser = new FileChooser();
        if (def.exists()) {
            chooser.setInitialDirectory(def);
        }
        chooser.setTitle("Choose a config");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Config file", "*.efc"),
                new FileChooser.ExtensionFilter("All files", "*"));
        File f = chooser.showOpenDialog(stage);
        if (f == null) {
            return;
        }
        out.loadConfigFile(f);
    }

    private void displayError(Throwable thr) {
        displayError(thr.getMessage());
    }

    private void displayError(String msg) {
        busyText.setTextFill(Paint.valueOf("RED"));
        busyText.setText(msg);
        busyIndicator.setVisible(false);
        fadeaway();
    }

    private void working(String desc, EventHandler cancelAction) {
        busyText.setContextMenu(null);
        busyIndicator.setContextMenu(null);
        if (cancelAction != null) {
            ContextMenu cm = new ContextMenu();
            MenuItem cancelItem = new MenuItem("Cancel");
            cm.getItems().add(cancelItem);
            cancelItem.setOnAction(cancelAction);
            busyText.setContextMenu(cm);
            busyIndicator.setContextMenu(cm);
        }

        if (fader != null && fader.isRunning()) {
            fader.cancel();
        }
        busyText.setTextFill(Paint.valueOf("BLACK"));
        busyText.setOpacity(1);
        busyText.setText(desc);
        busyText.setVisible(true);
        busyIndicator.setVisible(true);
    }

    private void working(String desc) {
        working(desc, null);
    }

    private void doneWorking() {
        busyText.setTextFill(Paint.valueOf("BLACK"));
        busyText.setVisible(false);
        busyIndicator.setVisible(false);
    }

    private void doneWorking(String newText) {
        System.out.println("done working");
        busyIndicator.setVisible(false);
        busyText.setTextFill(Paint.valueOf("BLACK"));
        busyText.setText(newText);
        busyText.setOpacity(1);
        fadeaway();
        System.out.println("fade called");

    }

    private void fadeaway() {
        if (fader != null && fader.isRunning()) {
            fader.cancel(true);
            busyText.setOpacity(1);
        }

        fader = new Task() {
            @Override
            protected Object call() throws Exception {
                busyText.setOpacity(1);
                Thread.sleep(4000);
                busyText.setOpacity(1);
                long start = System.currentTimeMillis();
                long end = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 10000) {
                    Thread.sleep(200);
                    busyText.setOpacity(1.0f - (float) (System.
                            currentTimeMillis() - start) / 10000.0f);
                    //System.out.println(1.0 - (float)(System.currentTimeMillis() - start) / 10000.0f);
                }
                return null;
            }
        };

        Thread fadeThread = new Thread(fader);
        fadeThread.start();
        //busyText.opacityProperty().bind(fader.progressProperty().negate().add(1.0));
    }

    private void openPort(Port p) {
        working("Opening port " + p.getPortName() + "...");
        Task<Boolean> tsk = new Task() {
            @Override
            protected Boolean call() throws Exception {
                return p.openPort();
            }
        };

        tsk.setOnSucceeded(( a ) -> {
            p.setValid(true);
            devices.refresh();
            doneWorking("Port opened!");
        });
        tsk.setOnFailed(( a ) -> {
            p.setValid(false);
            devices.refresh();

            displayError("Could not open port!");
        });
        tsk.setOnCancelled(( a ) -> {
            displayError("Cancelled connection!");
        });

        new Thread(tsk).start();

    }

    private void connectToDevice(String name) {
        Task<Port> tk = new Task<Port>() {

            @Override
            protected Port call() throws Exception {
                updateProgress(-1, 1);
                Port myPort = new Port(name);

                try {

                    if (myPort.openPort()) {
                        myPort.setParams(SerialPort.BAUDRATE_9600,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        //devices.getItems().add(myPort);
                        System.out.println("added");
                        return myPort;
                    } else {
                        System.out.println("not added");
                    }
                } catch (SerialPortException e) {
                    updateProgress(0, 1);
                    //System.out.println("err");
                    throw e;

                }
                updateProgress(0, 1);
                return null;
            }
        };
    }

    private void validateDevices() {

    }

    @FXML
    private void handleSaveConfiguration() {
        FileChooser fc = new FileChooser();
        File f = new File(Settings.home + "/Documents/Synesthesia Direct/");
        if (!f.exists()) {
            f.mkdirs();
        }
        fc.setInitialDirectory(f);
        fc.setTitle("Save the current configuration");
        fc.getExtensionFilters().
                add(new ExtensionFilter("Synesthesia Direct Config", "*.sdcf"));
        File res = fc.showSaveDialog(stage);
        if (res == null) {
            return;
        }

        try (Scanner s = new Scanner(res)) {
            while (s.hasNext()) {

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    //available.getItems().clear();
    //});
    private Task updateDeviceList() {
        //devices.getItems().addAll(SerialPortList.getPortNames());
        //available.getItems().removeIf(( String o ) -> {
        //for (SerialPort p : mgr.getPorts()) {
        //  if (p.getPortName().equals(o)) {
        //    return true;
        //}
        //}
        // return false;
        working("Looking for devices...");
        Task<String[]> tsk = new Task() {
            @Override
            protected String[] call() throws Exception {

                return SerialPortList.getPortNames();
            }
        };
        tsk.setOnSucceeded(( WorkerStateEvent a ) -> {
            try {
                System.out.println(Arrays.toString(tsk.get()));

                for (String s : tsk.get()) {
                    boolean inList = false;
                    for (Port p : devices.getItems()) {
                        if (p.getPortName().equals(s)) {
                            System.out.println("dup");
                            inList = true;
                            break;
                        }
                    }
                    if (!inList) {
                        devices.getItems().add(new Port(s));
                    }
                }

                for (Port p : devices.getItems()) {
                    p.setValid(false);
                    for (String s : tsk.get()) {
                        if (p.getPortName().equals(s)) {
                            p.setValid(true);
                            break;
                        }
                    }

                    if (!p.isValid()) {
                        System.out.println("dead");
                    }
                }

            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
                Logger.getLogger(FXMLDocumentController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            devices.refresh();
            doneWorking("Updated device list!");
        });

        tsk.setOnCancelled(( w ) -> {
            doneWorking("Cancelled!");
        });
        tsk.setOnFailed(( a ) -> {
            doneWorking("Update failed! Check log for details.");
        });

        Thread th = new Thread(tsk);
        th.start();

        return tsk;
    }

    private Task updateInputList() {
        working("Looking for input devices...");
        Task<List<Mixer>> tsk = new Task() {
            @Override
            protected List<Mixer> call() throws Exception {
                List<Mixer> mixers = Streamer.getTargets();
                return mixers;
            }
        };
        tsk.setOnSucceeded(( WorkerStateEvent a ) -> {
            try {
                inputs.getItems().clear();
                inputs.getItems().addAll(tsk.get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
                Logger.getLogger(FXMLDocumentController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

            doneWorking();
        });

        tsk.setOnCancelled(( w ) -> {
            doneWorking("Cancelled!");
        });
        tsk.setOnFailed(( a ) -> {
            doneWorking("Update failed! Check log for details.");
        });

        Thread th = new Thread(tsk);
        th.start();

        return tsk;

    }

    @FXML
    private void handleAddPseudoport() {
        devices.getItems().add(new PseudoPort("Pseudo " + pseudoCount));
        pseudoCount++;
    }

    @FXML
    private void handleOutputRefresh(ActionEvent event) {
        updateDeviceList();
        devices.refresh();
    }

    private void loadPortSettingUI(Port p) {
        outputVBox.getChildren().clear();
        if (p instanceof PseudoPort) {
            Button rmPort = new Button("Remove Pseudoprt");
            rmPort.setOnAction(( a ) -> {
                try {
                    p.closePort();
                } catch (SerialPortException ex) {
                }
                devices.getSelectionModel().clearSelection();
                devices.getItems().remove(p);

                for (int i = 0; i < allOutputList.getItems().size(); i++) {
                    if (allOutputList.getItems().get(i).getPort().equals(p)) {
                        allOutputList.getItems().remove(i);
                        i--;
                    }
                }

                outputVBox.getChildren().clear();
                devices.refresh();
            });
            outputVBox.getChildren().add(rmPort);

        }

        Button nConn = new Button("New RGB Mono Connection");
        nConn.setOnAction(( a ) -> {
            Output o = new RGBMono("New Connection " + p.getOutputs().size(), p);
            p.addOutput(o);
            allOutputList.getItems().add(o);
            allOutputList.refresh();
        });

        Button nConn2 = new Button("New Addressable LED Connection");
        nConn2.setOnAction(( a ) -> {
            Output o = new RGBAddressable("New Connection " + p.getOutputs().
                    size(), p);
            p.addOutput(o);
            allOutputList.getItems().add(o);
            allOutputList.refresh();
        });

        Button nConn3 = new Button("New RGB Split Connection");
        nConn3.setOnAction(( a ) -> {
            Output o = new RGBSeparate("New Connection " + p.getOutputs().size(), p);
            p.addOutput(o);
            allOutputList.getItems().add(o);
            allOutputList.refresh();
        });

        ChoiceBox<Port.types> typeBox = new ChoiceBox();
        typeBox.getItems().addAll(Port.types.values());
        typeBox.getSelectionModel().select(Port.types.Default);
        typeBox.getSelectionModel().selectedItemProperty().
                addListener(( ObservableValue<? extends Port.types> a, Port.types old, Port.types nw ) ->
                {
                    if (old != nw) {
                        p.setType(nw);
                        p.setValid(false);
                    }
                });

        outputVBox.getChildren().addAll(nConn, nConn2, nConn3, typeBox);

    }

    public void setup(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
        //Button login = new Button("Login!");
        //login.setOnAction(( ae ) -> handleSpotifyIntegration());
        //spotifyTab.setContent(login);
    }

    private void postInit() {
        updateInputList();
        updateDeviceList();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Settings.setup();

        spotifyAlbumImage.setOnMouseClicked(( me ) -> {
            if (me.getClickCount() == 2) {
                try {
                    com.wrapper.spotify.model_objects.specification.Image albImg = spotifyMaster.
                            getUserStatus().nextImage();
                    Image nwImg = new Image(albImg.
                            getUrl(), spotifyAlbumImage.
                                    getFitWidth(), spotifyAlbumImage.
                                    getFitHeight(), true, true, true);
                    spotifyAlbumImage.setImage(nwImg);
                } catch (NullPointerException e) {

                }
            }
        });

        spotDatKey.setCellValueFactory(new PropertyValueFactory<>("val1"));
        spotDatVal.setCellValueFactory(new PropertyValueFactory<>("val2"));
        spotDatView.setRowFactory(( TableView<TableViewPair> tv ) -> {

            TableRow<TableViewPair> tr = new TableRow<TableViewPair>() {
                //private Tooltip tooltip;
                public void updateItem(TableViewPair obj, boolean empty) {
                    super.updateItem(obj, empty);
                    if (empty || obj == null) {
                        setTooltip(null);
                    } else {

                        setTooltip(obj.getTooltip());
                    }
                }
            };

            //tr.setTooltip(new Tooltip(tr.getItem().getDescription()));
            return tr;
        });

        Button toDevices = new Button("Add a device, then choose an output!");
        toDevices.setOnAction(( a ) -> {
            tPane.getSelectionModel().clearAndSelect(1);
        });
        allOutputList.setPlaceholder(toDevices);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.out.println("shutdown caught! writing settings!");
                Settings.writeAll();
            }
        });

        syn.setSelected(true);
        syn.selectedProperty().
                addListener(( ObservableValue<? extends Boolean> a, Boolean old, Boolean nw ) ->
                {

                    if (nw) {
                        try {
                            EffectManager.stop();
                        } catch (Exception e) {
                            System.err.println("cant stop effect manager");
                        }
                        eff.setSelected(false);
                        slv.setSelected(false);
                        Settings.pushSetting("main.mode_index", 0);
                    } else if (old) {
                        handleSpotifyLogout();
                    }
                });
        eff.selectedProperty().
                addListener(( ObservableValue<? extends Boolean> a, Boolean old, Boolean nw ) ->
                {
                    if (nw) {
                        try {
                            EffectManager.init();
                            streamer.stop();
                        } catch (Exception e) {
                            System.err.println("manager is already running");
                        }
                        syn.setSelected(false);
                        slv.setSelected(false);
                        Settings.pushSetting("main.mode_index", 1);
                    }
                });
        slv.selectedProperty().
                addListener(( ObservableValue<? extends Boolean> a, Boolean old, Boolean nw ) ->
                {
                    if (nw) {
                        eff.setSelected(false);
                        syn.setSelected(false);
                        Settings.pushSetting("main.mode_index", 2);
                    }
                });
        inputTab.disableProperty().bind(syn.selectedProperty().not());

        switch (Settings.getInt("main.mode_index", 0)) {
            case 0:
                syn.setSelected(true);
                break;
            case 1:
                eff.setSelected(true);
                inputTab.getTabPane().getSelectionModel().clearAndSelect(1);

                break;
            case 2:
                slv.setSelected(true);
                inputTab.getTabPane().getSelectionModel().clearAndSelect(1);

                break;
            default:
                syn.setSelected(true);
                break;
        }

        streamer = new Streamer();
        inputs.setCellFactory(( ListView<Mixer> lv ) -> {
            ListCell<Mixer> lc = new ListCell<Mixer>() {
                @Override
                public void updateItem(Mixer mxr, boolean empty) {
                    super.updateItem(mxr, empty);
                    if (empty || mxr == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    if (mxr.equals(streamer.getMixer())) {
                        setTextFill(Paint.valueOf("GREEN"));
                    } else {
                        setTextFill(Paint.valueOf("BLACK"));
                    }

                    setText(mxr == null ? "NULL" : mxr.getMixerInfo().getName());
                    setTooltip(new Tooltip(mxr == null ? "NULL" : mxr.
                            getMixerInfo().getDescription()));

                }

            };

            lc.setOnMouseClicked(( MouseEvent me ) -> {
                //System.out.println(lc.getItem() == null);
                if (me.getClickCount() == 2 && lc.getItem().isOpen()) {
                    System.out.println("close?");
                    //streamer.stop();
                } else if (me.getClickCount() == 2) {
                    System.out.println("2");
                    streamer.stop();
                    streamer.setMixer(lc.getItem());
                    streamer.start();
                    //System.out.println("picked input!");
                    inputs.refresh();
                }

            });

            return lc;
        });

        devices.setCellFactory(( ListView<Port> lv ) -> {
            ListCell<Port> lc = new ListCell<Port>() {
                @Override
                public void updateItem(Port port, boolean empty) {
                    if (port != null && !empty) {
                        super.updateItem(port, empty);
                    }

                    if (empty || port == null) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }

                    setText(port.getPortName());
                    if (port.isOpened()) {
                        setTextFill(Paint.valueOf("GREEN"));
                    } else if (port.isValid()) {
                        setTextFill(Paint.valueOf("BLACK"));
                        //System.out.println("valid");
                    } else {
                        //System.out.println("invalid");
                        setTextFill(Paint.valueOf("RED"));
                    }
                }

            };

            lc.selectedProperty().
                    addListener(( ObservableValue<? extends Boolean> val, Boolean oldVal, Boolean newVal ) ->
                    {
                        if (newVal && !oldVal) {
                            loadPortSettingUI(lc.getItem());
                        }
                    });

            lc.setOnMouseClicked(( MouseEvent me ) -> {
                if (me.getClickCount() == 2) {
                    if (lc == null || lc.getItem() == null) {
                        return;
                    } else if (lc.getItem().isOpened()) {
                        try {
                            if (lc.getItem().closePort()) {
                                doneWorking("Port closed!");
                            } else {
                                doneWorking("Could not close port!");
                            }
                        } catch (SerialPortException ex) {
                            doneWorking("Error when closing port!");
                        }
                    } else {
                        openPort(lc.getItem());
                    }
                } else if (me.getClickCount() == 3) {

                } else if (me.isShiftDown()) {
                    if (streamer.getMixer() == null) {
                        doneWorking("Please choose an input first!");
                    } else {
                        //lc.getItem().activateAll(selectedMixer);
                    }
                }
                devices.refresh();

            });

            return lc;
        });
        allOutputList.getSelectionModel().
                setSelectionMode(SelectionMode.MULTIPLE);
        allOutputList.setCellFactory(( ListView<Output> lv ) -> {
            ListCell<Output> lc;
            lc = new ListCell<Output>() {

                @Override
                public void updateItem(Output item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    setText(null);

                    HBox box = new HBox();
                    box.setSpacing(30);
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.prefWidthProperty().bind(allOutputList.widthProperty());
                    //box.setPrefWidth(allOutputList.getWidth() / 2);
                    //box.setMinWidth(allOutputList.getWidth() / 2);
                    Button wind = new Button("Window");
                    //MenuItem conf = new MenuItem("Configure");
                    ColorPicker pkr = new ColorPicker(Color.BLACK);
                    //if(syn.isSelected()){
                    SplitMenuButton config = new SplitMenuButton();
                    config.setText("Configure");
                    config.setOnAction(( a ) -> {

                        if (syn.isSelected()) {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                                        getResource("/fxml/OutputConfigUI.fxml"));
                                Parent root1 = (Parent) fxmlLoader.load();
                                Stage stg = new Stage();
                                stg.initOwner(stage);
                                OutputConfigUIController ctrlr = (OutputConfigUIController) fxmlLoader.
                                        getController();
                                //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
                                stg.initModality(Modality.NONE);
                                stg.initStyle(StageStyle.UTILITY);
                                if (item.isSynced()) {
                                    stg.setTitle("Configure: " + item.
                                            getSyncMaster().getPort().
                                            getPortName() + ": " + item.
                                                    getName() + " (Synced)");
                                    ctrlr.setup(item.getSyncMaster(), stg);
                                } else if (item.isSyncMaster()) {
                                    stg.setTitle("Configure: " + item.getPort().
                                            getPortName() + ": " + item.
                                                    getName() + " (Sync master)");
                                    ctrlr.setup(item, stg);
                                } else {
                                    stg.setTitle("Configure: " + item.getPort().
                                            getPortName() + ": " + item.
                                                    getName());
                                    ctrlr.setup(item, stg);
                                }

                                stg.setScene(new Scene(root1));
                                stg.setResizable(false);
                                stg.show();
                                //ctrlr.setup(item, stg);
                                //System.out.println("out");
                                //songTable.
                            } catch (Exception h) {
                                //ErrorHandler.standard(h);
                                h.printStackTrace();
                            }

                        } else {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                                        getResource("/fxml/RGBEffectController.fxml"));
                                Parent root1 = (Parent) fxmlLoader.load();
                                Stage stg = new Stage();
                                stg.initOwner(stage);
                                RGBEffectControllerController ctrlr = (RGBEffectControllerController) fxmlLoader.
                                        getController();
                                //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
                                stg.initModality(Modality.NONE);
                                stg.initStyle(StageStyle.UTILITY);
                                if (item.isSynced()) {
                                    stg.setTitle("Configure: " + item.
                                            getSyncMaster().getPort().
                                            getPortName() + ": " + item.
                                                    getName() + " (Synced)");
                                    ctrlr.setup(item.getSyncMaster(), stg);
                                } else if (item.isSyncMaster()) {
                                    stg.setTitle("Configure: " + item.getPort().
                                            getPortName() + ": " + item.
                                                    getName() + " (Sync master)");
                                    ctrlr.setup(item, stg);
                                } else {
                                    stg.setTitle("Configure: " + item.getPort().
                                            getPortName() + ": " + item.
                                                    getName());
                                    ctrlr.setup(item, stg);
                                }
                                stg.setScene(new Scene(root1));
                                stg.setResizable(false);
                                stg.show();

                                //System.out.println("out");
                                //songTable.
                            } catch (Exception h) {
                                //ErrorHandler.standard(h);
                                h.printStackTrace();
                            }

                        }
                    });

                    //config.set
                    MenuItem loadConf = new MenuItem("Load...");
                    MenuItem saveConf = new MenuItem("Save...");
                    config.getItems().addAll(loadConf, saveConf);
                    loadConf.setOnAction(( a ) -> {
                        loadFXConfig(item);
                    });
                    saveConf.setOnAction(( a ) -> {
                        saveFXConfig(item);
                    });

                    String xtra = item.isSyncMaster() ? "**" : item.isSynced() ? "*" : "";

                    Label port = new Label(item.getPort().getPortName());
                    Label conn = new Label(item.getPort().getPortName() + "\t" + item.
                            getName() + xtra);
                    if (item.isActive()) {
                        port.setTextFill(Paint.valueOf("GREEN"));
                        conn.setTextFill(Paint.valueOf("GREEN"));
                    } else {
                        port.setTextFill(Paint.valueOf("BLACK"));
                        conn.setTextFill(Paint.valueOf("BLACK"));
                    }

                    conn.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
                    conn.setTooltip(new Tooltip(conn.getText()));
                    conn.prefWidthProperty().bind(box.widthProperty().
                            multiply(.40));

                    box.getChildren().addAll(conn);
                    //box.getChildren().addAll(port, conn);

                    if (item instanceof RGBSeparate) {

                        Ellipse r = new Ellipse(9, 9);
                        Ellipse g = new Ellipse(9, 9);
                        Ellipse b = new Ellipse(9, 9);

                        r.fillProperty().bind(((RGBSeparate) item).r);
                        g.fillProperty().bind(((RGBSeparate) item).g);
                        b.fillProperty().bind(((RGBSeparate) item).b);
                        r.setVisible(true);
                        g.setVisible(true);
                        b.setVisible(true);

                        HBox clrs = new HBox(1, r, g, b);
                        clrs.setPrefWidth(60);
                        clrs.setAlignment(Pos.CENTER);
                        box.getChildren().add(clrs);
                    } else if (item instanceof RGBAddressable) {

                    } else {
                        Rectangle vis = new Rectangle(60, 20);
                        //vis.fillProperty().bind(item.color);
                        item.color.addListener(( a, old, nw ) -> {
                            //System.out.println("heard color change");
                            vis.setFill(nw);
                        });
                        box.getChildren().add(vis);
                    }

                    box.getChildren().addAll(config, wind);
                    setGraphic(box);

                    wind.setOnAction(( a ) -> {
                        if (item.getVisualWindowStage() != null) {
                            item.getVisualWindowStage().requestFocus();
                        } else {

                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().
                                        getResource(item.getVisualizerFXML()));
                                Parent root1 = (Parent) fxmlLoader.load();
                                AbstractVisualWindowController ctrlr = (AbstractVisualWindowController) fxmlLoader.
                                        getController();

                                Stage stg = new Stage();
                                stg.initOwner(stage);
                                //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
                                stg.initModality(Modality.NONE);
                                stg.initStyle(StageStyle.UTILITY);
                                stg.setAlwaysOnTop(false);
                                stg.setTitle("Preview: " + item.getPort().
                                        getPortName() + ": " + item.getName());
                                stg.setScene(new Scene(root1));
                                stg.setResizable(true);
                                stg.show();
                                ctrlr.setup(item, stg);
                                item.setVisualWindowStage(stg);
                                stg.setOnCloseRequest(( ae ) -> {
                                    item.setVisualWindowStage(null);
                                });
                                //System.out.println("out");
                                //songTable.
                            } catch (Exception h) {
                                //ErrorHandler.standard(h);
                                h.printStackTrace();
                            }
                        }
                    });

                }

            };

            MenuItem del = new MenuItem("Delete");
            del.setOnAction(( a ) -> {
                lc.getItem().delete();
                allOutputList.getItems().remove(lc.getItem());
            });

            MenuItem test = new MenuItem("Test");
            test.setOnAction(( a ) -> {
                lc.getItem().test();
            });

            MenuItem sync = new MenuItem("sync");
            MenuItem desync = new MenuItem("desync");
            MenuItem desyncAll = new MenuItem("desync group");
            sync.setOnAction(( e ) -> {
                lc.getItem().sync(new ArrayList(allOutputList.
                        getSelectionModel().getSelectedItems()));
                System.out.println("synced. master: " + lc.getItem().getName());
                allOutputList.refresh();
            });
            desync.setOnAction(( a ) -> {
                lc.getItem().desync();
                allOutputList.refresh();
            });
            desyncAll.setOnAction(( a ) -> {
                lc.getItem().desyncAll();
                allOutputList.refresh();
            });

            lc.setOnContextMenuRequested(( ContextMenuEvent ce ) -> {
                desyncAll.setVisible(false);
                desync.setVisible(false);
                sync.setVisible(false);

                if (allOutputList.getSelectionModel().getSelectedItems().size() > 1) {
                    sync.setVisible(true);
                    for (Output o : allOutputList.getSelectionModel().
                            getSelectedItems()) {
                        if (o.isSynced()) {
                            sync.setVisible(false);
                            break;
                        }
                    }

                } else if (allOutputList.getSelectionModel().getSelectedItems().
                        size() == 1) {
                    if (lc.getItem().isSyncMaster()) {
                        desyncAll.setVisible(true);
                    } else if (lc.getItem().isSynced()) {
                        desync.setVisible(true);
                        desyncAll.setVisible(true);
                    }
                }
            });

            ContextMenu cm = new ContextMenu(del, test, sync, desync, desyncAll);

            lc.setContextMenu(cm);

            lc.setOnMouseClicked(( MouseEvent me ) -> {
                if (me.getClickCount() == 2 && lc.getItem() != null && lc.
                        getItem().getPort().isOpened() && lc.getItem().getPort().
                                isValid()) {
                    if (lc.getItem().isActive()) {
                        lc.getItem().deactivate();
                    } else {
                        if (lc.getItem().activate()) {
                            streamer.addOutput(lc.getItem());
                        }
                    }
                    lv.refresh();
                }
            });

            return lc;
        });

        tPane.getSelectionModel().selectedIndexProperty().
                addListener(( ObservableValue<? extends Number> obj, Number old, Number nw ) ->
                {
                    if (nw.intValue() == 3 && (spotifyMaster == null || spotifyMaster.
                            canAuthorize())) {
                        tPane.getSelectionModel().clearAndSelect(old.intValue());
                        handleSpotifyIntegration();
                    } else if (nw.intValue() == 3 && (spotifyMaster == null || spotifyMaster.
                            getStatus().get() == LoginState.AUTHORIZING)) {
                        tPane.getSelectionModel().clearAndSelect(old.intValue());
                    }
                });
        spotDatView.
                setPlaceholder(new VBox(new HBox(new ProgressIndicator(-1), new Label("Acquiring data..."))));
        postInit();
    }
}
