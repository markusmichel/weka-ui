/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui;

import java.io.File;
import java.util.Date;

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
