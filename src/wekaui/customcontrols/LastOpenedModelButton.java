/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author markus
 */
public class LastOpenedModelButton extends VBox  {

    public LastOpenedModelButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LastOpenedModelButton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
}
