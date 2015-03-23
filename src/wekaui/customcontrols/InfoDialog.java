/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 * InfoDialog - Shows a short notification for the user. 
 * Works only if parent is StackPane
 *
 * @author FreshXL
 */
public class InfoDialog extends VBox {
    @FXML
    Label infoText;
    
    @FXML
    VBox container;
    
    StackPane parent;
    double removeDuration = 2;
    
    // problem: value is only known after the node is shown
    double nodeHeight = 38;
    
    private List<InfoDialogClickListener> listeners = new ArrayList<>();
    
    /**
     * Constructor
     * @param textToSet The text to show
     * @param parent The parent to which the InfoDialog is added
     * @param type Determines the type of the InfoDialog: warning or info
     */
    public InfoDialog(String textToSet, StackPane parent, String type){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InfoDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();            
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }    
        
        switch(type){
            case "warning": container.getStyleClass().add("info-dialog-warning");
                break;
            case "info": container.getStyleClass().add("info-dialog-info");
                break;
            default: break;
        }
        
        this.parent = parent;            
        this.setPickOnBounds(false);
        initializeInfoNode(textToSet, this.parent);        
        startRemoveCounter();        
    }
    
    public void addOnClickListener(InfoDialogClickListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Initializes the events and sets up the parameters.
     * @param textToSet The information text of the InfoDialog
     * @param parent The parent to which the InfoDialog is added
     */
    private void initializeInfoNode(String textToSet, StackPane parent) {     
        infoText.setText(textToSet);
        parent.getChildren().add(this);
        
        StackPane.setMargin(this, new Insets( -nodeHeight, 0, 0, 0));        
        addInfoDialogAnimation();
        
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
    
    
    /**
     * Starts the counter. When it ends the remove Animation is triggered.
     */
    private void startRemoveCounter(){        
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(removeDuration),
            timerEnd -> removeInfoDialogAnimation()));
        
        timeline.play();
    }   
    
    /**
     * Removes the InfoDialog with an animation.
     */
    private void removeInfoDialogAnimation(){
        final TranslateTransition trans
            = new TranslateTransition(Duration.millis(500), container);   
        
        trans.setToY(-container.getHeight());
        
        VBox that = this;        
        trans.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {                
                parent.getChildren().remove(that);                
            }
        });
        
        trans.play();        
    }
    
    /**
     * Adds the InfoDialog with an animation.
     */
    private void addInfoDialogAnimation(){
        final TranslateTransition trans
            = new TranslateTransition(Duration.millis(500), container);      
                      
        trans.setToY(nodeHeight);        
        
        trans.play();        
    }    
    
    public interface InfoDialogClickListener {
        public void handle(MouseEvent event);
    }
    
}
