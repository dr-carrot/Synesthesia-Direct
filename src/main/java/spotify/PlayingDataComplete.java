/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotify;

import com.wrapper.spotify.model_objects.specification.Track;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author 'Aaron Lomba'
 */
public class PlayingDataComplete {
    private SimpleObjectProperty<Track> track = new SimpleObjectProperty<>();
}
