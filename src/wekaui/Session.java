package wekaui;

import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;
import wekaui.logic.MyInstances;

/**
 *
 * @author Markus Michel
 */
public class Session {
    
    private LastUsedModel model;
    private List<MyInstances> unlabeledData;
    private ArffFile arffFile;
    private Instances originalDataset;

    public ArffFile getArffFile() {
        return arffFile;
    }

    public void setArffFile(ArffFile arffFile) {
        this.arffFile = arffFile;
    }
    
    private final List<OnModelChangeListener> modelChangeListeners = new ArrayList<>();

    /**
     * @return the model
     */
    public LastUsedModel getModel() {
        return model;
    }

    /**
     * Set a weka training model file.
     * Calls modelChangeListeners when set.
     * @param model the model to set
     */
    public void setModel(LastUsedModel model) {
        this.model = model;
        
        for(OnModelChangeListener listener : modelChangeListeners) {
            listener.handle(model);
        }
    }
    
    public void addModelChangeListener(OnModelChangeListener listener) {
        modelChangeListeners.add(listener);
    }
    
    public void removeModelChangeListener(OnModelChangeListener listener) {
        modelChangeListeners.remove(listener);
    }

    /**
     * @return the text
     */
    public List<MyInstances> getUnlabeledData() {
        return unlabeledData;
    }

    /**
     * @param text the text to set
     */
    public void setUnlabeledData(List<MyInstances> data) {
        this.unlabeledData = data;
    }
    
    /**
     *
     * @return the original Instances
     */
    public Instances getOriginalDataset() {
        return originalDataset;
    }
    
    public void setOriginalDataset(Instances originalDataset) {
        this.originalDataset = originalDataset;
    }
    
    public interface OnModelChangeListener {
        public void handle(LastUsedModel model);
    }
}
