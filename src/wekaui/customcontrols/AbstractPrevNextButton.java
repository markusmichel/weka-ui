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
    /**
     * The HiddenImage variable.
     */
    protected Image HIDDEN_IMAGE  = null;
    /**
     * The NormalImage variable. The image which is shown normally.
     */
    protected Image NORMAL_IMAGE  = null;
    /**
     * The HoveredImage variable. The image which is shown when the cursor is above it.
     */
    protected Image HOVERED_IMAGE = null;
    /**
     * The PressedImage variable. The image which is shown when it's clicked on.
     */
    protected Image PRESSED_IMAGE = null;
    
    /**
     * The currentImage variable. Contains the current shown image.
     */
    private Image currentImage = null;

    /**
     * Returns the currentImage
     * @return Imagefile
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Sets the current image
     * @param currentImage Imagefile
     */
    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }
    
    /**
     * Contructor
     */
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
    
    /**
     * Applies an image as currentImage
     * @param image Imagefile
     */
    private void applyImage(Image image) {
        currentImage = image;
        setImage(currentImage);
    }
    
    /**
     * Shows the normal image.
     */
    public void show() {
        applyImage(NORMAL_IMAGE);
    }
    
    /**
     * Shows the hidden image.
     */
    public void hide() {
        applyImage(HIDDEN_IMAGE);
    }
    
    /**
     * Checks if the currentImage is the HiddenImage
     * @return true if currentImage is not the HiddenImage, false otherwise
     */
    public boolean isShown() {
        return currentImage != HIDDEN_IMAGE;
    }
    /**
     * Checks if the currentImage is the HiddenImage
     * @return true if currentImage is the HiddenImage, false otherwise
     */
    public boolean isHidden() {
        return currentImage == HIDDEN_IMAGE;
    }
}
