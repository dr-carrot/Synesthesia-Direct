/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package output;

/**
 *
 * @author 'Aaron Lomba'
 */
public class RGBAddressable extends Output{
    
    public RGBAddressable(String name, Port port) {
        super(name, port);
    }

    @Override
    public String getVisualizerFXML() {
        return "/fxml/AddressableVisualUI.fxml";
    }

    
}
