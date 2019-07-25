/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import output.Output;

/**
 *
 * @author 'Aaron Lomba'
 */
public abstract class AbstractVisualWindowController  implements Initializable {
    public abstract void setup(Output item, Stage stage);
}
