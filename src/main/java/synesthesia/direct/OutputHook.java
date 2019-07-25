/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import effects.Algorithm;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableList;
import jssc.SerialPortException;
import output.Output;
import output.Port;
import output.PseudoPort;

/**
 *
 * @author 'Aaron Lomba'
 */
public class OutputHook extends Output{
    private ObservableFloatArray values = FXCollections.observableFloatArray();

    public ObservableFloatArray getValues() {
        return values;
    }

    public OutputHook(String name, Port port, Algorithm.algorithms a) {
        super(name, port, a);
    }
    
    public OutputHook(){
        super("Graph", new PseudoPort("Graph"), Algorithm.algorithms.FFT);
    }
    
    @Override
    public int[] preprocess(float[] vals){
        //TODO loop unrolling
        float[] n = Algorithm.fft(vals);
        //Float[] fl = new Float[vals.length];
        //for(int i = 0; i < vals.length; i++){
        //    fl[i] = n[i];
        //}
        //ObservableFloatArray ofla = FXCollections.observableFloatArray(vals);

        //System.out.println("got data");
        values.setAll(n);
        return null;
    }
    public int[] preprocess(byte[] in) throws SerialPortException {
        System.out.println("fuck");
        return null;
    }
    
    public OutputHook(Output o){
        this();
    }

    @Override
    public String getVisualizerFXML() {
        return null;
    }
    
}
