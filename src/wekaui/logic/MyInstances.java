/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    
    public List<MyInstance> getMyInstances() {
        return this.instances;
    }    
    
    /**
     * Merges the MyInstances List to one file
     * @param dataToMerge List containing MyInstances
     * @return 
     */
    static public LinkedHashMap<String, List<MyInstance>> getMergedData(List<MyInstances> dataToMerge){
        LinkedHashMap<String, List<MyInstance>> list = new LinkedHashMap<String, List<MyInstance>> ();
        
        for(MyInstances instances: dataToMerge){
            List<MyInstance> i = instances.getMyInstances();
            for(MyInstance ins: i){
                String classifiedClass = ins.getInstance().classAttribute().value((int)ins.getInstance().classValue());                
                if(!list.containsKey(classifiedClass)){
                    List<MyInstance> l = new ArrayList<>();
                    l.add(ins);
                    list.put(classifiedClass, l);
                }else{
                    List<MyInstance> l = list.get(classifiedClass);
                    l.add(ins);
                    list.put(classifiedClass, l);
                }                
            }
        }      
        
        return list;
    }
}
