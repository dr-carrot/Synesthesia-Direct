/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

import effects.Algorithm;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import jssc.SerialPortException;

/**
 *
 * @author 'Aaron Lomba'
 */
public class RGBSeparate extends Output{
    public ObjectProperty<Paint> r = new SimpleObjectProperty<>(Color.BLACK);
    public ObjectProperty<Paint> g = new SimpleObjectProperty<>(Color.BLACK);
    public ObjectProperty<Paint> b = new SimpleObjectProperty<>(Color.BLACK);
    
    
    public RGBSeparate(String name, Port port, Algorithm.algorithms a) {
        super(name, port, a);
    }
    
    public RGBSeparate(String name, Port port) {
        super(name, port);
    }
    
    public int[] process(float[] n) throws SerialPortException{
        Algorithm.fft(n);
        float mi = min;
        float mx = max;
        float avg = stravg(n);
        for(float f : n){
            if(f > max)
                max = f;
            if (f < min)
                min = f;
        }
        /*
        if(mi != min){
            System.out.println("new max: " + max);
        }
        
        if(mx != max){
            System.out.println("new min: " + min);
        }*/
        float rc = Math.abs(avg) * 8;
        //System.out.println("avg: " + r);
        int[] rgb = toIntArray(doColorStuff(rc, smoother));
        color.set(Color.rgb(rgb[0], rgb[1], rgb[2]));
        r.set(Color.rgb(rgb[0], 0, 0));
        g.set(Color.rgb(0, rgb[1], 0));
        b.set(Color.rgb(0, 0, rgb[2]));
        return rgb;
    }
    

    public int[] process(byte[] n) throws SerialPortException {
        smootherCheck();
        int[] out = process_old(n);
        color.set(Color.rgb(out[0], out[1], out[2]));
        r.set(Color.rgb(out[0], 0, 0));
        g.set(Color.rgb(0, out[1], 0));
        b.set(Color.rgb(0, 0, out[2]));
        port.writeArr(out, trailing);
        //Algorithm.norm(n);
        //color.set(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        return out;
    }

    @Override
    public String getVisualizerFXML() {
        return "/fxml/RGBSplitVisualUI.fxml";
    }
}
