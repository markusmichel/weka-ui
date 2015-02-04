/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import wekaui.Session;
import weka.core.converters.TextDirectoryLoader;
import wekaui.logic.arffconverter.ArffConverter;

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
        if (session.getModel() == null) {
            session.setModel(new File(modelPath));
        }
        if (session.getUnlabeledData() == null) {
            //session.setUnlabeledData(new File(testdataPath));
        }

        model = (Classifier) weka.core.SerializationHelper.read(session.getModel().getAbsolutePath());
    }

    public Instances classifyData() throws Exception {

        Instances unlabeled = new Instances(
                new BufferedReader(
                        new FileReader(session.getUnlabeledData().get(0).getAbsolutePath())));

        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        Instances labeled = new Instances(unlabeled);

        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = model.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }

        return labeled;
    }

    public Instances classifyDataFoo(Instances unlabeled) throws Exception {

        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        Instances labeled = new Instances(unlabeled);

        for (int i = 0; i < unlabeled.numInstances(); i++) {
            double clsLabel = model.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }

        return labeled;
    }

    public Instances createDataset(List<File> data) throws Exception {        
        ArffConverter converter = new ArffConverter();                
        return new Instances(null, null, 0);
    }
}
