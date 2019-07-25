/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

import effects.Algorithm;

/**
 *
 * @author 'Aaron Lomba'
 */
public class RGBMono extends Output{
    
    public RGBMono(String name, Port port, Algorithm.algorithms a) {
        super(name, port, a);
    }
    public RGBMono(String name, Port port) {
        super(name, port);
    }

    @Override
    public String getVisualizerFXML() {
        return "/fxml/WindowVisualUI.fxml";
    }
    
}
