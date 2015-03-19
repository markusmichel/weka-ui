/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Freakalot
 */
public class Weka extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = new Locale("de", "DE");
        
        //Parent root = FXMLLoader.load(getClass().getResource("scenes/ChooseModel.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scenes/ChooseModel.fxml"));
        //fxmlLoader.setResources(ResourceBundle.getBundle("bundle", locale));
        
        Scene scene = new Scene(fxmlLoader.load());
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
