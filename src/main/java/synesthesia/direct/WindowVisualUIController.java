/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import output.Output;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class WindowVisualUIController extends AbstractVisualWindowController {
    @FXML
    private Rectangle colorBox;
    private Output output;
    private Stage stage;
    
    public void setup(Output item, Stage stage){
        item.color.addListener((a, old, nw)->{
            colorBox.setFill(nw);
        });
        //colorBox.fillProperty().bind(item.color);
        output = item;
        this.stage = stage;
        colorBox.widthProperty().bind(stage.widthProperty());
        colorBox.heightProperty().bind(stage.heightProperty());
        
        MenuItem mi = new MenuItem();
        
        stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE));
        stage.setFullScreenExitHint("Press Esc to exit");
        mi.setOnAction((e)->{
            stage.setFullScreen(!stage.isFullScreen());
        });
        
        /*stage.getScene().setOnKeyPressed((KeyEvent ke)->{
            if(ke.getCode() == KeyCode.ESCAPE && stage.isFullScreen()){
                stage.setFullScreen(false);
            }
        });*/
        
        ContextMenu cm = new ContextMenu(mi);
        colorBox.setOnContextMenuRequested((ContextMenuEvent e)->{
            mi.setText(stage.isFullScreen() ? "Windowed" : "Fullscreen");
            cm.show(colorBox, e.getScreenX(), e.getScreenY());
            
        });
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //colorBox.
        // TODO
        
        
        
    }    
    
}
