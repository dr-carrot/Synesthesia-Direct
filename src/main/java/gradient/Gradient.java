/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gradient;

import java.io.File;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Paint;
import javafx.util.Pair;

/**
 *
 * @author 'Aaron Lomba'
 */
public class Gradient {

    //private double size;
    private DoubleProperty sz = new SimpleDoubleProperty(0);
    
    
    private Color c1, c2;
    
    
    
    public Gradient(){
    }
    
    public Gradient(Color c1, Color c2){
        this(c1, c2, 1);
    }
    
    public DoubleProperty sizeProperty(){
        return sz;
    }
    
    public Gradient(Color c1, Color c2, double sz){
        //size = sz;
        this.sz.set(sz);
        this.c1 = c1;
        this.c2 = c2;
    }
    
    private void addColorPair(Color c1, Color c2){
        this.c1 = c1;
        this.c2 = c2;
        
    }
    
    public double getSize(){
        return sz.get();
    }

    public Color getColor1(){
        return c1;
    }
    
    public Color getColor2(){
        return c2;
    }
    
    public void setSize(double d){
        sz.set(d);
    }
    
    public void setP2(Color p){
        c2 = p;
    }
    
    public void setP1(Color p){
        c1 = p;
    }

    public Color getGradColor(double value) {
        
        return c1.interpolate(c2, value);
        /*
        if (value < 0 || value > 1) {
            throw new NumberFormatException("Invalid value. Must be between 0 and 1: " + value);
        }
        
        float rf = (float) (c1.getRed());
        float gf = (float) (c1.getGreen());
        float bf = (float) (c1.getBlue());
        
        float r = (float) (rf + value * (c2.getRed() - rf));
        float g = (float) (gf + value * (c2.getGreen() - gf));
        float b = (float) (bf + value * (c2.getBlue() - bf));
        //System.out.println();
        
        return new Color(r, g, b, 1);*/
    }
}
