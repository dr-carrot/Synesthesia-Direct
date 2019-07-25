/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package effects.simple;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import jssc.SerialPortException;
import output.Output;
import gradient.FullGradient;

/**
 *
 * @author 'Aaron Lomba'
 */
public class GradEffect extends Effect {

    private final Output output;
    private FullGradient fg;
    private long fLen;
    private long startTime;
    private boolean reverse;
    private boolean goingUp = true;
    private double lastValue;

    public GradEffect(Output o, FullGradient h, long time, boolean r) {
        output = o;
        fg = h;
        reverse = r;
        fLen = time;

    }

    public GradEffect(Output o, FullGradient h, long time) {
        this(o, h, time, false);
    }

    public FullGradient getGradient() {
        return fg;
    }

    public long getTime() {
        return fLen;
    }

    public boolean useReverse() {
        return reverse;
    }

    @Override
    public Output getOutput() {
        return output;
    }

    @Override
    public void doShit() {

        /*if (output.isSynced() && !output.isSyncMaster()) {
            try {
                output.writeStaticColor(output.getSyncMaster().color.get());
                System.out.println("snc");
                prep();
                return;
                
            } catch (SerialPortException ex) {
                ex.printStackTrace();
            }
        }*/

        long cTime = System.currentTimeMillis() - startTime;
        if (cTime >= fLen) {
            System.out.println("switch");
            prep();
            if (reverse) {
                goingUp = !goingUp;
            }
            //goingUp = !goingUp && reverse;
            return;
        }
        //System.out.println(startTime + " = " + (float)startTime);
        //System.out.println(cTime);
        double d = cTime / ((double) fLen);

        if (d > lastValue + 0.000001) {
            lastValue = d;
        } else {
            return;
        }

        if (!goingUp) {

            d = 1 - d;
        }

        d = Math.max(0, Math.min(d, 1));

        try {
            Color c = fg.getColorAt(d);
            if (c != null) {
                output.writeStaticColor(c);
                if(output.isSyncMaster()){
                    for(Output o : output.getSyncSlaves()){
                        if(o.isActive())
                            o.writeStaticColor(c);
                    }
                }
            } else {
                System.err.println("The color was null!?!?! val: " + d);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void prep() {
        startTime = System.currentTimeMillis();
        lastValue = 0;
    }

}
