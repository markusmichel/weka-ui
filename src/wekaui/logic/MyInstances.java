/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import weka.core.Instances;

/**
 *
 * Helper class for the weka isntances.
 */
public class MyInstances extends Instances {
    
    /**
     * The source file
     */
    private File source;
    /**
     * List which contains MyInstances.
     */
    private List<MyInstance> instances;

    /**
     * Constructor
     * @param dataset Weka instances
     * @param source Source files
     */
    public MyInstances(Instances dataset, File source) {        
        super(dataset);
        this.source = source;
        instances = new ArrayList();
    }
       
    /**
     * Returns the source file
     * @return File 
     */
    public File getSource() {
        return this.source;
    }           
    
    /**
     * Returns the list which contains the MyInstances.
     * @return List with MyInstance
     */
    public List<MyInstance> getMyInstances() {
        return this.instances;
    }
    
    /**
     * Merges the MyInstances List to one file
     * @param dataToMerge List containing MyInstances
     * @return A list which contains the merged data
     */
    static public List<MyInstance> getMergedData(List<MyInstances> dataToMerge){
        List<MyInstance> list = new ArrayList<>();
        
        for(MyInstances instances: dataToMerge){
            List<MyInstance> i = instances.getMyInstances();
            for(MyInstance ins: i){
                list.add(ins);
            }
        }
        
        return list;
    }
    
    /**
     * Orders the MyInstance List descending, according to their probability
     * @param dataToOrder List containing MyInstance
     * @return The list in descending order
     */
    static public List<MyInstance> getOrderedData(List<MyInstance> dataToOrder){
        Collections.sort(dataToOrder, new Comparator<MyInstance>() {
            @Override
            public int compare(MyInstance ins1, MyInstance ins2) {
                return Double.compare(ins2.maxProbability, ins1.maxProbability);
            }
        });
        
        return dataToOrder;
    }
    
    /**
     * Helper-Method to determine the max value of the probability array
     * @param arr
     * @return The max value of the array
     */
    static private double getMaxOfArray(double[] arr){           
        Arrays.sort(arr);
        return arr[arr.length - 1];
    }
    
}
