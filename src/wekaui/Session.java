
package wekaui;

import java.io.File;

/**
 *
 * @author Markus Michel
 */
public class Session {
    private File model;
    private String[] text;

    /**
     * @return the model
     */
    public File getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(File model) {
        this.model = model;
    }

    /**
     * @return the text
     */
    public String[] getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String[] text) {
        this.text = text;
    }
}
