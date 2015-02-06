/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.scene.image.Image;

/**
 *
 * @author markus
 */
public class NextButton extends AbstractPrevNextButton {
        
    public NextButton() {
        HIDDEN_IMAGE = null;
        NORMAL_IMAGE = new Image(getClass().getResourceAsStream("next-button.png"));
        HOVERED_IMAGE = new Image(getClass().getResourceAsStream("next-button-hover.png"));
        PRESSED_IMAGE = new Image(getClass().getResourceAsStream("next-button.png"));
    }
}
