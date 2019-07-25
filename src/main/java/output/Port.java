/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import jssc.SerialPort;
import jssc.SerialPortException;
import settings.Settings;

/**
 *
 * @author 'Aaron Lomba'
 */
public class Port extends SerialPort {

    //private Profile profile;
    //private final String SETTING_TYPE;
    //private SimpleBooleanProperty open = new SimpleBooleanProperty(false);
    //private ArrayList<Connection> connections = new ArrayList<>();
    private List<Output> outputs = new ArrayList<>();
    private final SimpleBooleanProperty valid = new SimpleBooleanProperty(false);
    private boolean isActive = false;

    public enum types {
        Default, Bytes
    };
    private Task<byte[]> tsk;
    private types type = types.Default;

    public types getType() {
        return type;
    }

    public void setType(types t) {
        type = t;
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    public boolean isValid() {
        return valid.get();
    }

    public void setValid(boolean vlid) {
        this.valid.set(vlid);
    }

    public Port(String portName) {
        super(portName);
        //SETTING_TYPE = "Port." + portName;
    }

    @Override
    public String toString() {
        return getPortName();
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    @Deprecated
    public void activateAll(Mixer mxr) {
        if (isActive) {
            System.out.println("already active");
            return;
        }

        //streamer.addPort()
        //-------------
        if (1 == 1) {
            return;
        }
        System.out.println("activating...");
        float sampleRate = 176400;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);

        //TargetDataLine ln = (TargetDataLine) mxr.getLine(info);
        tsk = new Task() {

            @Override
            protected byte[] call() throws Exception {
                try {
                    //try  {
                    //TargetDataLine line = AudioSystem.getTargetDataLine(format);

                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    TargetDataLine line = (TargetDataLine) mxr.getLine(info);
                    line.open(format);
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void start() {
                            System.out.println("Closing input line on hook!");
                            line.close();
                        }
                    });

                    //AudioInputStream ais = new AudioInputStream
                    //inf[6]
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int numBytesRead;
                    byte[] data;

// Begin audio capture.
                    line.start();
                    System.out.println("watching...");
// Here, stopped is a global boolean set by another thread.
                    while (!isCancelled()) {
                        data = new byte[3000];
                        // Read the next chunk of data from the TargetDataLine.
                        numBytesRead = line.read(data, 0, data.length);

                        // Save this chunk of data.
                        //out.write(data, 0, numBytesRead);
                        updateValue(data);

                        //System.out.println(avg(data));
                    }
                    System.out.println("Done with input!");
                    //}
                    line.close();
                    //System.out.println("Done with main try");

                } catch (LineUnavailableException ex) {
                    System.err.println("Cant get line");
                }

                return null;
            }

        };

        tsk.setOnFailed(( e ) -> {
            tsk.getException().printStackTrace();
        });

        tsk.valueProperty().addListener(( ObservableValue<? extends byte[]> p, byte[] o, byte[] n ) -> {
            if (isOpened()) {
                for (Output out : outputs) {
                    if (out.isActive()) {
                        try {
                            int[] d = out.process(n);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            //Logger.getLogger(Port.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {

            }
        });

        Thread th = new Thread(tsk);
        th.start();

        isActive = true;
    }

    public Output addOutput(String name) {
        Output o = new RGBMono(name, this);
        outputs.add(o);
        return o;
    }

    public boolean addOutput(Output o) {
        return outputs.add(o);
    }

    @Override
    public boolean closePort() throws SerialPortException {
        boolean b = super.closePort();
        if (b) {
            for (Output o : outputs) {
                o.deactivate();
            }
        }
        return b;
    }

    @Override
    public boolean setParams(int baudRate, int dataBits, int stopBits, int parity) throws SerialPortException {
        boolean b = super.setParams(baudRate, dataBits, stopBits, parity);

        if (b) {
            Settings.SETTINGS.put("Port." + toString() + ".baudRate",
                    Integer.toString(baudRate));
            Settings.SETTINGS.put("Port." + toString() + ".stopBits",
                    Integer.toString(stopBits));
            Settings.SETTINGS.put("Port." + toString() + ".parity",
                    Integer.toString(parity));
            Settings.SETTINGS.put("Port." + toString() + ".dataBits",
                    Integer.toString(dataBits));
        }

        return b;
    }

    public boolean autoSetParams() throws SerialPortException {
        String s = "Port." + toString();
        return setParams(BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    public boolean writeArr(int[] rgb) throws SerialPortException {
        if (rgb.length != 3) {
            return false;
        }
        String s = "" + rgb[0] + " " + rgb[1] + " " + rgb[2] + "e";

        return writeString(s);
    }

    public boolean writeArr(int[] rgb, char trail) throws SerialPortException {
        if (rgb.length != 3) {
            System.out.println("length problem");
            return false;
        }
        String s = "" + rgb[0] + " " + rgb[1] + " " + rgb[2] + trail;
        //System.out.println("writing: " + s);
        return writeString(s);
    }
    /*
     * public boolean output(float avg) throws SerialPortException,
     * ProcessingException{
     * boolean b = true;
     * for(Connection c : connections){
     *
     * b = b && writeArr(c.getProfile().process(avg), c.getTrailChar());
     * }
     * return b;
     * }
     *
     * public boolean outputEach(float avg) throws ProcessingException,
     * SerialPortException{
     * if(profile == null)
     * return false;
     * //System.out.println(avg);
     * int[] rgb = profile.process(avg);
     * return writeArr(rgb);
     * }
     */
 /*
     * public ArrayList<Connection> getConnections(){
     * return connections;
     * }
     *
     * public void addConnection(Connection c){
     * connections.add(c);
     * }
     *
     * public void removeConnection(Connection c){
     * connections.remove(c);
     * }
     */

}
