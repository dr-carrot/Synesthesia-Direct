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
public class DatabaseNotInitializedException extends RuntimeException {

    /**
     * Creates a new instance of <code>DatabaseNotInitializedException</code>
     * without detail message.
     */
    public DatabaseNotInitializedException() {
    }

    /**
     * Constructs an instance of <code>DatabaseNotInitializedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DatabaseNotInitializedException(String msg) {
        super(msg);
    }
}
