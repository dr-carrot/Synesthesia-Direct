/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

import jssc.SerialPortException;

/**
 *
 * @author 'Aaron Lomba'
 */
public class PseudoPort extends Port{
    
    private boolean open = false;
    
    public PseudoPort(String portName) {
        super(portName);
    }
    
    @Override
    public void setValid(boolean x){
        //do nothing
    }
    
    /**
     * A pseudoport is always valid
     * @return true
     */
    @Override
    public boolean isValid(){
        return true;
        
    }
    
    /**
     * A pseudoport is always opened successfully
     * @return true
     */
    @Override
    public boolean openPort(){
        System.out.println("pseudo open");
        open = true;
        return true;
        
    }
    
    /**
     * Does nothing instead of writing the bytes
     * @param bytes The bytes to not write
     * @return true
     */
    @Override
    public boolean writeBytes(byte[] bytes){
        return true;
    }
    
    /**
     * uses Port's closePort. always succeeds (i think)
     * @return true
     */
    @Override
    public boolean closePort(){
        try {
            super.closePort();
        } catch (SerialPortException ex) {
        }
        open = false;
        
        return true;
    }
    
    /**
     * returns whether the port is open
     * @return
     */
    @Override
    public boolean isOpened(){
        return open;
    }
    
    /**
     * does nothing
     * @param a
     * @return true
     */
    @Override
    public boolean writeArr(int[] a){
        return true;
    }

    /**
     * does nothing
     * @param a
     * @param t
     * @return true
     */
    @Override
    public boolean writeArr(int[] a ,char t){
        return true;
        
    }
    
    /**
     * does nothing
     * @param b
     * @return true
     */
    @Override
    public boolean writeByte(byte b){
        return true;
    }
    
}
