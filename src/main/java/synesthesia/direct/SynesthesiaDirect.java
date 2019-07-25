/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synesthesia.direct;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author 'Aaron Lomba'
 */
public class SynesthesiaDirect extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/FXMLDocument.fxml"));
        Parent root = (Parent)ldr.load();
        
        
        FXMLDocumentController controller = (FXMLDocumentController)ldr.getController();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Synesthesia Direct");
        stage.show();
        controller.setup(stage, scene);
        
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        //Scene scene = new Scene(root);
        
        //stage.setScene(scene);
        //stage.show();
        
        stage.setOnCloseRequest(( WindowEvent we ) -> {
            try{
                //ctrlr.plr.ardComm.close();
            }catch(NullPointerException e){
                System.err.println("No comm port to close!");
                //ErrorHandler.standard(e);
            }
            System.out.println("Stage is closing");
            quit();
        }); 
    }
    
    public void quit(){
        System.out.println("Quitting...");
        //ctr.closeAll();
        //JIntellitype.getInstance().cleanUp();
        Platform.exit();
        //PlatformImpl.exit();
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);
    }
    
}
