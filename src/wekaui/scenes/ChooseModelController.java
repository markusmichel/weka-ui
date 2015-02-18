package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;
import wekaui.ArffFile;
import wekaui.LastUsedModel;
import wekaui.Session;
import wekaui.customcontrols.InfoDialog;
import wekaui.customcontrols.LastOpenedModelButton;
import wekaui.customcontrols.NextButton;

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
    
    public static final String[] ALLOWERD_FILE_TYPES = {"model"};
    
    @FXML
    private FlowPane lastUsedModelsContainer;
    @FXML
    private StackPane container;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initSession();
        initModelDropzone();
        initLastUsedModels();
        
        //nextButton.show();
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
        
        lastUsedModels = LastUsedModel.getLastUsedModels();
        
        for(LastUsedModel lastUsedModel : lastUsedModels) {
            LastOpenedModelButton button = new LastOpenedModelButton(lastUsedModel);
            lastUsedModelsContainer.getChildren().add(button);
            
            button.addOnClickListener((LastUsedModel model) -> {
                session.setModel(model);
                button.getStyleClass().add("active");
                if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");
                selectedModelButton = button;
            });
        }   
        
    }
    
    /**
     * Check if a dragboard contains a valid weka model file.
     * @param db DrabBoard to check for valid weka model file.
     * @return true, if the dragboard contains a valid model file.<br>
     * false:<br>
     * <ul>
     *  <li>if the file extension is not in the ALLOWED_FILE_TYPES list</li>
     *  <li>There are more than one file in the DragBoard</li>
     *  <li>The file is a folder (even if the folder has e.g. ".model" extension"</li>
     * </ul
     */
    private boolean dragBoardIsValid(Dragboard db) {
        if (
                db.hasFiles() && 
                db.getFiles().size() == 1 && 
                Arrays.asList(ALLOWERD_FILE_TYPES).contains(FilenameUtils.getExtension(db.getFiles().get(0).getName()).toLowerCase()) &&
                !db.getFiles().get(0).isDirectory()
                ) {
            return true;
        }
        
        return false;
    }

    private void initModelDropzone() {        
        dropzoneModel.setOnDragEntered((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (dragBoardIsValid(db)) {
                dropzoneModel.getStyleClass().add("active");
            } else {
                dropzoneModel.getStyleClass().add("error");
            }
            event.consume();
        });
        
        // Remove active and error states on mouse out.
        dropzoneModel.setOnDragExited((DragEvent event) -> {
            dropzoneModel.getStyleClass().removeAll("active", "error");
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
            
            
            if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");
            
            // Save first file to modelFile
            if (dragBoardIsValid(db)) {
                modelFile = db.getFiles().get(0);
            } else {
                // No valid model file selected
                System.err.println("only .model files are supported");
                
                nextButton.hide();               
                
                InfoDialog info = new InfoDialog("Wrong fileformat", container, "warning");
                info.addOnClickListener((MouseEvent e) -> {
                    container.getChildren().remove(info);                    
                });
            }
            
            event.setDropCompleted(success);
            event.consume();
            
            // @todo: always set model file - even if invalid?
            // @todo: check if dropped model was already opened / is in the list of last used models
            session.setModel(new LastUsedModel(modelFile, new Date()));
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
                session.setModel(new LastUsedModel(modelFile, new Date()));
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
        
        // Add current model to last used models list and persist it to xml file
        lastUsedModels.add(currentSelectedModel);            

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseUnclassifiedTexts.fxml"));
        Scene scene;
        
        try {
            scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ChooseUnclassifiedTextsController ctrl = loader.getController();
            ctrl.setSession(session);
            
            // remove old onModelChangeListener to prevent zombie objects
            session.removeModelChangeListener(onModelChangeListener);
            LastUsedModel model = lastUsedModels.get(0);
            ArffFile arff = new ArffFile("test.arff");
            model.setEmptyArffFile(arff);
            LastUsedModel.saveLastUsedModels(lastUsedModels);
            
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
        
        onModelChangeListener = (LastUsedModel model) -> {
            if(model != null && model.getFile()!= null && model.getFile().exists()) {
                System.out.println("model file exists");
                // @todo: check if valid model file
                nextButton.show();
                currentSelectedModel = model;
                labelCurrentSelectedModelFile.setText(model.getFile().getName());
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