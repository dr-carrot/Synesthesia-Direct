/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package effects;

import java.util.Arrays;
import org.jtransforms.fft.FloatFFT_1D;

/**
 *
 * @author 'Aaron Lomba'
 */
public abstract class Algorithm {

    public static enum algorithms {OG, FFT, RealNumbers, Gradient};


    private static float[] fftMemo = null;
    private static float[] memoTest = new float[3];

    public static  float[] fft(float[] vals) {
        
        if (fftMemo != null && memoTest[0] == vals[0] && memoTest[1] == vals[1] && memoTest[2] == vals[vals.length - 1]) {
            //System.out.println("memoized");
            return fftMemo;
        }
        memoTest[0] = vals[0];
        memoTest[1] = vals[1];
        memoTest[2] = vals[vals.length - 1];
        return runFFT(vals);
    }

    

    private static float[] runFFT(float[] vals) {
        float[] arr = Arrays.copyOf(vals, vals.length);
        FloatFFT_1D fft = new FloatFFT_1D(vals.length);
        fft.realForward(arr);
        fftMemo = arr;
        return arr;
    }
}
