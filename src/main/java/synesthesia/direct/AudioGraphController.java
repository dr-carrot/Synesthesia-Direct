/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.collections.ArrayChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPortException;
import output.Output;
import streaming.Streamer;

/**
 * FXML Controller class
 *
 * @author 'Aaron Lomba'
 */
public class AudioGraphController implements Initializable {

    @FXML
    private ScatterChart<String, Float> chart;
    @FXML
    private Path linePath;
    private Chart genChart;
    private OutputHook hook;
    private Stage stage;
    private Streamer streamer;
    private boolean isUpdating = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setup(Stage st) {

        //hook = new OutputHook();
        stage = st;
        st.setOnCloseRequest(( a ) -> {
            if (hook != null) {

            }
        });

    }

    private Task<List<? extends PathElement>> execThread(final ObservableFloatArray vals, int from, int to){
        Task<List<? extends PathElement>> thr1 = new Task<List<? extends PathElement>>() {
            @Override
            protected List<? extends PathElement> call() throws Exception {
                List<PathElement> lst = new ArrayList<>();
                //linePath.getElements().clear();
                lst.add(new MoveTo(0, 0));
                for (int i = from; i < vals.size() - 2 && i < to - 1; i += 2) {
                    lst.add(new LineTo(i, (vals.get(i + 1) + vals.get(i)) / 2f));
                    //System.out.println(vals.get(i));
                    //if (vals.get(i) >= 0) {
                    //data.add(new XYChart.Data(Integer.toString(i), vals.get(i)));
                    //}
                }
                return lst;

            }

        };
        return thr1;
    }
    
    private void asyncUpdateGraph(final ObservableFloatArray vals) {
        //System.out.println("updataing graph");
        //XYChart.Series<Integer, Float> series = new XYChart.Series();
        //XYChart.Series<String, Float> series = chart.getData().get(0);
        //ObservableList<XYChart.Data<String, Float>> data = FXCollections.observableArrayList();
//        linePath.getElements().clear();
//        linePath.getElements().add(new MoveTo(0, 0));
//        for (int i = 0; i < vals.size() / 8; i += 2) {
//            linePath.getElements().add(new LineTo(i, (vals.get(i + 1) + vals.get(i)) / 2f));
//            //System.out.println(vals.get(i));
//            //if (vals.get(i) >= 0) {
//            //data.add(new XYChart.Data(Integer.toString(i), vals.get(i)));
//            //}
//        }
        //series.getData().setAll(data);
        Thread[] threads = new Thread[4];
        Task<List<? extends PathElement>>[] tasks = new Task[4];
        for(int i = 0; i < threads.length; i++){
            tasks[i] = execThread(vals, i * 128, (i + 1) * 128);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }
        linePath.getElements().clear();
        linePath.getElements().add(new MoveTo(0, 0));
        for(int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
                linePath.getElements().addAll(tasks[i].get());
                PathElement pe = tasks[i].get().get(tasks[i].get().size() - 1);
                linePath.getElements().add(new MoveTo(((LineTo)pe).getX(), ((LineTo)pe).getX()));
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(AudioGraphController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
//        thr1.setOnSucceeded(
//                ( WorkerStateEvent a ) -> {
//                    isUpdating = false;
//                    try {
//                        linePath.getElements().setAll(thr1.get());
//                    } catch (InterruptedException | ExecutionException ex) {
//                        Logger.getLogger(AudioGraphController.class.getName()).log(Level.SEVERE,
//                                null, ex);
//                    }
//                }
//        );
//        
//        thr1.setOnFailed(
//                ( a ) -> {
//                    isUpdating = false;
//                    thr1.getException().printStackTrace();
//                }
//        );
//        if (!isUpdating) {
//            Thread th = new Thread(thr1);
//            th.start();
//
//        }

        //series.getData().clear();
        //series.getData().addAll(vals.parallelStream().map(fl -> new XYChart.Data<>("a", fl)).collect(Collectors.toList()));
        //chart.getData().setAll(series);
    }

    private Output openOutput() {
        hook = new OutputHook();
        return hook;
    }

    void setup(Stage stg, Streamer streamer) {
        this.stage = stg;

        this.streamer = streamer;
        OutputHook graphHook = new OutputHook();
        streamer.addOutput(graphHook);
        hook = graphHook;
        graphHook.activate();
        try {
            graphHook.getPort().openPort();
        } catch (SerialPortException ex) {
            System.err.println("not a pseudo port!");
        }

        
        chart.setTitle("yes");
        chart.setAnimated(false);
        XYChart.Series<String, Float> ser = new XYChart.Series();
        ser.setName("data");
        chart.getData().add(ser);
        //chart.getXAxis().setAutoRanging(false);
        //ser.setData();
        //chart.set

        stage.setOnCloseRequest(( WindowEvent e ) -> {
            streamer.removeOutput(hook);
        });

        graphHook.getValues().addListener(( ObservableFloatArray arr, boolean sizeChanged, int from, int to ) -> {
            //try {
                //Thread.sleep(1000);
            //} catch (InterruptedException ex) {
            //    Logger.getLogger(AudioGraphController.class.getName()).log(Level.SEVERE, null, ex);
            //}
            asyncUpdateGraph(arr);
        });

    }
}
