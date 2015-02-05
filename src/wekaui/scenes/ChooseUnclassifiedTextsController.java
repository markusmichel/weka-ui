/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

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
    private ListView<File> dropzoneListView;
    @FXML
    private Button clearButton;
    @FXML
    private Button addButton;
    
    private static final ObservableList<File> dataList = 
            FXCollections.observableArrayList();
    
    private Session session; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDataDropzone();        
        initListView();
    }    
    
    private void initListView(){
        dropzoneListView.setItems(dataList);
        
        dropzoneListView.setCellFactory((ListView<File> list) -> {
            
            final ListCell cell = new ListCell<File>(){
                @Override
                public void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());                        
                }

                private String getString() {
                    return getItem() == null ? "" : getItem().toString();
                }
            };
            
            Tooltip deleteTip = new Tooltip("Click to delete");
            
            cell.setOnMouseEntered((MouseEvent event) -> {
                if(cell.getItem() != null){
                    double x = event.getScreenX();
                    double y = event.getScreenY();                    
                    deleteTip.show(cell, x + 10, y + 5);
                }
            });
            
            cell.setOnMouseExited((MouseEvent event) -> {
                if(cell.getItem() != null){
                    deleteTip.hide();
                }
            });
            
            cell.setOnMouseClicked((MouseEvent event) -> {
                if(cell.getItem() != null){                    
                    dataList.remove((File)cell.getItem());
                    deleteTip.hide();
                    checkIfDatalistIsEmpty();
                }
            });
            return cell;
        });
        
    }
    
    private void initDataDropzone() {        
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
                    dataList.add(file);                                        
                }
                session.setUnlabeledData(dataList);
                changeDataButtonsVisibility(true);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        /**
        * Show file chooser if user clicks on the dropzone instead of dragging a file into it
        */
        dropzoneArea.setOnMouseClicked((MouseEvent event) -> {
            startChooseFileDialog(event);            
        });
    }

    @FXML
    private void onPrevClicked(ActionEvent event) {        
        System.out.println("prev step");
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModel.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ChooseModelController ctrl = loader.getController();
            ctrl.setSession(session);            
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
    
    /**
     * Clears the dataList and the UnlabeledData from the Session
     * @param event 
     */
    @FXML
    private void onClearClicked(ActionEvent event) {
        dataList.clear();        
        session.setUnlabeledData(dataList);
        dropzoneLabel.setVisible(true);
        changeDataButtonsVisibility(false);        
    }

    @FXML
    private void onAddClicked(ActionEvent event) {
        startChooseFileDialog(event);
    }
    
    private void changeDataButtonsVisibility(boolean visibility){
        addButton.setVisible(visibility);
        clearButton.setVisible(visibility);
    }

    private void startChooseFileDialog(Event event) {
        FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Unklassifizierte Daten öffnen");
            
            Window window = ((Node)event.getTarget()).getScene().getWindow();
            
            List<File> dataFile = fileChooser.showOpenMultipleDialog(window);
            
            if(dataFile != null) {                
                if(dropzoneListView.getItems().size() == 0){
                    dropzoneLabel.setVisible(false);
                }
                
                for(File file:dataFile){
                    dataList.add(file);
                }
                
                session.setUnlabeledData(dataList);
                changeDataButtonsVisibility(true);
            }
    }

    private void checkIfDatalistIsEmpty() {
        if(dataList.isEmpty()){
            dropzoneLabel.setVisible(true); 
            changeDataButtonsVisibility(false);
        }
        
    }

    public void setSession(Session session) {        
        this.session = session;
        
        if(this.session.getUnlabeledData() != null){
                        
            if(this.session.getUnlabeledData().isEmpty()){
                dropzoneLabel.setVisible(true);
                changeDataButtonsVisibility(false);
            }else{
                dropzoneLabel.setVisible(false);
                changeDataButtonsVisibility(true);
            }
        }
    }
    
}
