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
    
    private Session session;
    
    String modelPath = "train.model";
    String testdataPath = "test.arff";
    
    private Classifier model;

    public Trainer(Session session) throws Exception {
        this.session = session;   
        
        //for development
        session.setModel(new LastUsedModel(new File(modelPath), new Date()));
        List<File> unlabeledData = new ArrayList<File>();
        unlabeledData.add(new File(testdataPath));
        session.setUnlabeledData(unlabeledData);
        
        model = (Classifier) weka.core.SerializationHelper.read(session.getModel().getFile().getAbsolutePath());
    }

    public List<MyInstance> classifyData() throws Exception {
        List<MyInstance> myInstances = new ArrayList<>();

        Instances unlabeled = new Instances(
            new BufferedReader(
              new FileReader(session.getUnlabeledData().get(0).getAbsolutePath())));           

        // set class attribute
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

        // create copy
        Instances labeled = new Instances(unlabeled);

        // label instances
        for (int i = 0; i < unlabeled.numInstances(); i++) {
          double clsLabel = model.classifyInstance(unlabeled.instance(i));
          labeled.instance(i).setClassValue(clsLabel);
          
          myInstances.add(new MyInstance(labeled.instance(i), model.distributionForInstance(labeled.instance(i))));
        }

        return myInstances;
    }
    
}