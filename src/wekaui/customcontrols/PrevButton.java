/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.scene.image.Image;

/**
 * The Button to switch to the previous scene.
 * @author markus
 */
public class PrevButton extends AbstractPrevNextButton {
    
    /**
     * Contructor
     */
    public PrevButton() {
        NORMAL_IMAGE = new Image(getClass().getResourceAsStream("prev-button.png"));
        HOVERED_IMAGE = new Image(getClass().getResourceAsStream("prev-button-hover.png"));
        PRESSED_IMAGE = new Image(getClass().getResourceAsStream("prev-button.png"));
        show();
    }
    
}
