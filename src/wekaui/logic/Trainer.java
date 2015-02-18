/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import wekaui.LastUsedModel;
import wekaui.Session;

/**
 *
 * @author Freakalot
 */
public class Trainer {
    
    String modelPath = "train.model";
    String testdataPath = "test.arff";
    
    public static MyInstances classifyData(Classifier model, MyInstances unlabeled) throws Exception {
          //model = (Classifier) weka.core.SerializationHelper.read(session.getModel().getFile().getAbsolutePath());
        // set class attribute
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy
        Instances labeled = new Instances(unlabeled);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
          double clsLabel = model.classifyInstance(unlabeled.instance(i));
          labeled.instance(i).setClassValue(clsLabel);
          
          unlabeled.addMyInstance(new MyInstance(labeled.instance(i), model.distributionForInstance(labeled.instance(i))));
        }       
        
        return unlabeled;
    }
    
}