/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import weka.core.Instance;

/**
 *
 * @author Noodlewood
 */
public class LabeledData {
    private Instance instance;
    private double[] probabilities;
    
    public LabeledData(Instance instance, double[] probabilities) {
        this.instance = instance;
        this.probabilities = probabilities;
    }
    
    public Instance getInstance() {
        return instance;
    }
    
    public double[] getProbabilities() {
        return probabilities;
    }
    
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
    
}
