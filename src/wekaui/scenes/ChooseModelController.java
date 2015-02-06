package wekaui.scenes;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import weka.core.Instance;
import weka.core.Instances;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;
import wekaui.LastUsedModel;
import wekaui.Session;
import wekaui.customcontrols.LastOpenedModelButton;
import wekaui.customcontrols.NextButton;
import wekaui.logic.Trainer;

/**
 *
 * @author markus
 */
public class ChooseModelController implements Initializable {
    
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelLastUsed;
    @FXML
    private Label dropzoneModel;
    @FXML
    private Label labelOpenModel;
    @FXML
    private NextButton nextButton;
    @FXML
    private Label labelCurrentSelectedModelFile;
    
    private Session session;
    
    private LastOpenedModelButton selectedModelButton;
    private LastUsedModel currentSelectedModel;
    private List<LastUsedModel> lastUsedModels;
    
    private Session.OnModelChangeListener onModelChangeListener;
    
    @FXML
    private FlowPane lastUsedModelsContainer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initSession();
        initModelDropzone();
        initLastUsedModels();
        
        //nextButton.show();
        
        //Test train data; For development
        try {
            // Trainer trainer = new Trainer(session);
            
            //Instances classifiedData = trainer.createDataset("txt_sentoken");
//            classifiedData = trainer.classifyDataFoo(classifiedData);
//            for (int i = 0; i < classifiedData.numInstances(); i++) {
//                Instance instance = classifiedData.instance(i);
//                System.out.println(instance.toString());
//                System.out.println(instance.classAttribute().value((int)instance.classValue()));
//            }
            /*
            Instances classifiedData = trainer.classifyData();
            for (int i = 0; i < classifiedData.numInstances(); i++) {
                Instance instance = classifiedData.instance(i);
                System.out.println(instance.toString());
                System.out.println(instance.classAttribute().value((int)instance.classValue()));
            }
                        */
        } catch (Exception ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void setSession(Session session) {
        this.session = null;
        this.session = session;
        
        // show the nextButton if a model is given
        //@TODO add more logic?! Highlight selected model?!
        if(this.session.getModel() != null)
            nextButton.show();
        
        initSession(session);
    }

    private void initLastUsedModels() {
        lastUsedModelsContainer.setVgap(5);
        lastUsedModelsContainer.setHgap(5);
        
        // Fetch last used models from xml file
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream ("models.xml")));
             lastUsedModels = (List<LastUsedModel>) decoder.readObject ();
            decoder.close ();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
            lastUsedModels = new LinkedList<>();
        }
        
        for(LastUsedModel lastUsedModel : lastUsedModels) {
            LastOpenedModelButton button = new LastOpenedModelButton(lastUsedModel);
            lastUsedModelsContainer.getChildren().add(button);
            
            button.addOnClickListener((LastUsedModel model) -> {
                session.setModel(model.getFile());
                button.getStyleClass().add("active");
                if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");
                selectedModelButton = button;
            });
        }   
        
    }

    private void initModelDropzone() {        
        dropzoneModel.setOnDragEntered((DragEvent event) -> {
            dropzoneModel.getStyleClass().add("active");
        });
        
        dropzoneModel.setOnDragExited((DragEvent event) -> {
            dropzoneModel.getStyleClass().remove("active");
        });
        
        dropzoneModel.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            }
            event.consume();
        });
        
        /**
         * User has moved one or more items over the dropzone and dropped it.
         * Check if the files are valid .model files and assert that there are 
         * not more than one file dropped.
         */
        dropzoneModel.setOnDragDropped((DragEvent event) -> {
            dropzoneModel.getStyleClass().remove("active");
            
            Dragboard db = event.getDragboard();
            boolean success = false;
            File modelFile = null;
            
            // Save first file to modelFile
            if (db.hasFiles() && db.getFiles().size() == 1) {
                success = true;
                for (File file: db.getFiles()) {
                    modelFile = file;
                }
            } else {
                // @todo: provide visual feedback
                System.err.println("only one file supported");
            }
            
            // Assert only model files are selected
            if(
                    modelFile != null 
                    && FilenameUtils.getExtension(modelFile.getName()).toLowerCase().equals("model")
                    && !modelFile.isDirectory()
                    ) {
                
                
                
            } else {
                // No valid model file selected
                // @todo: provide visual feedback
                System.err.println("only .model files are supported");
            }
            
            event.setDropCompleted(success);
            event.consume();
            
            // @todo: always set model file - even if invalid?
            session.setModel(modelFile);
        });
        
        /**
         * Show file chooser if user clicks on the dropzone instead of dragging a file into it
         */
        dropzoneModel.setOnMouseClicked((MouseEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Datenmodell öffnen");
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            
            // @todo: extension of model file??
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weka Model File", "*.model"));
            File modelFile = fileChooser.showOpenDialog(window);
            
            // @todo: replace placeholder text withcurrent selected file
            if(modelFile != null) {
                session.setModel(modelFile);
            }
        });
    }
    
    /**
     * User has selected a weka training model and wants to proceed.
     * Go to next step (select unclassified data).
     * @param event
     */
    @FXML
    public void onNextClicked(MouseEvent event) {
        // Only proceed if button is visible
        if(nextButton.isHidden()) return;
        
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            // Add current model to last used models list and persist it to xml file
            lastUsedModels.add(currentSelectedModel);
            XMLEncoder encoder;
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("models.xml")));
            encoder.writeObject(lastUsedModels);
            encoder.close ();
            
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseUnclassifiedTexts.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ChooseUnclassifiedTextsController ctrl = loader.getController();
            ctrl.setSession(session);
            
            // remove old onModelChangeListener to prevent zombie objects
            session.removeModelChangeListener(onModelChangeListener);

        } catch (IOException ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initSession() {
        session = new Session();
        initSession(session);
    }
    
    private void initSession(Session session) {
        if(onModelChangeListener != null) {
            // remove old onModelChangeListener to prevent zombie objects
            session.removeModelChangeListener(onModelChangeListener);
        }
        
        onModelChangeListener = (File model) -> {
            if(model != null && model.exists()) {
                System.out.println("model file exists");
                // @todo: check if valid model file
                nextButton.show();
                currentSelectedModel = new LastUsedModel(model, new Date());
                labelCurrentSelectedModelFile.setText(model.getName());
            } else {
                System.out.println("model file does not exist");
                nextButton.hide();
                labelCurrentSelectedModelFile.setText("Kein Datenmodell ausgewählt");
            }
        };
        
        // Called when session model gets a weka training model file
        session.addModelChangeListener(onModelChangeListener);
    }
}