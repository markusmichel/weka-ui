package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;
import wekaui.ArffFile;
import wekaui.LastUsedModel;
import wekaui.LastUsedModelsList;
import wekaui.Session;
import wekaui.customcontrols.InfoDialog;
import wekaui.customcontrols.LastOpenedModelButton;
import wekaui.customcontrols.NextButton;
import wekaui.exceptions.ArffFileIncompatibleException;
import wekaui.logic.MyInstances;
import wekaui.logic.Trainer;

/**
 * FXML Controller class
 *
 * Handles the weka models.
 */
public class ChooseModelController implements Initializable {
    
    /**
     * The title.
     */
    @FXML
    private Label labelTitle;
    /**
     * Label for the LastUsedModels.
     */
    @FXML
    private Label labelLastUsed;
    /**
     * The label for the drag&drop area.
     */
    @FXML
    private Label dropzoneModel;    
    @FXML
    private Label labelOpenModel;
    /**
     * NextButton to navigate to the next scene.
     */
    @FXML
    private NextButton nextButton;
    @FXML
    private Label labelCurrentSelectedModelFile;
    /**
     * Session object which contains the whole data 
     * like the model and the unclassified data.
     */
    private Session session;
    /**
     * CustomControl: Button for the LastOpenedModels-
     */
    private LastOpenedModelButton selectedModelButton;
    /**
     * The current select model in the list.
     */
    private LastUsedModel currentSelectedModel;
    /**
     * CustomControl: The list which contains the LastUsedModels.
     */
    private LastUsedModelsList lastUsedModels;
    /**
     * The ModelChangeListener of the session object.
     */
    private Session.OnModelChangeListener onModelChangeListener;
    /**
     * Array which contains the allowed file types for the drag&drop area.
     */
    public static final String[] ALLOWED_FILE_TYPES = {"model"};
    /**
     * FlowPane for the LastUsedModels.
     */
    @FXML
    private FlowPane lastUsedModelsContainer;
    /**
     * Parent container of the controls.
     */
    @FXML
    private StackPane container;
    /**
     * Imageview which used as a Button. 
     * If it's clicked an Arff file structure can be added to the model.
     */
    @FXML
    private ImageView addArffStrucButton;
    /**
     * Imageview which used as a Button. 
     * If it's clicked the help scene is shown.
     */
    @FXML
    private ImageView helpButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initSession();
        initModelDropzone();
        initLastUsedModels();  
        initializeHelpButton();
        initializeNewArffFileButton();
    }
    
    /**
     * Sets the session
     * @param session Session object which contains the data
     */
    public void setSession(Session session) {
        this.session = null;
        this.session = session;
        
        // show the nextButton if a model is given
        //@TODO add more logic?! 
        if(this.session.getModel() != null){
            nextButton.show();
            
            // highlight the according button
            for (int i = 0; i < lastUsedModelsContainer.getChildren().size(); i++) {                    
                LastOpenedModelButton last = (LastOpenedModelButton) lastUsedModelsContainer.getChildren().get(i);
                if(last.getLastUsedModel().getFile().equals(session.getModel().getFile())){
                    currentSelectedModel = last.getLastUsedModel();
                    last.getStyleClass().add("active");
                    if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");
                    selectedModelButton = last;
                }
            }       
        }
        
        initSession(session);
    }
    
    /**
     * Initializes the help button and adds event listener to it     
     */
    private void initializeHelpButton(){
        helpButton.setCursor(Cursor.HAND);
        
        helpButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {                
                onHelpButtonClicked(event);
        });
        
        helpButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                helpButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/help-button-hover.png")));
                Tooltip t = new Tooltip("Show help");
                Tooltip.install(helpButton, t);
        });
        
        helpButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                helpButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/help-button.png")));
        });
    }
    
    /**
     * Initializes the NewArffFile button and adds event listener to it     
     */
    private void initializeNewArffFileButton(){
        addArffStrucButton.setCursor(Cursor.HAND);
        
        addArffStrucButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {                
                onAddArffStrucButtonClicked(event);
        });
        
        addArffStrucButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                addArffStrucButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/add-arff-structure-to-model-hover.png")));
                Tooltip t = new Tooltip("Add arff-structure to model.");
                Tooltip.install(addArffStrucButton, t);
        });
        
        addArffStrucButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                addArffStrucButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/add-arff-structure-to-model.png")));
        });
    }
    
    /**
     * Initializes the LastUsedModels.
     */
    private void initLastUsedModels() {
        lastUsedModelsContainer.setVgap(5);
        lastUsedModelsContainer.setHgap(5);
        
        lastUsedModels = LastUsedModel.getLastUsedModels();
        
        LastUsedModel lastUsedModel;
        for(int i = lastUsedModels.size() - 1; i >= 0; i--) {
            lastUsedModel = lastUsedModels.get(i);
            
            // Remove model if the model file does not exist anymore
            if(!lastUsedModel.getFile().exists()) {
                lastUsedModels.remove(i);
                continue;
            }
            
            LastOpenedModelButton button = new LastOpenedModelButton(lastUsedModel);
            lastUsedModelsContainer.getChildren().add(button);
            
            button.addOnClickListener((LastUsedModel model) -> {
                session.setModel(model);                
                button.getStyleClass().add("active");
                if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");                
                if(session.getModel().getEmptyArffFile() == null) addArffStrucButton.setVisible(true);
                else addArffStrucButton.setVisible(false);
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
                Arrays.asList(ALLOWED_FILE_TYPES).contains(FilenameUtils.getExtension(db.getFiles().get(0).getName()).toLowerCase()) &&
                !db.getFiles().get(0).isDirectory()
                ) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Initializes dropzone
     */
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
                addArffStrucButton.setVisible(false);
                
                InfoDialog info = new InfoDialog("Wrong fileformat", container, "warning");                
            }
            
            event.setDropCompleted(success);
            event.consume();
            
            // @todo: always set model file - even if invalid?            
            session.setModel(new LastUsedModel(modelFile, new Date()));
        });
        
        /**
         * Show file chooser if user clicks on the dropzone instead of dragging a file into it
         */
        dropzoneModel.setOnMouseClicked((MouseEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open model");
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weka Model File", "*.model"));
            File modelFile = fileChooser.showOpenDialog(window);
            
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
        currentSelectedModel.setLastOpened(new Date());
        lastUsedModels.push(currentSelectedModel);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseUnclassifiedTexts.fxml"));
        Scene scene;
        
        try {
            scene = new Scene(loader.load());
            stage.setScene(scene);
            
            // remove old onModelChangeListener to prevent zombie objects
            session.removeModelChangeListener(onModelChangeListener);
            LastUsedModel.saveLastUsedModels(lastUsedModels);
            
            session.setLastUsedModelsList(lastUsedModels);
            
            ChooseUnclassifiedTextsController ctrl = loader.getController();
            ctrl.setSession(session);
            
        } catch (IOException ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Initializes the Session object.
     */
    private void initSession() {
        session = new Session();
        initSession(session);
    }
    
    /**
     * Initializes the session and checks if a session is already initiated.
     * @param session Session object which contains the data
     */
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
                if(model.getEmptyArffFile() == null) 
                    addArffStrucButton.setVisible(true);
                currentSelectedModel = model;
                
                // highlight the according button
                boolean modelFound = false;
                for (int i = 0; i < lastUsedModelsContainer.getChildren().size(); i++) {                    
                    LastOpenedModelButton last = (LastOpenedModelButton) lastUsedModelsContainer.getChildren().get(i);
                    if(last.getLastUsedModel().getFile().equals(currentSelectedModel.getFile())){
                        last.getStyleClass().add("active");
                        if(selectedModelButton != null){
                            selectedModelButton.getStyleClass().remove("active");
                        }
                        selectedModelButton = last;
                        modelFound = true;
                    }
                }
                
                //if model is not yet in the container create new button and add it to the container
                if(!modelFound){                    
                    if(selectedModelButton != null){
                            selectedModelButton.getStyleClass().remove("active");
                    }
                    LastOpenedModelButton newButton = new LastOpenedModelButton(currentSelectedModel); 
                    newButton.getStyleClass().add("active");
                    selectedModelButton = newButton;
                    lastUsedModelsContainer.getChildren().add(newButton);
                    
                    newButton.addOnClickListener((currentSelectedModel) -> {
                        session.setModel(currentSelectedModel);                
                        newButton.getStyleClass().add("active");
                        if(selectedModelButton != null) selectedModelButton.getStyleClass().remove("active");
                        selectedModelButton = newButton;
                    });
                }
                
            } else {
                System.out.println("model file does not exist");
                addArffStrucButton.setVisible(false);
                nextButton.hide();                
            }
        };
        
        // Called when session model gets a weka training model file
        session.addModelChangeListener(onModelChangeListener);
    }
    
    /**
     * Is called when the user clicks on the addArffStrucButton.
     * Open the FileChoser and sets the empty arff file of the model.
     * @param event 
     */
    private void onAddArffStrucButtonClicked(MouseEvent event){
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add arff-file structure to model");
        Window window = ((Node)event.getTarget()).getScene().getWindow();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arff File", "*.arff"));
        File arff = null;
        try {
            arff = fileChooser.showOpenDialog(window);
        } catch (Exception ex) {            
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
            InfoDialog info = new InfoDialog("Error saving arff file\nWrong fileformat", container, "warning");
        }        
        
        if(arff != null) {                
            
            if(session.getModel().getEmptyArffFile() == null) {                
                    ArffFile f = new ArffFile(arff.getAbsolutePath());
                    setEmptyArffFileToModel(f);
            }   
        }
    }
    
    /**
     * Sets the empty arfffile
     * @param f the arfffile
     */
    private void setEmptyArffFileToModel(ArffFile f){
         try{
            MyInstances instances = f.getInstances();                     
            Trainer.classifyData(session.getModel(), instances);
            f = f.saveEmptyArffFile(f, session.getModel().getFile().getName());                    
            session.getModel().setEmptyArffFile(f);
            addArffStrucButton.setVisible(false);
            InfoDialog info = new InfoDialog("Arff file added to model", container, "info");

        } catch (ArffFile.ArffFileInvalidException | ArffFileIncompatibleException | IOException ex){
            InfoDialog info = new InfoDialog("Error saving arff file", container, "warning");
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Is called when the user clicks on the helpButton.
     * Opens a new window with information for the user.
     * @param event 
     */
    private void onHelpButtonClicked(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wekaui/scenes/HelpDialog.fxml"));            
            VBox page = (VBox) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Weka Help");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node)event.getTarget()).getScene().getWindow());
            Scene scene = new Scene(page);            
            dialogStage.setScene(scene);
            
            HelpDialogController ctrl = loader.getController();
            ctrl.setDialogStage(dialogStage);
            dialogStage.showAndWait();            
        } catch (IOException ex) {
            Logger.getLogger(NewArffFileController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}