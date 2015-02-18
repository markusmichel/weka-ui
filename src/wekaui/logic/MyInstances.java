/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import weka.core.Instances;

/**
 *
 * @author Noodlewood
 */
public class MyInstances extends Instances {
    
    private File source;
    private List<MyInstance> instances;

    public MyInstances(Instances dataset, File source) {        
        super(dataset);
        this.source = source;
        instances = new ArrayList();
    }
       
    public File getSource() {
        return this.source;
    }        
    
    public void addMyInstance(MyInstance instance) {
        instances.add(instance);
    }
    
    public List<MyInstance> getMyInstances() {
        return this.instances;
    }
}
