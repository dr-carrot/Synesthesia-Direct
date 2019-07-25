/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import javafx.scene.paint.Color;

/**
 *
 * @author 'Aaron Lomba'
 */
public interface ControlPort {
    void setColor(Color c);
    void setColor(int... rgb);
}
