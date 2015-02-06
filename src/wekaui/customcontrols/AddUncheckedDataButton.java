/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;


import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
/**
 *
 * @author FreshXL
 */
public class AddUncheckedDataButton extends AbstractPrevNextButton{
    
    public AddUncheckedDataButton(){
        HIDDEN_IMAGE = null;
        NORMAL_IMAGE = new Image(getClass().getResourceAsStream("add-button.png"));
        HOVERED_IMAGE = new Image(getClass().getResourceAsStream("add-button-hover.png"));
        PRESSED_IMAGE = new Image(getClass().getResourceAsStream("add-button.png"));  
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            if(isShown()) {
                Tooltip t = new Tooltip("Add new data");
                Tooltip.install(this, t);
            }
        });

    }    
}
