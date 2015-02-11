/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javax.xml.soap.Node;

/**
 * FXML Controller class
 *
 * @author FreshXL
 */
public class InfoDialog extends VBox {
    @FXML
    Label infoText;
    
    @FXML
    VBox container;
    
    private List<InfoDialogClickListener> listeners = new ArrayList<>();
    
    public InfoDialog(String textToSet){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }    
        
        initialize(textToSet);
    }
    
    public void addOnClickListener(InfoDialogClickListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Initializes the events.
     */    
    public void initialize(String textToSet) {
        // TODO
        // setup the layout
        
        infoText.setText(textToSet);
        
        container.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            
            // Only proceed if user clicked primary(left) mouse button
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                for(InfoDialogClickListener listener : listeners) {
                    listener.handle(event);
                }
            }
            
            event.consume();
        });
    }  
    
    public interface InfoDialogClickListener {
        public void handle(MouseEvent event);
    }
    
}
