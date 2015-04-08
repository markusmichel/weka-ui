package wekaui;

import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;
import wekaui.logic.MyInstances;

/**
 * Session class to handle the data through the different scenes.
 */
public class Session {
    
    /**
     * The current selected model.
     */
    private LastUsedModel model;
    /**
     * List which contains the datasets.
     */
    private List<MyInstances> unlabeledData;
    /**
     * ArffFile 
     */
    private ArffFile arffFile;
    /**
     * Instances object which contains the dataset.
     */
    private Instances originalDataset;

    /**
     * Returns the ArffFile.
     * @return ArffFile
     */
    public ArffFile getArffFile() {
        return arffFile;
    }

    /**
     * Sets the ArffFile
     * @param arffFile ArffFile
     */
    public void setArffFile(ArffFile arffFile) {
        this.arffFile = arffFile;
    }
    
    /**
     * List which contains the OnModelChangeListener.
     */
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
    
    /**
     * Adds a changelistener
     * @param listener OnModelChangeListener
     */
    public void addModelChangeListener(OnModelChangeListener listener) {
        modelChangeListeners.add(listener);
    }
    
    /**
     * Removes the changelistener
     * @param listener OnModelChangeListener
     */
    public void removeModelChangeListener(OnModelChangeListener listener) {
        modelChangeListeners.remove(listener);
    }
    
    /**
     * Returns the MyInstances
     * @return the list of MyInstances
     */
    public List<MyInstances> getUnlabeledData() {
        return unlabeledData;
    }
    
    /**
     * Sets the unlabeled data
     * @param data List of MyInstances
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
    
    /**
     * Sets the original dataset.
     * @param originalDataset the Instances of the dataset.
     */
    public void setOriginalDataset(Instances originalDataset) {
        this.originalDataset = originalDataset;
    }
    
    public interface OnModelChangeListener {
        public void handle(LastUsedModel model);
    }
    /**
     * CustomControl which contains the LastUsedModels.
     */
    private LastUsedModelsList lum;
    /**
     * Sets the LastUsedModelsList
     * @param l the LastUsedModelsList
     */
    public void setLastUsedModelsList(LastUsedModelsList l){
        this.lum = l;
    }
    
    /**
     * Returns the LastUsedModelList
     * @return The LastUsedModelList
     */
    public LastUsedModelsList getLastUsedModelsList(){
        return this.lum;
    }
    
    /**
     * Resets the whole session object.
     */
    public void resetSession(){
        this.arffFile = null;
        this.model = null;
        this.modelChangeListeners.clear();
        this.originalDataset = null;
        this.unlabeledData.clear();
    }
}
