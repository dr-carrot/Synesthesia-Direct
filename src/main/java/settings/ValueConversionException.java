/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

/**
 *
 * @author 'Aaron Lomba'
 */
public class ValueConversionException extends Exception {

    /**
     * Creates a new instance of <code>ValueConversionException</code> without
     * detail message.
     */
    public ValueConversionException() {
    }

    /**
     * Constructs an instance of <code>ValueConversionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ValueConversionException(String msg) {
        super(msg);
    }
}
