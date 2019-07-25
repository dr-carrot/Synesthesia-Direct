/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import effects.Algorithm;
import effects.Algorithm.algorithms;
import effects.FX;
import effects.Processor;
import gradient.FullGradient;
import gradient.Gradient;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import output.Output;
import output.Port;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class OutputConfigUIController implements Initializable {

    @FXML
    private TextField smootherFieldx;
    @FXML
    private Slider smootherSliderx;
    @FXML
    private TextField jumperFieldx;
    @FXML
    private Slider jumperSliderx;
    @FXML
    private TextField factorFieldx;
    @FXML
    private Slider factorSliderx;
    @FXML
    private TextField trailChar;
    @FXML
    private ChoiceBox<Algorithm.algorithms> algoChooser;
    @FXML
    private ChoiceBox outputType;
    @FXML
    private Label trailText;
    @FXML
    private Rectangle gradientBox;
    

    private Processor processor;
    private FX fx;
    private Output output;
    private Stage stage;
    
    @FXML
    private void handleLoadFX(){
        
    }
    
    @FXML
    private void handleSaveFX(){
        
    }
    
    private void handleSaveSpotifyFX(){
        
    }
    
    @FXML
    private void handleOpenGradientEditor(){
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
            ctrlr.setup(stg, fx.getGradient());
            stg.showAndWait();
            FullGradient fg = ctrlr.getFullGradient();
            if (fg != null) {
                fx.setGradient(fg);
                drawGradRect();
                
            }

            //System.out.println("out");
            //songTable.
        } catch (Exception h) {
            //ErrorHandler.standard(h);
            h.printStackTrace();
        }
    }
    
    private void drawGradRect(){
        final ArrayList<Stop> stops = new ArrayList<>();
        double d = 0;
        //Rectangle lSpacer = new Rectangle(fg.getGradients().get(0).getSize() * gradBox.getWidth(), 1);
        //lSpacer.setOpacity(0);
        //boolean top = false;
        for (Gradient gradient : fx.getGradient().getGradients()) {
            stops.add(new Stop(d, gradient.getColor1()));
            d += gradient.getSize();
            stops.add(new Stop(d, gradient.getColor2()));
        }
        gradientBox.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
    }

    @FXML
    private void loadFXFile() {

    }
    
    

    public void loadFX(FX fx) {
        this.fx = fx;
        smootherFieldx.setText(Integer.toString(fx.getSmootherValue()));
        jumperFieldx.setText(Integer.toString(fx.getJumperValue()));
        factorFieldx.setText(Double.toString(fx.getFactorValue()));
        factorSliderx.setValue(fx.getFactorValue());
    }

    @FXML
    private void handleApply() {
        

        saveFX();
        try {
            int v = Integer.parseInt(trailChar.getText());
            if (v < 128) {
                output.setTrailingByte((byte) v);
            } else {
                output.setTrailingByte((byte) (v - 256));
            }
        } catch (NumberFormatException e) {
        }

        output.setAlgo(algoChooser.getSelectionModel().getSelectedItem());
        if (!trailChar.getText().isEmpty()) {
            output.setTrailingCharacter(trailChar.getText().charAt(0));
        }
        if (output.getAlgo() == algorithms.FFT) {
            factorSliderx.setMax(30);
        } else {
            factorSliderx.setMax(10);
        }
    }

    @FXML
    private void handleCLose() {
        stage.close();
    }

    @FXML
    private void loadGradientFile() {

    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        algoChooser.getSelectionModel().selectedItemProperty().addListener(( ObservableValue<? extends algorithms> obj, algorithms old, algorithms nw ) -> {
            if (nw.equals(algorithms.OG)) {
                factorSliderx.setMax(6);
            } else if (nw.equals(algorithms.FFT)) {
                factorSliderx.setMax(10);
            } else {
                factorSliderx.setMax(10);
            }

            if (old != null) {
                if (old.equals(algorithms.OG) && nw.equals(algorithms.FFT)) {
                    double ov = Double.parseDouble(factorFieldx.getText());
                    factorFieldx.setText(Double.toString(ov * 2));
                } else if (old.equals(algorithms.FFT) && nw.equals(algorithms.OG)) {
                    double ov = Double.parseDouble(factorFieldx.getText());
                    factorFieldx.setText(Double.toString(ov / 2));
                }
            }

        });

        smootherFieldx.textProperty().bindBidirectional(smootherSliderx.valueProperty(), new StringConverter<Number>() {

            @Override
            public String toString(Number t) {
                return Integer.toString(t.intValue());
            }

            @Override
            public Number fromString(String string) {
                if (!isDouble(string)) {
                    return 0;
                }
                return Double.parseDouble(string);
            }
        });

        jumperFieldx.textProperty().bindBidirectional(jumperSliderx.valueProperty(), new StringConverter<Number>() {

            @Override
            public String toString(Number t) {
                return Integer.toString(t.intValue());
            }

            @Override
            public Number fromString(String string) {
                if (!isDouble(string)) {
                    return 0;
                }
                return (int) Double.parseDouble(string);
            }
        });

        factorFieldx.textProperty().bindBidirectional(factorSliderx.valueProperty(), new StringConverter<Number>() {

            @Override
            public String toString(Number t) {
                double dVal = t.doubleValue() * 100.0;
                double out = Math.round(dVal) / 100.0;
                //System.out.println(out);
                return Double.toString(out);
            }

            @Override
            public Number fromString(String string) {
                if (!isDouble(string)) {
                    return 0;
                }
                return Math.round(Double.parseDouble(string) * 100) / 100;
            }
        });

        // TODO
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void saveFX() {
        //fx.setName(name);
        fx.setFactorValue(Double.parseDouble(factorFieldx.getText()));
        fx.setJumperValue((int) Double.parseDouble(jumperFieldx.getText()));
        fx.setSmootherValue(Integer.parseInt(smootherFieldx.getText()));
    }

    public void setup(Output item, Stage stage) {
        if (item.getAlgo() == algorithms.FFT) {
            factorSliderx.setMax(30);
        }
        this.stage = stage;
        loadFX(item.getFX());
        output = item;
        trailChar.setText(Character.toString(item.getTrailingCharacter()));

        algoChooser.getItems().addAll(Algorithm.algorithms.values());
        if (output.getAlgo() == null) {
            algoChooser.getSelectionModel().clearAndSelect(0);
        } else {
            algoChooser.getSelectionModel().clearAndSelect(algoChooser.getItems().indexOf(output.getAlgo()));
        }

        if (item.getPort().getType() == Port.types.Bytes) {
            int x = item.getTrailingByte();
            if (x >= 0) {
                trailChar.setText(Integer.toString(x));
            } else {
                trailChar.setText(Integer.toString(x + 256));
            }
            trailText.setText("Trailing Byte");

            trailChar.textProperty().addListener(( ObservableValue<? extends String> o, String old, String nw ) -> {
                if (nw.length() > 3) {
                    trailChar.setText(old);
                }
                if (!trailChar.getText().isEmpty()) {
                    try {
                        int v = Integer.parseInt(nw);
                        if (v > 255 || v < 0) {
                            trailChar.setText(old);
                        }

                    } catch (NumberFormatException e) {
                        trailChar.setText(old);
                    }
                }

            });
        } else if (item.getPort().getType() == Port.types.Default) {
            trailChar.textProperty().addListener(( ObservableValue<? extends String> o, String old, String nw ) -> {
                if (!nw.contains("\\n") && !nw.contains("\\r")) {
                    try {
                        nw.charAt(1);
                        trailChar.setText(old);
                    } catch (Exception e) {

                    }
                }
            });
        }
        drawGradRect();

    }

}
