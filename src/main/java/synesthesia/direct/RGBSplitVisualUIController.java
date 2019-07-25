/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import output.Output;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class RGBSplitVisualUIController extends AbstractVisualWindowController{
    @FXML
    private Ellipse red;
    @FXML
    private Ellipse green;
    @FXML
    private AnchorPane background;
    @FXML
    private Rectangle rect;
    @FXML
    private Ellipse blue;
    private Stage stage;
    private Output item;
    private boolean whitePower = true;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void setup(Output item, Stage stage) {
        this.stage = stage;
        this.item = item;
        item.color.addListener((ObservableValue<? extends Color> o, Color old, Color nw)->{
            
            red.setFill(Color.color(nw.getRed(), 0, 0));
            green.setFill(Color.color(0, nw.getGreen(), 0));
            blue.setFill(Color.color(0, 0, nw.getBlue()));
            
        });
    }
    
    @FXML
    private void swapBackground(){
        whitePower = !whitePower;
        if(whitePower){
            rect.setFill(Color.WHITE);
        }else{
            rect.setFill(Color.BLACK);
        }
        
    }
}
