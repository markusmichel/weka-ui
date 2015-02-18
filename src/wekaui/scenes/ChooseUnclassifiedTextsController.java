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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import wekaui.ArffFile;

import wekaui.Session;
import wekaui.customcontrols.AddUncheckedDataButton;
import wekaui.customcontrols.ClearUncheckedDataButton;
import wekaui.customcontrols.NextButton;
import wekaui.customcontrols.PrevButton;
import wekaui.logic.MyInstances;
import wekaui.logic.Trainer;
import wekaui.scenes.result.ResultMainController;

/**
 * FXML Controller class
 *
 * @author markus
 */
public class ChooseUnclassifiedTextsController implements Initializable {
    @FXML
    private Label labelOpenData;    
    @FXML
    private PrevButton prevButton;
    @FXML
    private Label modelInfoLabel;
    @FXML
    private Label modelInfoText;
    @FXML
    private NextButton nextButton;
    @FXML
    private Pane dropzoneArea;
    @FXML
    private Label dropzoneLabel;
    @FXML
    private ListView<MyInstances> dropzoneListView;
    @FXML
    private ClearUncheckedDataButton clearButton;
    @FXML
    private AddUncheckedDataButton addButton;
    
    private static final ObservableList<MyInstances> dataList = 
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
        
        // add change listener to dataList to show next button
        dataList.addListener(new ListChangeListener(){            
            @Override
            public void onChanged(Change c){                
                System.out.println("LIST CHANGED");
                if(dataList.size() != 0){
                    nextButton.show();
                }else{
                    nextButton.hide();
                }                
            }
        });
        
        dropzoneListView.setCellFactory((ListView<MyInstances> list) -> {
            
            final ListCell cell = new ListCell<MyInstances>(){
                @Override
                public void updateItem(MyInstances item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : getString());                     
                }

                private String getString() {
                    return getItem() == null ? "" : getItem().getSource().toString();
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
                    dataList.remove((MyInstances)cell.getItem());
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
                ArffFile arff;
                MyInstances instances;
                for (File file:db.getFiles()) {                    
                    if(dropzoneListView.getItems().size() == 0){
                        dropzoneLabel.setVisible(false);
                    }
                    arff = new ArffFile(file.getPath());
                    try {            
                        instances = arff.getInstances();
                        dataList.add(instances);
                        Trainer.classifyData(session.getModel(), instances);
                    } catch (ArffFile.ArffFileInvalidException | Trainer.ArffFileIncompatibleException ex) {
                        Logger.getLogger(ChooseUnclassifiedTextsController.class.getName()).log(Level.SEVERE, null, ex);
                        // @todo: show error message
                    }
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
    
    private void changeDataButtonsVisibility(boolean visibility){
        if(visibility){
            addButton.show();
            clearButton.show();
        }else{
            addButton.hide();
            clearButton.hide();
        }
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
                
                ArffFile arff;
                MyInstances instances;
                for(File file: dataFile) {
                    arff = new ArffFile(file.getPath());
                    try {
                        instances = arff.getInstances();
                        dataList.add(instances);
                        Trainer.classifyData(session.getModel(), instances);
                    } catch (ArffFile.ArffFileInvalidException | Trainer.ArffFileIncompatibleException ex) {
                        Logger.getLogger(ChooseUnclassifiedTextsController.class.getName()).log(Level.SEVERE, null, ex);
                        // @todo: show error message
                    }
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
                nextButton.show();
                dropzoneLabel.setVisible(false);
                changeDataButtonsVisibility(true);
            }
        }
    }

    @FXML
    private void onNextClicked(MouseEvent event) {        
         // Only proceed if button is visible
        if(nextButton.isHidden()) return;        
        
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wekaui/scenes/result/ResultMain.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ResultMainController ctrl = loader.getController();
            ctrl.setSession(session);    
        } catch (IOException ex) {
            Logger.getLogger(ChooseUnclassifiedTextsController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void onPrevClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModel.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            ChooseModelController ctrl = loader.getController();
            ctrl.setSession(session);            
        } catch (IOException ex) {
            Logger.getLogger(ChooseUnclassifiedTextsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onAddClicked(MouseEvent event) {
        startChooseFileDialog(event);
    }
    
    /**
     * Clears the dataList and the UnlabeledData from the Session
     * @param event 
     */
    @FXML
    private void onClearClicked(MouseEvent event) {
        dataList.clear();        
        session.setUnlabeledData(dataList);
        dropzoneLabel.setVisible(true);
        changeDataButtonsVisibility(false);            
    }
    
}
