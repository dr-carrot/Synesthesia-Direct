/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package streaming;

import effects.Algorithm;
import effects.Algorithm.algorithms;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import jssc.SerialPortException;
import output.Output;

/**
 *
 * @author 'Aaron Lomba'
 */
public class Streamer {

    private Mixer mixer;
    private boolean isActive;
    private Task<byte[]> tsk;
    private List<Output> outputs = new ArrayList<>();

    public Streamer() {

    }

    public Streamer(Mixer mixer) {
        this.mixer = mixer;
    }

    public static List<Mixer> getTargets() {
        List<Mixer> out = new ArrayList<>();
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        List<Line.Info> availableLines = new ArrayList<>();
        for (Mixer.Info mixerInfo : mixers) {

            Mixer m = AudioSystem.getMixer(mixerInfo);

            Line.Info[] lines = m.getTargetLineInfo();
            if (lines.length > 0 && !mixerInfo.toString().substring(0, 4).equals("Port")) {
                //System.out.println("Found Mixer: " + mixerInfo);
                out.add(m);
            }

        }
        return out;
    }

    public void start() {
        System.out.println("activating...");
        float sampleRate = 176400;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
        tsk = new Task() {

            @Override
            protected byte[] call() throws Exception {
                try {
                    //try  {
                    //TargetDataLine line = AudioSystem.getTargetDataLine(format);

                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    TargetDataLine line = (TargetDataLine) mixer.getLine(info);
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
                    //ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int numBytesRead;
                    byte[] data;

// Begin audio capture.
                    line.start();
                    System.out.println("watching...");
// Here, stopped is a global boolean set by another thread.
                    while (!isCancelled()) {
                        data = new byte[4096];
                        // Read the next chunk of data from the TargetDataLine.
                        numBytesRead = line.read(data, 0, data.length);
                        //line.getLevel();
                        // Save this chunk of data.
                        //out.write(data, 0, numBytesRead);
                        updateValue(data);
                        updateProgress(line.getLevel(), 1);

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
            float[] norm = norm(n);
            for (Output out : outputs) {
                if (out.isActive() && out.getPort().isOpened()) {
                    try {
                        if (null == out.getAlgo()) {
                            out.preprocess(n);
                        } else {
                            switch (out.getAlgo()) {
                                case FFT:
                                    out.preprocess(norm);
                                    break;
                                case Gradient:
                                    break;
                                default:
                                    out.preprocess(n);
                                    break;
                            }
                        }

                    } catch (SerialPortException ex) {
                        Logger.getLogger(Streamer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        Thread th = new Thread(tsk);
        th.start();

        isActive = true;
    }

    public static float[] norm(byte[] audioBytes) {

        float[] audioFloats = new float[audioBytes.length];
        for (int i = 0; i < audioBytes.length; i++) {
            audioFloats[i] = ((float) audioBytes[i]) / 0x80;
            //System.out.println(((float) audioBytes[i]) / 0x80);
        }
        return audioFloats;
    }

    public Mixer getMixer() {
        return mixer;
    }

    public void setMixer(Mixer item) {
        this.mixer = item;
    }

    public void addOutput(Output item) {
        System.out.println("registered new output: " + item.getName());
        outputs.add(item);
    }

    public void removeOutput(Output o) {
        System.out.println("removed an output");
        outputs.remove(o);
    }

    public void stop() {
        if (tsk != null) {
            tsk.cancel();
        }
    }

    public void dumpMixer() {
        mixer = null;
    }
}
