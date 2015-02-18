/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import weka.core.Instances;
import wekaui.LastUsedModel;

/**
 *
 * @author Freakalot
 */
public class Trainer {
    
    String modelPath = "train.model";
    String testdataPath = "test.arff";
    
    public static MyInstances classifyData(LastUsedModel model, MyInstances unlabeled) throws Exception {
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        Instances labeled = new Instances(unlabeled);

        for (int i = 0; i < unlabeled.numInstances(); i++) {
          double clsLabel = model.getModel().classifyInstance(unlabeled.instance(i));
          labeled.instance(i).setClassValue(clsLabel);
          
          unlabeled.getMyInstances().add(new MyInstance(labeled.instance(i), model.getModel().distributionForInstance(labeled.instance(i))));
        }       
        
        return unlabeled;
    }    
}