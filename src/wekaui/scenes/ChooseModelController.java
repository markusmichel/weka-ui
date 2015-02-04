package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import wekaui.LastUsedModel;
import wekaui.Session;
import wekaui.customcontrols.LastUsedModelsListViewCell;
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
    
    private Session session;
    @FXML
    private ListView<LastUsedModel> lastUsedModelsList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initModelDropzone();
        initSession();
        initLastUsedModelsList();
        
        //Test train data; For development
        try {
            Trainer trainer = new Trainer(session);
            System.out.println(trainer.classifyData().toString());
        } catch (Exception ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
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
        
        dropzoneModel.setOnDragDropped((DragEvent event) -> {
            dropzoneModel.getStyleClass().remove("active");
            
            Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        System.out.println(filePath);
                    }
                }
                event.setDropCompleted(success);
                event.consume();
        });
        
        /**
         * Show file chooser if user clicks on the dropzone instead of dragging a file into it
         */
        dropzoneModel.setOnMouseClicked((MouseEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Datenmodell öffnen");
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            
            // @todo: extension of model file??
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weka Model File", "*.model"));
            File modelFile = fileChooser.showOpenDialog(window);
            
            // @todo: replace placeholder text withcurrent selected file
            if(modelFile != null) {
                System.out.println("file: " + modelFile.getName());
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
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseUnclassifiedTexts.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ChooseUnclassifiedTextsController ctrl = loader.getController();
            //ctrl.setSession(session);

        } catch (IOException ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initSession() {
        session = new Session();
        
        // Called when session model gets a weka training model file
        session.addModelChangeListener((File model) -> {
            
            if(model != null) {
                // @todo: check if valid model file
                nextButton.setVisible(true);
                nextButton.setDisable(false);
            }
            
        });
    }

    private void initLastUsedModelsList() {
        ObservableList<LastUsedModel> items = FXCollections.observableArrayList (
            new LastUsedModel(),
            new LastUsedModel()
        );
        lastUsedModelsList.setItems(items);
        lastUsedModelsList.setCellFactory((ListView<LastUsedModel> param) -> new LastUsedModelsListViewCell());
    }
    
}