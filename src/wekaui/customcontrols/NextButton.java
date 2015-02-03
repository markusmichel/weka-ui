/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author markus
 */
public class NextButton extends ImageView {
    private final Image NORMAL_IMAGE;
    private final Image HOVERED_IMAGE;
    private final Image PRESSED_IMAGE;
    
    
    public NextButton() {
        NORMAL_IMAGE = new Image(getClass().getResourceAsStream("next-button.png"));
        HOVERED_IMAGE = new Image(getClass().getResourceAsStream("next-button-hover.png"));
        PRESSED_IMAGE = new Image(getClass().getResourceAsStream("next-button.png"));
        
        setImage(NORMAL_IMAGE);
        
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            setImage(HOVERED_IMAGE);
        });
        
        addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            setImage(NORMAL_IMAGE);
        });
    }
    
}
