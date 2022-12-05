/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotify;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.TooManyRequestsException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 *
 * @author 'Aaron Lomba'
 */
public class UserStatus {

    private ObjectProperty<PlayingData> playingData = new SimpleObjectProperty<>();

    private final SpotifyMaster master;
    private Task queryer;
    private EventHandler changeHandler;
    private ChangeListener<PlayingData> listener;
    private int albIdx = 1;

    public UserStatus(SpotifyMaster master) {
        this.master = master;
        

    }
    
    public Image nextImage(){
        Image img = playingData.get().getImageAutomod(albIdx);
        albIdx++;
        return img;
    }

    public ObjectProperty<PlayingData> getplayingData() {
        return playingData;
    }

    public void setOnChangeDetected(EventHandler eh) {
        changeHandler = eh;
    }

    public void addListener(ChangeListener<PlayingData> listener) {
        this.listener = listener;
    }

    public void stopQuery() {
        queryer.cancel();
    }

    public ReadOnlyObjectProperty<PlayingData> playingDataProperty() {
        return queryer.valueProperty();
    }
    
    public void setOnEternalQueryFailed(EventHandler<WorkerStateEvent> eh){
        queryer.setOnFailed(eh);
    }

    public void eternalQuery(long queryDelay) {
        queryer = new Task<PlayingData>() {
            @Override
            protected PlayingData call() throws Exception {
                //PlayingData data = new PlayingData();
                Exception lastError = null;
                int failCount = 0;
                while (!isCancelled()) {
                    try {

                        if (failCount > 10) {
                            String msg = "Eternal query encountered too many errors and terminated!";
                            if (lastError == null) {
                                throw new Exception(msg);
                            } else {
                                throw new Exception(msg + " Last known exception: " + lastError.getMessage());
                            }

                        } else if (failCount > 7) {
                            Thread.sleep(1000);
                        }
                        PlayingData data = querySet();
                        if (playingData.get() == null) {
                            playingData.set(data);
                            updateValue(data);
                            albIdx = 1;
                        } else {
                            if (data == null) {
                                failCount++;
                            } else if (!data.getPlaying().get().getItem().getId().equals(playingData.get().getPlaying().get().getItem().getId())) {
                                playingData.set(data);
                                //playingData.get().reflect(data);
                                updateValue(data);
                                albIdx = 0;
                                //if(failCount != 0)
                                //System.out.println(data.getPlaying().get().getItem().getName());
                                //System.out.println(playingData.get().getPlaying().get().getItem().getName());
                                //System.out.println();
                                failCount = 0;

                            }
                        }
                        Thread.sleep(queryDelay);

                    } catch (TooManyRequestsException e) {
                        //e.printStackTrace();
                        System.out.println("Too many requests! resume after " + e.getRetryAfter() + " seconds");
                        Thread.sleep(e.getRetryAfter() * 1000 + 100);
                        lastError = e;
                    } catch (SpotifyWebApiException e) {
                        System.out.println("spotify error: " + e.getMessage());
                        System.out.println("failure streak: " + failCount);
                        failCount++;
                        lastError = e;
                    }
                }
                return null;

            }
        };

        Thread th = new Thread(queryer);
        th.setName("eternal query");
        th.start();

    }

    public PlayingData querySet() throws IOException, SpotifyWebApiException, ParseException {
        CurrentlyPlayingContext curr = master.getApi().getInformationAboutUsersCurrentPlayback().build().execute();
        PlayingData data = new PlayingData();
        try {
            Track trackData = master.getApi().getTrack(curr.getItem().getId()).build().execute();
            data.getPlayingTrack().set(curr.getItem().getName());
            data.getPlayingId().set(curr.getItem().getId());
            data.getPlayingAlbum().set(trackData.getAlbum().getName());
            data.getPlayingArtists().set(Arrays.stream(trackData.getArtists()).map(ArtistSimplified::getName).collect(Collectors.toList()));
            data.getIsPlaying().set(curr.getIs_playing());
            data.getPlaying().set(curr);
            //data.setImages(curr.getItem().getAlbum().getImages());

        } catch (NullPointerException e) {
            //System.err.println("query set wa null?");
            e.printStackTrace();
            throw new SpotifyWebApiException("Unable to retrieve data from spotify!");
        }
        return data;
    }

}
