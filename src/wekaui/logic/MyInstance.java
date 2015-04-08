/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.util.Arrays;
import weka.core.Instance;

/**
 * Handles the Weka instance. 
 */
public class MyInstance{
    /**
     * Weka instance which contains the data to classify.
     */
    private Instance instance;
    /**
     * Array which contains the probability of the dataset.
     */
    private double[] probabilities;    
    /**
     * The maximum probability of the instance.
     */
    public double maxProbability;
    
    /**
     * Constructor
     * @param instance Weka instance
     * @param probabilities the probabilities of the instances.
     */
    public MyInstance(Instance instance, double[] probabilities) {
        this.instance = instance;
        this.probabilities = probabilities;        
        this.maxProbability = getMaxOfProbability();
    }
    
    /**
     * Returns the weka instance variable
     * @return Instance 
     */
    public Instance getInstance() {
        return instance;
    }
    
    /**
     * Returns the probabilites of the instances.
     * @return double array with the probabilities.
     */
    public double[] getProbabilities() {
        return probabilities;
    }
    
    /**
     * Prints all the available data of the instances.
     */
    public void printInfo() {
        System.out.println("-----------------------------------------");
        System.out.println(getInstance().toString());
        for (int i = 0; i < getInstance().numAttributes(); i++) {
            System.out.println(getInstance().attribute(i).toString());
        }                                    
        System.out.println("result: " + getInstance().classAttribute().value((int)getInstance().classValue()));
        for (int i = 0; i < getProbabilities().length; i++) {
             System.out.println(getProbabilities()[i] + " " + getInstance().classAttribute().value(i));
        }
    }    
    
    /**
     * Helper-Method to determine the max value of the probability array     
     * @return The max value of the array
     */
    private double getMaxOfProbability(){               
        Arrays.sort(probabilities);
        return probabilities[probabilities.length - 1];
    }
}
