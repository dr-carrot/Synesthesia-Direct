/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import gradient.Gradient;
import gradient.FullGradient;
import effects.simple.Effect;
import effects.simple.EffectManager;
import effects.simple.GradEffect;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import output.Output;
import output.Port;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class RGBEffectControllerController implements Initializable {

    private Stage stage;
    private Output output;
    private FullGradient fg = null;

    @FXML
    private Rectangle gradBox;
    @FXML
    private CheckBox reverse;
    @FXML
    private TextField timeField;
    @FXML
    private Label termLabel;
    @FXML
    private TextField termField;
    //private Port.types.

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /*
         * termLabel.setOnMouseClicked(( a ) -> {
         *
         * if (output.getPort().getType() == Port.types.Default) {
         * output.getPort().setType(Port.types.Bytes);
         * termLabel.setText("Trailing Byte:");
         * } else {
         * output.getPort().setType(Port.types.Default);
         * termLabel.setText("Trailing Char:");
         * }
         * });
         *
         */
        // TODO
    }

    public void modeSwap() {

    }

    @FXML
    private void handleLoadGradient() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/GradientControllerGUI.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stg = new Stage();
            stg.initOwner(stage);
            GradientController ctrlr = (GradientController) fxmlLoader.getController();
            //ctrlr.set((Song) getTable().getSelectionModel().getSelectedItem(), stg, mainLib, this);
            //stg.initModality(Modality.NONE);
            //stg.initStyle(StageStyle.DECORATED);
            //stg.in
            stg.setTitle("Gradient Editor <3");
            stg.setScene(new Scene(root1));
            stg.setResizable(true);
            ctrlr.setup(stg, fg);
            stg.showAndWait();
            fg = ctrlr.getFullGradient();
            if (fg != null) {
                drawRect();
            }

            //System.out.println("out");
            //songTable.
        } catch (Exception h) {
            //ErrorHandler.standard(h);
            h.printStackTrace();
        }
    }

    private void drawRect() {
        final ArrayList<Stop> stops = new ArrayList<>();
        double d = 0;
        //Rectangle lSpacer = new Rectangle(fg.getGradients().get(0).getSize() * gradBox.getWidth(), 1);
        //lSpacer.setOpacity(0);
        //boolean top = false;
        for (Gradient gradient : fg.getGradients()) {
            stops.add(new Stop(d, gradient.getColor1()));
            d += gradient.getSize();
            stops.add(new Stop(d, gradient.getColor2()));
        }
        gradBox.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
    }

    @FXML
    private void handleClose(){
        stage.close();
    }
    
    @FXML
    private void handleApply() {
        //EffectManager
        if (output.getPort().getType() == Port.types.Bytes) {
            try {
                int v = Integer.parseInt(termField.getText());
                if (v < 128) {
                    output.setTrailingByte((byte) v);
                } else {
                    output.setTrailingByte((byte) (v - 256));
                }
            } catch (NumberFormatException e) {
            }
        } else {
            if (!termField.getText().isEmpty()) {
                System.out.println("using " + termField.getText().charAt(0));
                output.setTrailingCharacter(termField.getText().charAt(0));
            }
        }
        System.out.println("adding effect");
        Effect eff = new GradEffect(output, fg, Long.parseLong(timeField.getText()), reverse.isSelected());
        
        eff.prep();
        EffectManager.submitEffect(eff);
        System.out.println("done");
        stage.close();
    }

    public void setup(Output item, Stage s) {
        output = item;
        stage = s;
        //item.setVisualWindowStage(stage);

        if (output.getPort().getType() == Port.types.Bytes) {
            int x = item.getTrailingByte();
            if (x >= 0) {
                termField.setText(Integer.toString(x));
            } else {
                termField.setText(Integer.toString(x + 256));
            }
            termLabel.setText("Trailing Byte:");

            termField.textProperty().addListener(( ObservableValue<? extends String> o, String old, String nw ) -> {
                if (nw.length() > 3) {
                    termField.setText(old);
                }
                if (!termField.getText().isEmpty()) {
                    try {
                        int v = Integer.parseInt(nw);
                        if (v > 255 || v < 0) {
                            termField.setText(old);
                        }

                    } catch (NumberFormatException e) {
                        termField.setText(old);
                    }
                }

            });
        } else if (item.getPort().getType() == Port.types.Default) {
            termLabel.setText("Trailing Char:");
            termField.textProperty().addListener(( ObservableValue<? extends String> o, String old, String nw ) -> {
                if (!nw.contains("\\n") && !nw.contains("\\r")) {
                    try {
                        nw.charAt(1);
                        termField.setText(old);
                    } catch (Exception e) {

                    }
                }
            });
        }
        
        Effect e = EffectManager.getMyEffect(output);
        if (e != null){
            GradEffect g = (GradEffect)e;
            fg = g.getGradient();
            timeField.setText(Long.toString(g.getTime()));
            reverse.setSelected(g.useReverse());
            if(fg != null){
                drawRect();
            }
        }
    }

}
