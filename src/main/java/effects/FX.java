/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package effects;

//import hardware.PortManager;
import java.io.File;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import gradient.FullGradient;
import gradient.Gradient;
import gradient.GradientIO;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import settings.Settings;
//import profiles.Profile;

/**
 *
 * @author 'Aaron Lomba'
 */
public class FX {

    private int smootherValue = 18;
    private int jumperValue = 6;
    private double factorValue = .7;
    private int leftLevelSmootherValue = 5;
    private int rightLevelSmootherValue = 5;
    private int leftLevelerValue = 5;
    private int rightLevelerValue = 5;
    private int peakerValue = 8;
    private boolean usingDefault;
    private int profile = 0;
    private String name = "Profile 1";
    private String binding;
    private FullGradient gradient;

    public boolean isUsingDefault() {
        return usingDefault;
    }

    public void setUsingDefault(boolean usingDefault) {
        this.usingDefault = usingDefault;
    }

    public FullGradient getGradient() {
        return gradient;
    }

    public void setGradient(FullGradient gradient) {
        this.gradient = gradient;
    }
    

    /**
     *
     */
    public FX() {
        
        usingDefault = true;
        File f = new File(Settings.getString("fx.default_gradient", Settings.resDir + "gradients/rgbmw_default.grd"));
        
        try {
            gradient = GradientIO.loadGradient(f);
        } catch (FileNotFoundException ex) {
            System.err.println("No gradient found!");
            //TODO acquire the gradient artifact from the internet
        }
    }
    
    public FX(String name){
        this();
        this.name = name;
        
    }
    
    
    

    /**
     *
     * @param smootherValue
     * @param jumperValue
     * @param factorValue
     * @param leftLevelSmootherValue
     * @param rightLevelSmootherValue
     * @param leftLevelerValue
     * @param rightLevelerValue
     * @param peakerValue
     */
    @Deprecated
    public FX(int smootherValue, int jumperValue, double factorValue, int leftLevelSmootherValue, int rightLevelSmootherValue, int leftLevelerValue, int rightLevelerValue, int peakerValue) {
        if (smootherValue >= 0) {
            this.smootherValue = smootherValue;
        }
        if (factorValue != -1) {
            this.factorValue = factorValue;
        }
        if (jumperValue != -1) {
            this.jumperValue = jumperValue;
        }
        if (leftLevelSmootherValue != -1) {
            this.leftLevelSmootherValue = leftLevelSmootherValue;
        }
        if (leftLevelerValue != -1) {
            this.leftLevelerValue = leftLevelerValue;
        }
        if (rightLevelSmootherValue != -1) {
            this.rightLevelSmootherValue = rightLevelerValue;
        }
        if (rightLevelerValue != -1) {
            this.rightLevelerValue = rightLevelerValue;
        }
        if (smootherValue != -1) {
            this.smootherValue = smootherValue;
        }

    }
    //Getters and Setters:

    /**
     *
     * @return
     */
    public boolean usesDefault() {
        return usingDefault;
    }
    
    public void setBinding(String s){
        binding = s;
    }
    
    public String getBinding(){
        return binding == null ? "NONE" : binding;
    }

    /**
     *
     * @param b
     */
    public void setUsesDefault(boolean b) {
        usingDefault = b;
    }

    /**
     *
     * @return
     */
    public double getFactorValue() {

        return factorValue;
    }

    /**
     *
     * @param factorValue
     */
    public void setFactorValue(double factorValue) {
        this.factorValue = factorValue;
        usingDefault = false;
    }

    /**
     *
     * @return
     */
    public int getLeftLevelSmootherValue() {

        return leftLevelSmootherValue;
    }

    /**
     *
     * @param leftLevelSmooterValue
     */
    public void setLeftLevelSmootherValue(int leftLevelSmooterValue) {
        this.leftLevelSmootherValue = leftLevelSmooterValue;
        usingDefault = false;
    }

    /**
     *
     * @return
     */
    public int getRightLevelSmootherValue() {

        return rightLevelSmootherValue;
    }

    /**
     *
     * @param rightLevelSmootherValue
     */
    public void setRightLevelSmootherValue(int rightLevelSmootherValue) {
        this.rightLevelSmootherValue = rightLevelSmootherValue;
        usingDefault = false;
    }

    /**
     *
     * @return
     */
    public int getLeftLevelerValue() {

        return leftLevelerValue;
    }

    /**
     *
     * @param leftLevelerValue
     */
    public void setLeftLevelerValue(int leftLevelerValue) {
        this.leftLevelerValue = leftLevelerValue;
        usingDefault = false;
    }

    /**
     *
     * @return
     */
    public int getRightLevelerValue() {

        return rightLevelerValue;
    }

    /**
     *
     * @param rightLevelerValue
     */
    public void setRightLevelerValue(int rightLevelerValue) {
        this.rightLevelerValue = rightLevelerValue;
    }

    /**
     *
     * @return
     */
    public int getPeakerValue() {

        return peakerValue;
    }

    /**
     *
     * @param peakerValue
     */
    public void setPeakerValue(int peakerValue) {
        this.peakerValue = peakerValue;
        usingDefault = false;
    }
    
    public void setName(String s){
        name = s;
    }

    /**
     *
     * @return
     */
    public int getJumperValue() {

        return jumperValue;
    }

    /**
     *
     * @param jumperValue
     */
    public void setJumperValue(int jumperValue) {
        this.jumperValue = jumperValue;
        usingDefault = false;
    }

    /**
     *
     * @return
     */
    public int getSmootherValue() {

        return smootherValue;
    }

    /**
     *
     * @param smootherValue
     */
    public void setSmootherValue(int smootherValue) {
        this.smootherValue = smootherValue;
        usingDefault = false;
    }

    /**
     *
     * @param i
     */
    public void setProfile(int i) {
        profile = i;
    }
    
    public String getName(){
        return name;
    }

    /**
     *
     * @return
     */
    public int getProfile() {
        return profile;
    }

    /**
     *
     */
    public void writeToMinisFile() {
        File f = new File("C:/Users/Aaron/Documents/Synesthesia resources/fxvalues.txt");
        ArrayList<String> vals = new ArrayList<>();
        vals.add(Integer.toString(getSmootherValue()));
        vals.add(Integer.toString(getJumperValue()));
        vals.add(Double.toString(getFactorValue()));
        vals.add(Integer.toString(getPeakerValue()));
        vals.add(Integer.toString(getLeftLevelSmootherValue()));
        vals.add(Integer.toString(getRightLevelSmootherValue()));
        vals.add(Integer.toString(getLeftLevelerValue()));
        vals.add(Integer.toString(getRightLevelerValue()));
        //Utilities.writeArrayList(vals, f);
    }
    
    @Override
    public String toString(){
        return getName();
    }

}
