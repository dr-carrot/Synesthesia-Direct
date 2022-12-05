/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotify;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.util.Pair;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Image;
import synesthesia.direct.TableViewPair;

/**
 *
 * @author 'Aaron Lomba'
 */
public class PlayingData {

    private static final String[] KEYS = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private int albIdx = 0;
    private ObjectProperty<AudioAnalysis> analysis = new SimpleObjectProperty<>();
    private ObjectProperty<AudioFeatures> features = new SimpleObjectProperty<>();
    private ObjectProperty<Image[]> images = new SimpleObjectProperty<>();
    private BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private StringProperty playingAlbum = new SimpleStringProperty("no data");
    private ObjectProperty<List<String>> playingArtists = new SimpleObjectProperty();
    private ObjectProperty<CurrentlyPlayingContext> playingContext = new SimpleObjectProperty<>();
    private StringProperty playingId = new SimpleStringProperty("no data");
    private StringProperty playingTrack = new SimpleStringProperty("no data");

    public boolean equals(Object o) {
        if (o instanceof PlayingData) {
            PlayingData pd = (PlayingData) o;
            return playingId.get().equals(pd.getPlayingId().get());// && isPlaying.get() == pd.getIsPlaying().get();
        }
        return super.equals(o);
    }

    public ObjectProperty<AudioAnalysis> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(ObjectProperty<AudioAnalysis> analysis) {
        this.analysis = analysis;
    }

    public List<TableViewPair> getBasicAnalysis() {
        List<TableViewPair> out = new ArrayList<>();
        out.add(new TableViewPair("Duration", analysis.get().getTrack().
                getDuration(), "The duration in seconds"));
        out.add(new TableViewPair("End of fade in", analysis.get().getTrack().
                getEndOfFadeIn(), "The end of fade in introduction of the track."));
        out.
                add(new TableViewPair("Start of fade out", analysis.get().
                        getTrack().getStartOfFadeOut(), "The start of the fade out in seconds."));
        out.
                add(new TableViewPair("Key", KEYS[analysis.get().getTrack().
                        getKey()], "The estimated main key of the track.", analysis.
                                get().getTrack().getKeyConfidence()));
        //out.add(new TableViewPair("Key confidence", analysis.get().getTrack().getKeyConfidence()));
        out.add(new TableViewPair("Loudness", analysis.get().getTrack().
                getLoudness(), "The average loudness of the track in decibels. These values are mostly in a range between -60 and 0 decibels."));
        //out.add(new TableViewPair("Modality", analysis.get().getTrack().getMode()));
        out.
                add(new TableViewPair("Number of samples", analysis.get().
                        getTrack().getNumSamples(), "The number of samples in the track. The total number of samples is\ncalculated by multiplying the duration of the track with the sample rate."));
        out.add(new TableViewPair("Offset seconds", analysis.get().getTrack().
                getOffsetSeconds()));
        out.
                add(new TableViewPair("Tempo", analysis.get().getTrack().
                        getTempo(), "The estimated tempo of the track in beats per minute.", analysis.
                                get().getTrack().getTempoConfidence()));
        //out.add(new TableViewPair("Time signature",analysis.get().getTrack().getTimeSignature()));
//analysis.get().getTrack().
        return out;
    }

    public List<TableViewPair> getBasicFeatures() {
        List<TableViewPair> out = new ArrayList<>();
        out.add(new TableViewPair("Acousticness", features.get().
                getAcousticness(), "The acousticness of the track as a value between 0.0 and 1.0. The higher the value, the higher the chance the track is acoustic."));
        out.add(new TableViewPair("Dancability", features.get().
                getDanceability(), "The danceability of the track as a value between 0.0 and 1.0. The danceability depends on factors like tempo and rhythm stability. Higher is better."));
        out.
                add(new TableViewPair("Energy", features.get().getEnergy(), "The energy of the track as a value between 0.0 and 1.0. The energetic value\nof the track depends on factors like speed and loudness. Fast and loud tracks feel more energetic than slow and quiet tracks."));
        out.add(new TableViewPair("Instrumentalness", features.get().
                getInstrumentalness(), "The instrumentalness of the track as a value between 0.0 and 1.0. The higher the value, the higher the chance the track contains no vocals."));
        out.
                add(new TableViewPair("Liveness", features.get().getLiveness(), "The liveness of the track as a value between 0.0 and 1.0. The liveness depends on ambient\nsounds like the presence of an audience. The higher the value, the higher the chance the track was performed live."));
        out.
                add(new TableViewPair("Loudness", features.get().getLoudness(), "The average loudness of the track. These values have mostly a range between -60 and 0 decibels."));
        out.
                add(new TableViewPair("Speechiness", features.get().
                        getSpeechiness(), "The speechiness of the track as a value between 0.0 and 1.0. The higher the value, the higher the chance the track only consists of spoken words."));
        out.
                add(new TableViewPair("Modality", features.get().getMode(), "The modality of the track. (either \"major\" or \"minor\")"));
        //out.add(new TableViewPair("Tempo", features.get().getTempo()));
        out.add(new TableViewPair("Time sigature", features.get().
                getTimeSignature(), "The estimated overall time signature of the track. The time signature (or meter) is the number of beats in a bar. Example: A Viennese waltz has\na three-quarters beat, so this method would return the value 3 in this case."));
        out.
                add(new TableViewPair("Valance", features.get().getValence(), "The valence of the track as a value between 0.0 and 1.0. A track with a high valence sounds more positive\n(happy, cheerful, euphoric) like the track with a low valence. (sad, depressed, angry)"));
        return out;

    }

    public ObjectProperty<AudioFeatures> getFeatures() {
        return features;
    }

    public void setFeatures(ObjectProperty<AudioFeatures> features) {
        this.features = features;
    }

    public Image getImage(int i) {
        return images.get()[i];
    }

    public Image getImageAutomod(int albIdx) {
        return getImage(albIdx % this.images.get().length);
    }

    public ObjectProperty<Image[]> getImages() {
        return images;
    }

    public BooleanProperty getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(BooleanProperty isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the playingContext
     */
    public ObjectProperty<CurrentlyPlayingContext> getPlaying() {
        return playingContext;
    }

    /**
     * @param playing the playingContext to set
     */
    public void setPlaying(ObjectProperty<CurrentlyPlayingContext> playing) {
        this.playingContext = playing;
    }

    public StringProperty getPlayingAlbum() {
        return playingAlbum;
    }

    public void setPlayingAlbum(StringProperty playingAlbum) {
        this.playingAlbum = playingAlbum;
    }

    public void setPlayingArtist(List<String> playingArtist) {
        this.playingArtists.set(playingArtist);
    }

    public ObjectProperty<List<String>> getPlayingArtists() {
        return playingArtists;
    }

    public ObjectProperty<CurrentlyPlayingContext> getPlayingContext() {
        return playingContext;
    }

    public void setPlayingContext(ObjectProperty<CurrentlyPlayingContext> playingContext) {
        this.playingContext = playingContext;
    }

    public StringProperty getPlayingId() {
        return playingId;
    }

    public void setPlayingId(StringProperty playingId) {
        this.playingId = playingId;
    }

    public StringProperty getPlayingTrack() {
        return playingTrack;
    }

    public void setPlayingTrack(StringProperty playingTrack) {
        this.playingTrack = playingTrack;
    }

    public Image nextImage() {
        Image img = images.get()[albIdx];
        albIdx = (albIdx + 1) % images.get().length;
        return img;
    }

    public void reflect(PlayingData pd) {
        playingAlbum.set(pd.getPlayingAlbum().get());
        playingArtists.set(pd.getPlayingArtists().get());
        playingContext.set(pd.getPlaying().get());
        playingTrack.set(pd.getPlayingTrack().get());
        playingId.set(pd.getPlayingId().get());
        isPlaying.set(pd.getIsPlaying().get());
    }

}
