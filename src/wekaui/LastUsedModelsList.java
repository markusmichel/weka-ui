package wekaui;

import java.util.Collections;
import java.util.Date;
import java.util.Stack;

/**
 *
 * @author markus
 */
public class LastUsedModelsList extends Stack<LastUsedModel> {

    public static final int MAX = 10;
    public static final String MODELS_XML_FILE_NAME = "models.xml";
    
    private int max;
    
    public LastUsedModelsList(int max) {
        super();
        this.max = max;
    }

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
