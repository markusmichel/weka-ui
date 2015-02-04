/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import wekaui.Session;

/**
 * FXML Controller class
 *
 * @author markus
 */
public class ChooseUnclassifiedTextsController implements Initializable {
    @FXML
    private Label labelTexts;
    @FXML
    private Label labelOpenData;    
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Pane dropzoneArea;
    @FXML
    private Label dropzoneLabel;
    @FXML
    private ListView<String> dropzoneListView;
    
    private static final ObservableList<String> dataList = 
            FXCollections.observableArrayList();
    
    private Session session;    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initModelDropzone();
        
        session = new Session();
        
        dropzoneListView.setItems(dataList);
    }    
    
    private void initModelDropzone() {        
        dropzoneArea.setOnDragEntered((DragEvent event) -> {                        
            dropzoneLabel.getStyleClass().add("active");
        });
                
        dropzoneArea.setOnDragExited((DragEvent event) -> {            
            dropzoneLabel.getStyleClass().remove("active");
        });
                
        dropzoneArea.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            }
            event.consume();
        });
        
        dropzoneArea.setOnDragDropped((DragEvent event) -> {            
            dropzoneLabel.getStyleClass().remove("active");            
            
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {                                        
                event.acceptTransferModes(TransferMode.LINK);
                String filePath = null;
                for (File file:db.getFiles()) {                    
                    if(dropzoneListView.getItems().size() == 0){
                        dropzoneLabel.setVisible(false);
                    }
                    dataList.add(file.getAbsolutePath());                                        
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        /**
        * Show file chooser if user clicks on the dropzone instead of dragging a file into it
        */
        dropzoneArea.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("area clicked");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Unklassifizierte Daten öffnen");
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            // @todo: extension of model file??
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weka Model File", "*.model"));
            File dataFile = fileChooser.showOpenDialog(window);
            // @todo: replace placeholder text withcurrent selected file
            if(dataFile != null) {                
                if(dropzoneListView.getItems().size() == 0){
                    dropzoneLabel.setVisible(false);
                }
                dataList.add(dataFile.getAbsolutePath());                
                //session.setText(dataList);
            }
        });
    }

    @FXML
    private void onPrevClicked(ActionEvent event) {        
        System.out.println("prev step");
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ChooseModel.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void onNextClicked(ActionEvent event) {
        System.out.println("next step");
        /*
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource(".fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException ex) {
            Logger.getLogger(ChooseModelController.class.getName()).log(Level.SEVERE, null, ex);
        }
                */
    }


    
}
