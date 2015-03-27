/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
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

            for (int i = 0; i < unlabeled.numInstances(); i++) {
              double clsLabel;
                try {
                    clsLabel = model.classifyInstance(unlabeled.instance(i));
                    unlabeled.instance(i).setClassValue(clsLabel);
                    unlabeled.getMyInstances().add(new MyInstance(unlabeled.instance(i), model.distributionForInstance(unlabeled.instance(i))));
                } catch (Exception ex) {
                    throw new ArffFileIncompatibleException();
                }          
            }    
        } catch (Exception ex) {
            //Logger.getLogger(LastUsedModel.class.getName()).log(Level.SEVERE, null, ex);
            throw new ArffFileIncompatibleException();
        }
    }    
}