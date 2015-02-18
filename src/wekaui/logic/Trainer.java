/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.core.Instances;
import wekaui.LastUsedModel;
import wekaui.exceptions.ArffFileIncompatibleException;

/**
 *
 * @author Freakalot
 */
public class Trainer {    
    /**
     * Classifies unlabeled instances and calculates class probabilites
     * labeled instances are saved  MyInstances Class
     * @param lum
     * @param unlabeled
     */
    public static void classifyData(LastUsedModel lum, MyInstances unlabeled) throws ArffFileIncompatibleException {
        try {
            Classifier model = (Classifier) weka.core.SerializationHelper.read(lum.getFile().getAbsolutePath());

            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            Instances labeled = new Instances(unlabeled);

            for (int i = 0; i < unlabeled.numInstances(); i++) {
              double clsLabel;
                try {
                    clsLabel = model.classifyInstance(unlabeled.instance(i));
                    labeled.instance(i).setClassValue(clsLabel);

                    unlabeled.getMyInstances().add(new MyInstance(labeled.instance(i), model.distributionForInstance(labeled.instance(i))));
                } catch (Exception ex) {
                    throw new ArffFileIncompatibleException();
                }          
            }    
        } catch (Exception ex) {
            Logger.getLogger(LastUsedModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}