/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author markus
 */
public class LastUsedModel {    
    private File file;
    private Date lastOpened;
    
    public LastUsedModel() {}
    
    public LastUsedModel(File file, Date lastOpened) {
        this.file = file;
        this.lastOpened = lastOpened;
    }
    
    public static void saveLastUsedModels(List<LastUsedModel> models) throws FileNotFoundException {
        XMLEncoder encoder;
        encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("models.xml")));
        encoder.writeObject(models);
        encoder.close ();
    }
    
    public static List<LastUsedModel> getLastUsedModels() {
        List<LastUsedModel> lastUsedModels;
                
        // Fetch last used models from xml file
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream ("models.xml")));
             lastUsedModels = (List<LastUsedModel>) decoder.readObject ();
            decoder.close ();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
            lastUsedModels = new LinkedList<>();
        }
        
        return lastUsedModels;
    }

    @Override
    public String toString() {
        return "Last opened model";
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
