package wekaui;

import java.util.Date;
import java.util.Stack;

/**
 * Extends Stack.
 * Overrides push method.
 * Custom behaviour:
 * <ul>
 *  <li>Only adds new items if the model file location is different</li>
 *  <li>If location is identical: move the model to first pace and update lastUsed date</li>
 * </ul> 
 */
public class LastUsedModelsList extends Stack<LastUsedModel> {

    /**
     * The static max value for the list.
     */
    public static final int MAX = 10;
    /**
     * The static file name.
     */
    public static final String MODELS_XML_FILE_NAME = "models.xml";
    
    /**
     * The max value for the list.
     */
    private int max;
    
    /**
     * Constructor
     * @param max sets the max value.
     */
    public LastUsedModelsList(int max) {
        super();
        this.max = max;
    }
    
    /**
     * Overrides the push method of the stack
     * @param item LastUsedModel
     * @return the LastUsedModel item
     */
    @Override
    public LastUsedModel push(LastUsedModel item) {
        
        boolean alreadyInList = false;
        // Search for models already added which have the same model file
        for(LastUsedModel model: this) {
            if(model.getFile().getAbsolutePath().equals(item.getFile().getAbsolutePath())) {
                alreadyInList = true;
                item = model;
                break;
            }
        }
        
        if(alreadyInList == true) {
            item.setLastOpened(new Date());
            remove(item);
            add(item);
        } else {
            super.push(item);
        }
        
        while(size() > max) {
            remove(size() - 1);
        }
        
        return item;
    }
    
}
