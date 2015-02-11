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
    
}
