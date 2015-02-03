/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author markus
 */
public class PrevButton extends ImageView {
    private final Image NORMAL_IMAGE;
    private final Image HOVERED_IMAGE;
    private final Image PRESSED_IMAGE;
    
    
    public PrevButton() {
        NORMAL_IMAGE = new Image(getClass().getResourceAsStream("prev-button.png"));
        HOVERED_IMAGE = new Image(getClass().getResourceAsStream("prev-button-hover.png"));
        PRESSED_IMAGE = new Image(getClass().getResourceAsStream("prev-button.png"));
        
        setImage(NORMAL_IMAGE);
        
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            setImage(HOVERED_IMAGE);
        });
        
        addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            setImage(NORMAL_IMAGE);
        });
    }
    
}
