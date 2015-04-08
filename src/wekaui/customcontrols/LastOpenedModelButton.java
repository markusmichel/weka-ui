/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import wekaui.LastUsedModel;

/**
 * FXML Controller class
 *
 * CustomControl which represents the models as a button with extra information.
 */
public class LastOpenedModelButton extends VBox  {

    /**
     * List which contains the LastOpenedModelButtonClickListener.
     */
    private List<LastOpenedModelButtonClickListener> listeners = new ArrayList<>();
    /**
     * The Weka model.
     */
    private final LastUsedModel model;
    
    /**
     * Container for the other controls.
     */
    @FXML
    VBox container;
    /**
     * Title of the model.
     */
    @FXML
    Label labelLastUsedModelTitle;
    /**
     * The date when the model is last used.
     */
    @FXML
    Label labelLastUsedModelDate;
    
    /**
     * Constructor
     * @param model the weka model
     */
    public LastOpenedModelButton(LastUsedModel model) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LastOpenedModelButton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        
        this.model = model;
        initialize();
    }
    
    /**
     * Adds onclickListener
     * @param listener LastOpenedModelButtonClickListener
     */
    public void addOnClickListener(LastOpenedModelButtonClickListener listener) {
        listeners.add(listener);
    }

    /**
     * Initializes the controller class.
     */
    private void initialize() {
        container.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            
            // Only proceed if user clicked primary(left) mouse button
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                for(LastOpenedModelButtonClickListener listener : listeners) {
                    listener.handle(model);
                }
            }
            
            event.consume();
        });
        
        labelLastUsedModelTitle.setText(model.getFile().getName());
        labelLastUsedModelDate.setText(model.getLastOpened().toLocaleString());
    }
    
    public interface LastOpenedModelButtonClickListener {
        public void handle(LastUsedModel model);
    }
    
    /**
     * Returns the associated LastUsedModel
     * @return The associated LastUsedModel
     */
    public LastUsedModel getLastUsedModel(){
        return model;
    }
    
}
