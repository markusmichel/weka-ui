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
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
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
        session.setModel(new File(modelPath));
        List<File> unlabeledData = new ArrayList<File>();
        unlabeledData.add(new File(testdataPath));
        session.setUnlabeledData(unlabeledData);
        
        model = (Classifier) weka.core.SerializationHelper.read(session.getModel().getAbsolutePath());
    }

    public List<LabeledData> classifyData() throws Exception {
        List<LabeledData> labeledData = new ArrayList<>();

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
          
          labeledData.add(new LabeledData(labeled.instance(i), model.distributionForInstance(labeled.instance(i))));
        }

        return labeledData;
    }
    
}