
package wekaui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Markus Michel
 */
public class Session {
    private File model;
    private List<File> unlabeledData;
    
    private final List<OnModelChangeListener> modelChangeListeners = new ArrayList<>();

    /**
     * @return the model
     */
    public File getModel() {
        return model;
    }

    /**
     * Set a weka training model file.
     * Calls modelChangeListeners when set.
     * @param model the model to set
     */
    public void setModel(File model) {
        this.model = model;
        
        for(OnModelChangeListener listener : modelChangeListeners) {
            listener.handle(model);
        }
    }
    
    public void addModelChangeListener(OnModelChangeListener listener) {
        modelChangeListeners.add(listener);
    }

    /**
     * @return the text
     */
    public List<File> getUnlabeledData() {
        return unlabeledData;
    }

    /**
     * @param text the text to set
     */
    public void setUnlabeledData(List<File> data) {
        this.unlabeledData = data;
    }
    
    public interface OnModelChangeListener {
        public void handle(File model);
    }
}
