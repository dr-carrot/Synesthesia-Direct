/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

import effects.Algorithm;
import effects.FX;
import effects.Processor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jssc.SerialPortException;

/**
 *
 * @author 'Aaron Lomba'
 */
public abstract class Output {
    protected Algorithm.algorithms algo = Algorithm.algorithms.OG;
    public ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.valueOf("BLACK"));
    private int currVals;
    protected FX fx = new FX();
    private float globSum;
    boolean isActive = false;
    private boolean isSynced;
    float max = -1;
    private int maxVals;
    float min = 1;
    private String name;
    private float newest = -1;
    private int oldSmoother;
    private float oldest = -1;
    protected Port port;
    private Processor processor;
    protected float[] smoother = new float[60];
    private Output syncMaster = null;
    private List<Output> syncSlaves;
    private boolean testing = false;
    char trailing;
    byte trailingByte;
    private Stage windowStage = null;
    public Output(String name, Port port, Algorithm.algorithms a) {
        this.name = name;
        this.port = port;
        algo = a;
    }
    public Output(String name, Port port) {
        this(name, port, Algorithm.algorithms.values()[0]);
    }
    public boolean activate() {
        if (!testing) {
            isActive = true;
            return true;
        }
        return false;
    }
    private byte asUnsigned(int x) {
        if (x > 255 || x < 0) {
            throw new NumberFormatException("Unsigned byte value out of bounds!");
        }
        if (x < 128) {
            return (byte) x;
        } else {
            return (byte) (x - 256);
        }
    }
    protected byte avg(byte[] bt) {
        int s = 0;
        int x = 1;
        for (byte b : bt) {
            if (Math.abs(b) > 2) {
                s += Math.abs(b);
                x++;
            }

        }
        return (byte) (s / x);
    }
    protected float avg(float[] bt) {
        float s = 0;
        float x = 1;
        for (float b : bt) {
            if (Math.abs(b) > 2) {
                s += Math.abs(b) / 128;
                x++;
            }

        }
        return (s / x);
    }
    private int[] bound8Conv(float val) {
        float v = (float) Math.min(val, 7.9);
        float r = 0;
        float g = 0;
        float b = 0;
        int rgb[] = new int[3];
        if (v < .1) {
        } else if (v < 1) {//black to magenta
            r = v;
            b = v;
            //1.2, 1.3, 1.9
        } else if (v < 2) {//magenta to blue
            b = 1;
            r = -(v - 2);
        } else if (v < 3) {//blue to cyan
            g = (v - 2);
            b = 1;
        } else if (v < 4) {//cyan to green
            g = 1;
            b = -(v - 4);
        } else if (v < 5) {//green to yellow
            g = 1;
            r = (v - 4);
        } else if (v < 6) {//yellow to red
            r = 1;
            g = -(v - 6);
        } else if (v < 8) {//red to white
            r = 1;
            g = v - 6;
            b = (v - (float) 5.5);
        }
        
        rgb[0] = Math.min((int) (255 * r), 255);
        rgb[1] = Math.min((int) (255 * g), 255);
        rgb[2] = Math.min((int) (255 * b), 255);
        
        //if(val >= 0 && val < 2)
        return rgb;
    }
    private Color bound8ConvGrad(float val) {
        float v = (float) Math.min(val, 7.9);
        Color colorAt = fx.getGradient().getColorAt(v / 7.9);
        if(colorAt == null){
            return color.get();
        }
        return colorAt;
    }
    public void deactivate() {
        isActive = false;
    }
    public void delete() {
        deactivate();
        port.getOutputs().remove(this);
    }
    public void desync() {
        syncMaster.notifyDesync(this);
        internalDesync();
    }
    public void desyncAll() {
        syncMaster.internalDesyncAll();
    }
    protected Color doColorStuff(double r, float[] smoother) {
        if (fx.getSmootherValue() == 0) {
            return bound8ConvGrad((float) Math.abs(r));
        }
        
        //int ss = getSong().getFX().getSmootherValue();
        int jumper = fx.getJumperValue();
        float avg = (float) (r * fx.getFactorValue());
        //float[] smoother = new float[ss];
        
        for (int i = 1; i < smoother.length; i++) {
            smoother[i - 1] = smoother[i];
        }
        
        smoother[smoother.length - 1] = Math.abs(avg);
        
        float smoothSum = 0;
        int smoothI = 0;
        
        for (int i = 0; i < smoother.length; i++) {
            smoothSum += smoother[i];
            smoothI++;
        }
        
        /*
         * for (int i = 1; i < smoother.length; i++) {
         * // && (smoother[i - 1] + jumper > avg && smoother[i - 1] - jumper <
         * avg)
         * if ((smoother[i] != 0 && (smoother[i - 1] + jumper > avg &&
         * smoother[i - 1] - jumper < avg))) {
         * smoothI++;
         * smoothSum += Math.abs(smoother[i]);
         * }
         * }
         */
        //System.out.println(avg);
        //System.out.println(((smoothSum / smoothI)));
        //float nw = (sum / fft.specSize() * fctr) % 4;
        //return conv((float) (((smoothSum / smoothI))));
        return bound8ConvGrad((smoothSum / (smoothI == 0 ? 1 : smoothI)));
    }

    public Algorithm.algorithms getAlgo() {
        return algo;
    }
    public void setAlgo(Algorithm.algorithms a) {
        algo = a;
    }
    public FX getFX() {
        
        return fx;
    }
    public void setFX(FX fx) {
        this.fx = fx;
    }

    public String getName() {

        return name;
    }


    public Port getPort() {
        return port;
    }
    public Output getSyncMaster() {
        return syncMaster;
    }
    public List<Output> getSyncSlaves() {
        return syncSlaves;
    }
    public byte getTrailingByte() {
        return trailingByte;
    }
    public void setTrailingByte(byte b) {
        trailingByte = b;
    }
    /*
     * public int getTrailingCharacterAsInt(){
     * if(trailingByte < 127){
     * return trailingByte;
     * }else{
     * int x = trailingByte;
     *
     * }
     * }
     */
    public char getTrailingCharacter() {
        return trailing;
    }
    public void setTrailingCharacter(char c) {
        trailing = c;
    }
    public Stage getVisualWindowStage() {
        return windowStage;
    }
    public void setVisualWindowStage(Stage stage) {
        windowStage = stage;
    }
    public abstract String getVisualizerFXML();
    private void internalDesync() {
        isSynced = false;
        syncMaster = null;
    }
    private void internalDesyncAll() {
        if (!isSyncMaster()) {
            throw new NullPointerException("This Output is not the master!");
        }
        
        for (Output o : new ArrayList<>(syncSlaves)) {
            o.desync();
        }
        
        //syncMaster = null;
        //isSynced = false;
        internalDesync();
        syncSlaves = null;
    }
    private void internalSync(Output master) {
        isSynced = true;
        syncMaster = master;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean b) {
        isActive = b;
    }
    public boolean isSyncMaster() {
//        if (syncMaster == null){
//            System.out.println("Output is null!?!?!");
//            throw new NullPointerException("Output is null?!?!?");
//        }
//
//        if(syncMaster == this){
//            System.out.println("found the master");
//        }

return isSynced && (syncMaster == this);
    }
    public boolean isSynced() {
        return isSynced;
    }
    public void loadConfigFile(File f) {
        
    }
    private void notifyDesync(Output escapee) {
        syncSlaves.remove(escapee);
        if (syncSlaves.isEmpty()) {
            isSynced = false;
            syncMaster = null;
        }
    }

    public int[] preprocess(byte[] in) throws SerialPortException {
        if (!isSynced() || isSyncMaster()) {
            int[] rgb = process(in);
            if (isSynced) {
                for (Output o : syncSlaves) {
                    o.write(rgb);
                    o.color.set(Color.rgb(rgb[0], rgb[1], rgb[2]));
                }
            }
            return process(in);
        }
        return new int[]{1, 2, 3};
        /*
         * int[] rgb = new int[3];
         * rgb[0] = (int)(syncMaster.color.get().getRed() * 255);
         * rgb[1] = (int)(syncMaster.color.get().getGreen() * 255);
         * rgb[2] = (int)(syncMaster.color.get().getBlue()* 255);
         * color.set(Color.rgb(rgb[0], rgb[1], rgb[2]));
         * write(rgb);
         * return rgb;
         */
    }

    public int[] preprocess(float[] in) throws SerialPortException {
        if (!isSynced() || isSyncMaster()) {
            int[] rgb = process(in);
            if (isSynced) {
                for (Output o : syncSlaves) {
                    o.write(rgb);
                    o.color.set(Color.rgb(rgb[0], rgb[1], rgb[2]));
                }
            }
            return process(in);
        }
        return new int[]{1, 2, 3};
    }

    protected int[] process(float[] in) throws SerialPortException {
        float[] n = Algorithm.fft(in);
        float mi = min;
        float mx = max;
        float avg = stravg(n);
        for (float f : n) {
            if (f > max) {
                max = f;
            }
            if (f < min) {
                min = f;
            }
        }
        smootherCheck();

        if (mi != min) {
            System.out.println("new max: " + max);
        }

        if (mx != max) {
            System.out.println("new min: " + min);
        }

        float r = Math.abs(avg) * 8;
        //System.out.println("avg: " + r);
        color.set(doColorStuff(r, smoother));
        int[] a = toIntArray(color.get());
        //System.out.println(Arrays.toString(a));
        write(a);
        //port.writeArr(rgb, trailing);
        return a;
    }


    protected int[] process(byte[] n) throws SerialPortException {
        smootherCheck();
        int[] out = process_old(n);
        color.set(Color.rgb(out[0], out[1], out[2]));
        write(out);
        //port.writeArr(out, trailing);
        //Algorithm.norm(n);
        //color.set(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        return out;
    }
    public void processOnGradient() {
        
    }
    @Deprecated
    public int[] process_old(byte[] n) {
        float avg = Math.max((float) ((avg(n) - 35) / 2), 0);
        return toIntArray(doColorStuff(avg, smoother));
    }
    protected void smootherCheck() {
        if (fx.getSmootherValue() < smoother.length) {
            System.out.println("Change detected in smoother");
            float[] newSmoother = new float[fx.getSmootherValue()];
            int delta = smoother.length - fx.getSmootherValue();
            for (int i = 0; i < newSmoother.length; i++) {
                newSmoother[i] = smoother[i + delta];
            }
            smoother = newSmoother;
        } else if (fx.getSmootherValue() > smoother.length) {
            System.out.println("Change detected in smoother");
            float[] newSmoother = new float[fx.getSmootherValue()];
            int delta = fx.getSmootherValue() - smoother.length;
            System.arraycopy(smoother, 0, newSmoother, delta, smoother.length);
            smoother = newSmoother;
        }
    }
    public boolean start() {
        return false;
    }
    protected float stravg(float[] bt) {
        float s = 0;
        float x = 1;
        for (float b : bt) {
            s += b;
            
        }
        return (s / bt.length);
    }
    /**
     * The output on which this function is called becomes the master.
     * The master is removed from the list automatically
     *
     * @param o
     */
    public void sync(List<Output> o) {
        syncSlaves = o;
        
        syncSlaves.remove(this);
        for (Output ot : syncSlaves) {
            ot.internalSync(this);
        }
        //syncSlaves.forEach((ot)->{
        //    ot.internalSync(this);
        //});
        syncMaster = this;
        isSynced = true;
    }
    public void test() {
        if (!testing && !isActive) {
            testing = true;
            Task t = new Task() {
                @Override
                protected Object call() throws Exception {
                    writeElp(255, 0, 0);
                    color.set(Color.RED);
                    Thread.sleep(700);
                    writeElp(0, 255, 0);
                    color.set(Color.GREEN);
                    Thread.sleep(700);
                    writeElp(0, 0, 255);
                    color.set(Color.BLUE);
                    Thread.sleep(700);
                    writeElp(0, 0, 0);
                    color.set(Color.BLACK);
                    
                    return null;
                }
            };
            
            Thread th = new Thread(t);
            
            t.setOnSucceeded(( Event a ) -> {
                testing = false;
            });
            
            t.setOnCancelled(( a ) -> {
                testing = false;
            });
            
            t.setOnFailed(( a ) -> {
                testing = false;
                t.getException().printStackTrace();
            });
            th.start();
        }
    }
    protected int[] toIntArray(Color c) {
        try {
            int[] a = new int[3];
            a[0] = (int) (c.getRed() * 255);
            a[1] = (int) (c.getGreen() * 255);
            a[2] = (int) (c.getBlue() * 255);
            return a;
        } catch (Exception e) {
            return new int[]{0, 0, 0};
        }
        
    }
    protected void write(Color c) throws SerialPortException {
        writeElp((int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
        //System.out.println("color was written");
    }
    protected void write(int[] rgb) throws SerialPortException {
        if (port.getType() == Port.types.Bytes) {
            byte[] out = new byte[6];
            out[0] = -1;//=1111 1111 us
            out[1] = 0;
            out[2] = asUnsigned(rgb[0]);
            out[3] = asUnsigned(rgb[1]);
            out[4] = asUnsigned(rgb[2]);
            out[5] = trailingByte;
            port.writeBytes(out);
        } else if (port.getType() == Port.types.Default) {
            port.writeArr(rgb, trailing);
        } else {
            System.out.println("fuck");
        }
    }
    protected void writeElp(int... rgb) throws SerialPortException {
        write(rgb);
    }

    public void writeStaticColor(Color value) throws SerialPortException {
        //System.out.println(value.getRed() + " " + value.getGreen() + " " + value.getBlue());
        color.set(value);
        //color.
        write(value);
        //System.out.println("nut");
    }
}
