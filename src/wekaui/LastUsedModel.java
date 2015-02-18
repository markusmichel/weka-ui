/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 *
 * @author markus
 */
public class LastUsedModel {
    /** Location of the model file */
    private File file;
    
    /** When was the model last used in the tool */
    private Date lastOpened;
    
    private File emptyArffFile;
    
    public LastUsedModel(File file, Date lastOpened) {
        this.file = file;
        this.lastOpened = lastOpened;
    }
    
    /**
     * Stores a list of LastUsedModel objects to a XML file
     * in the same directory as the running JAR file.
     * @param models List of models to serialize.
     * @throws FileNotFoundException 
     */
    public static void saveLastUsedModels(List<LastUsedModel> models) throws FileNotFoundException {
        XStream stream = new XStream();
        try {
            String xml = stream.toXML(models);
            FileUtils.writeStringToFile(new File("models.xml"), xml);
        } catch (Exception ex) {
            Logger.getLogger(LastUsedModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns a list of LastUsedModels objects.
     * The list is restored from a "models.xml" file in the JAR directory.
     * If the File is not present, an empty list will be returned.
     * @return 
     */
    public static List<LastUsedModel> getLastUsedModels() {
        List<LastUsedModel> lastUsedModels;
        
        try {
            XStream stream = new XStream();
            lastUsedModels = (LinkedList<LastUsedModel>)stream.fromXML(new File("models.xml"));
        } catch(Exception e) {
            lastUsedModels = new LinkedList<>();
        }
        
        return lastUsedModels;
    }

    @Override
    public String toString() {
        return "Last opened model";
    }
    
    public File getEmptyArffFile() {
        return emptyArffFile;
    }

    public void setEmptyArffFile(File emptyArffFile) {
        this.emptyArffFile = emptyArffFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Date getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(Date lastOpened) {
        this.lastOpened = lastOpened;
    }
    
}
