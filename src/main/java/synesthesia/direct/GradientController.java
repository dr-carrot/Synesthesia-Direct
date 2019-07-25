/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import gradient.Gradient;
import gradient.FullGradient;
import gradient.GradientIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import settings.Settings;
import settings.ValueConversionException;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class GradientController implements Initializable {

    @FXML
    private HBox gradBox;

    @FXML
    private AnchorPane pane;

    @FXML
    private HBox topBox;

    @FXML
    private HBox botBox;

    //@FXML
    //private AnchorPane topPane;
    @FXML
    private AnchorPane botPane;

    @FXML
    private VBox gradV;

    @FXML
    private ColorPicker leftPkr;
    @FXML
    private ColorPicker rightPkr;

    private Stage stage;
    private FullGradient bigGrad = new FullGradient();
    private boolean movingSlider = false;
    private List<Slider> sliders = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleEvenOut() {
        double sz = 1.0 / bigGrad.getGradients().size();
        for (Gradient g : bigGrad.getGradients()) {
            g.setSize(sz);
            redrawGradient();
        }
    }

    public void setup(Stage stage, FullGradient f) {
        this.stage = stage;
        if (f != null) {
            bigGrad = f;
        }

        leftPkr.getCustomColors().addAll(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA);
        rightPkr.getCustomColors().setAll(leftPkr.getCustomColors());

        rightPkr.setOnAction(( ae ) -> {
            //System.out.println("right action");
            leftPkr.getCustomColors().setAll(rightPkr.getCustomColors());
        });

        leftPkr.setOnAction(( ae ) -> {
            //System.out.println("left action");
            rightPkr.getCustomColors().setAll(leftPkr.getCustomColors());
        });

        stage.setMinWidth(737);
        stage.setMinHeight(491);
        try {

            stage.setWidth(Settings.getInt("ui.gradient.width"));
            stage.setHeight(Settings.getInt("ui.gradient.height"));

        } catch (ValueConversionException ex) {

        }

        stage.widthProperty().addListener(( a, b, c ) -> {
            redrawGradient();
        });
        stage.widthProperty().addListener(( a ) -> {
            redrawGradient();
        });

        stage.setOnCloseRequest(( a ) -> {
            System.out.println("closing");

        });

    }

    @FXML
    private void handleClear() {
        bigGrad = new FullGradient();

        redrawGradient();
    }

    @FXML
    private void handleClose() {
        if (!stage.isMaximized()) {
            Settings.pushSetting("ui.gradient.width", stage.getWidth());
            Settings.pushSetting("ui.gradient.height", stage.getHeight());
        }
        stage.close();
    }

    @FXML
    private void handleOK() {
        if (!stage.isMaximized()) {
            Settings.pushSetting("ui.gradient.width", stage.getWidth());
            Settings.pushSetting("ui.gradient.height", stage.getHeight());
        }
        stage.close();
    }

    private void redrawGradientOnly() {
        if (gradV.getChildren().size() != 3) {
            System.err.println("not enough children!");

            return;
        }

        Rectangle newRect = (Rectangle) gradV.getChildren().get(1);
        //newRect.setWidth(648);
        //newRect.setHeight(140);

        final ArrayList<Stop> stops = new ArrayList<>();
        double d = 0;

        for (int i = 0; i < bigGrad.getGradients().size(); i++) {
            Gradient gradient = bigGrad.getGradients().get(i);
            stops.add(new Stop(d, gradient.getColor1()));
            d += gradient.getSize();
            stops.add(new Stop(d, gradient.getColor2()));
        }

        newRect.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
        if (gradV.getChildren().size() == 3) {
            gradV.getChildren().remove(1);
        }
        gradV.getChildren().add(1, newRect);
    }

    private void redrawGradient() {

        topBox.getChildren().clear();
        botBox.getChildren().clear();
        sliders.clear();
        if (bigGrad.getGradients().isEmpty()) {
            if (gradV.getChildren().size() == 3) {
                gradV.getChildren().remove(1);
            }
            //    Rectangle newRect = new Rectangle();
            //newRect.setWidth(gradV.getWidth());
            //newRect.setHeight(gradV.getHeight() - topBox.getHeight() - botBox.getHeight() - 10);
            return;
        }
        if (gradV.getChildren().size() == 3) {
            gradV.getChildren().remove(1);
        }

        Rectangle newRect = new Rectangle();
        newRect.setWidth(gradV.getWidth());
        newRect.setHeight(gradV.getHeight() - topBox.getHeight() - botBox.getHeight() - 10);

        MenuItem mvL = new MenuItem("Move left <-");
        MenuItem mvR = new MenuItem("Move right ->");
        MenuItem del = new MenuItem("Delete");

        newRect.setOnMouseClicked(( MouseEvent me ) -> {
            //System.out.println("m");
            if (me.getButton() == MouseButton.SECONDARY) {
                //System.out.println("h");
                ContextMenu cm = new ContextMenu();
                Gradient g = bigGrad.gradientAt(me.getX() / newRect.getWidth());
                int c = bigGrad.getGradients().indexOf(g);
                mvL.setOnAction(( a ) -> {
                    bigGrad.swap(g, bigGrad.getGradients().get(c - 1));
                    redrawGradient();
                });
                mvR.setOnAction(( a ) -> {
                    bigGrad.swap(g, bigGrad.getGradients().get(c + 1));
                    redrawGradient();
                });

                del.setOnAction(( a ) -> {
                    bigGrad.remove(g);
                    if (bigGrad.getGradients().isEmpty()) {
                        gradV.getChildren().remove(1);
                    }
                    redrawGradient();
                });

                if (c > 0) {
                    cm.getItems().add(mvL);
                    mvL.setVisible(true);
                } else {
                    mvL.setVisible(false);
                }
                if (c < bigGrad.getGradients().size() - 1) {
                    cm.getItems().add(mvR);
                    mvR.setVisible(true);
                } else {
                    mvR.setVisible(false);
                }
                cm.getItems().add(del);

                cm.show(newRect, me.getScreenX(), me.getScreenY());

            }
        });

        mvL.setOnAction(( ActionEvent a ) -> {

            bigGrad.gradientAt(0);
        });

        final ArrayList<Stop> stops = new ArrayList<>();
        double d = 0;
        Rectangle lSpacer = new Rectangle(bigGrad.getGradients().get(0).getSize() * newRect.getWidth(), 1);
        topBox.getChildren().add(lSpacer);
        lSpacer.setOpacity(0);

        boolean top = false;
        for (int i = 0; i < bigGrad.getGradients().size(); i++) {
            Gradient gradient = bigGrad.getGradients().get(i);
            stops.add(new Stop(d, gradient.getColor1()));
            d += gradient.getSize();
            stops.add(new Stop(d, gradient.getColor2()));

            if (i < bigGrad.getGradients().size() - 1) {
                Gradient nxt = bigGrad.getGradients().get(i + 1);
                Slider s = new Slider();
                s.setMin(d - gradient.getSize());
                s.setMax(d + nxt.getSize());
                s.adjustValue(d);
                s.setPrefWidth(gradient.getSize() * newRect.getWidth() + nxt.getSize() * newRect.getWidth());
                sliders.add(s);
                //gradient.sizeProperty().bind(s.widthProperty().divide(648.0));

                //final int m = i;
                Tooltip t = new Tooltip();
                s.setTooltip(t);
                t.textProperty().bind(s.valueProperty().asString("%.5s"));

                s.valueChangingProperty().addListener(( ObservableValue<? extends Boolean> a, Boolean old, Boolean nw ) -> {
                    if (nw) {
                        if (!t.isShowing()) {
                            t.show(stage);
                        }
                    } else {
                        t.hide();
                    }
                });

                s.valueProperty().addListener(( ObservableValue<? extends Number> a, Number old, Number nw ) -> {

                    //Point mouse = java.awt.MouseInfo.getPointerInfo().getLocation();
                    //Point2D local = s.screenToLocal(mouse.x, mouse.y);
                    //t.setText(Double.toString(nw.doubleValue()).substring(0, 5));
                    //t.setX(mouse.x);
                    //t.setY(mouse.y);
                    double diff = nw.doubleValue() - old.doubleValue();
                    //System.out.print(gradient.getSize());
                    gradient.setSize(gradient.getSize() + diff);
                    //System.out.println(" " + gradient.getSize());
                    nxt.setSize(nxt.getSize() - diff);
                    redrawGradientOnly();
                    /*
                     *
                     * if (m < sliders.size() - 1) {
                     *
                     * Slider ns = sliders.get(m + 1);
                     *
                     * //ns.
                     * //double r = ns.getValue();
                     * ns.setPrefWidth(ns.getWidth() + diff);
                     * ns.setMin(nw.doubleValue());
                     *
                     * //ns.adjustValue(r);
                     * }
                     *
                     * if (m > 0) {
                     * Slider ls = sliders.get(m - 1);
                     * //double r = ls.getValue();
                     * ls.setPrefWidth(ls.getWidth() + diff);
                     * ls.setMax(nw.doubleValue());
                     * //ls.adjustValue(r);
                     * } else {
                     * ((Rectangle)
                     * topBox.getChildren().get(0)).setWidth(sliders.get(0).getValue()
                     * * 648);
                     * }
                     * movingSlider = false;
                     */

                });

                if (top) {
                    topBox.getChildren().add(s);
                } else {
                    botBox.getChildren().add(s);
                }
                top = !top;
            }
        }

        if (sliders.size() > 1) {
            lSpacer.widthProperty().bind(sliders.get(0).valueProperty().multiply(newRect.widthProperty()));
            sliders.get(0).maxProperty().bind(sliders.get(1).valueProperty());
            sliders.get(0).prefWidthProperty().bind(sliders.get(1).valueProperty().multiply(newRect.widthProperty()));
        }

        for (int i = 1; i < sliders.size() - 1; i++) {
            sliders.get(i).minProperty().bind(sliders.get(i - 1).valueProperty());
            sliders.get(i).maxProperty().bind(sliders.get(i + 1).valueProperty());
            sliders.get(i).prefWidthProperty().bind(sliders.get(i + 1).valueProperty().subtract(sliders.get(i - 1).valueProperty()).multiply(newRect.widthProperty()));

        }

        if (sliders.size() > 1) {
            sliders.get(sliders.size() - 1).minProperty().bind(sliders.get(sliders.size() - 2).valueProperty());
            sliders.get(sliders.size() - 1).prefWidthProperty().bind(sliders.get(sliders.size() - 2).valueProperty().negate().add(1).multiply(newRect.widthProperty()));
        }

        //stops.add(new Stop(0, pkr.getValue()));
        //stops.add(new Stop(1, pkr2.getValue()));
        newRect.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
        if (gradV.getChildren().size() == 3) {
            gradV.getChildren().remove(1);
        }
        gradV.getChildren().add(1, newRect);
    }

    @FXML
    private void handleAddSection(Event e) {

        Gradient gr = new Gradient(leftPkr.getValue(), rightPkr.getValue(), 0.3);
        bigGrad.appendGrad(gr);
        //Slider tSlider = new Slider();
        //tSlider.setMax(1);
        //tSlider.setValue(1);
        //topPane.getChildren().add(tSlider);
        redrawGradient();

        //AnchorPane.setTopAnchor(tSlider, 0.0);
        //AnchorPane.setLeftAnchor(tSlider, 0.0);
        //AnchorPane.setRightAnchor(tSlider, 0.0);
        leftPkr.setValue(rightPkr.getValue());
        rightPkr.setValue(Color.WHITE);

        //newRect.heightProperty().bind(gradBox.heightProperty().subtract(anchor.radiusProperty()));
    }

    @FXML
    private void saveGradient() {

        File dir = new File(Settings.getString("syn.last_dir", Settings.docDir + "gradients"));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(dir);
        fc.getExtensionFilters().add(new ExtensionFilter("Gradient file", "*.grd"));
        fc.setTitle("Where to save the file?");
        File f = fc.showSaveDialog(stage);
        if (f != null) {
            try {
                GradientIO.saveGradient(bigGrad, f);
            } catch (IOException ex) {
                System.err.println("caould not write gradient!");
                Settings.pushSetting("syn.last_dir", f.getParentFile().getPath());
                ex.printStackTrace();
                //Logger.getLogger(GradientController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void loadGradient() {
        File dir = new File(Settings.getString("syn.last_dir", Settings.docDir + "gradients"));

        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(dir);
        fc.setTitle("Choose gradient file");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Gradient", "*.grd"),
                new ExtensionFilter("Any", "*")
        );
        File f = fc.showOpenDialog(stage);

        if (f != null) {
            try {
                bigGrad = GradientIO.loadGradient(f);
                Settings.pushSetting("syn.last_dir", f.getParentFile().getPath());
                redrawGradient();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }

    }

    public FullGradient getFullGradient() {
        return bigGrad;
    }

}
