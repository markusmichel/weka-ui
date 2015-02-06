/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author markus
 */
public abstract class AbstractPrevNextButton extends ImageView {
    protected Image HIDDEN_IMAGE  = null;
    protected Image NORMAL_IMAGE  = null;
    protected Image HOVERED_IMAGE = null;
    protected Image PRESSED_IMAGE = null;
    
    private Image currentImage = null;

    public Image getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }
    
    public AbstractPrevNextButton() {        
        applyImage(HIDDEN_IMAGE);
        
        // Hover
        // Show hover image if button is not hidden
        addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {
            if(isShown()) {
                setCursor(Cursor.HAND);
                applyImage(HOVERED_IMAGE);
            }
        });
        
        // Hover out
        // Show normal image if button is not hidden
        addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
            if(isShown()) applyImage(NORMAL_IMAGE);
        });
    }
    
    private void applyImage(Image image) {
        currentImage = image;
        setImage(currentImage);
    }
    
    public void show() {
        applyImage(NORMAL_IMAGE);
    }
    
    public void hide() {
        applyImage(HIDDEN_IMAGE);
    }
    
    public boolean isShown() {
        return currentImage != HIDDEN_IMAGE;
    }
    
    public boolean isHidden() {
        return currentImage == HIDDEN_IMAGE;
    }
}
