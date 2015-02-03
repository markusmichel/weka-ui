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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

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
    private Label dropzoneData;
    @FXML
    private Button prevButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initModelDropzone();
    }    
    
    private void initModelDropzone() {        
        dropzoneData.setOnDragEntered((DragEvent event) -> {
            dropzoneData.getStyleClass().add("active");
        });
        
        dropzoneData.setOnDragExited((DragEvent event) -> {
            dropzoneData.getStyleClass().remove("active");
        });
        
        dropzoneData.setOnDragDropped((DragEvent event) -> {
            dropzoneData.getStyleClass().remove("active");
            
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
        dropzoneData.setOnMouseClicked((MouseEvent event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Unklassifizierte Daten öffnen");
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            
            // @todo: extension of model file??
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Weka Model File", "*.model"));
            File modelFile = fileChooser.showOpenDialog(window);
            
            // @todo: replace placeholder text withcurrent selected file
            if(modelFile != null) {
                System.out.println("file: " + modelFile.getName());
                //session.setModel(modelFile);
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
    
}
