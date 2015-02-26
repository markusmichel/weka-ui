/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
import wekaui.exceptions.ArffFileIncompatibleException;
import wekaui.exceptions.FileAlreadyAddedException;
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

    private static final ObservableList<MyInstances> dataList
            = FXCollections.observableArrayList();

    private Session session;

    private Set<String> filepaths;
    @FXML
    private Label dataSetCountLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filepaths = new HashSet<>();
        initDataDropzone();
        initListView();
    }
    
    /**
     * Initializes the listview and adds event listener to the cells
     */
    private void initListView() {
        dropzoneListView.setItems(dataList);

        // add change listener to dataList to show next button
        dataList.addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {                
                if (dataList.size() != 0) {
                    nextButton.show();
                    setDataSetAmountLabel();
                } else {
                    nextButton.hide();      
                    dataSetCountLabel.setText("Data set amount: 0");
                }
            }
        });

        dropzoneListView.setCellFactory((ListView<MyInstances> list) -> {

            final ListCell cell = new ListCell<MyInstances>() {
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
                if (cell.getItem() != null) {
                    double x = event.getScreenX();
                    double y = event.getScreenY();
                    deleteTip.show(cell, x + 10, y + 5);
                }
            });

            cell.setOnMouseExited((MouseEvent event) -> {
                if (cell.getItem() != null) {
                    deleteTip.hide();
                }
            });

            cell.setOnMouseClicked((MouseEvent event) -> {
                if (cell.getItem() != null) {
                    filepaths.remove(((MyInstances) cell.getItem()).getSource().getPath());
                    dataList.remove((MyInstances) cell.getItem());
                    deleteTip.hide();
                    checkIfDatalistIsEmpty();
                }
            });
            return cell;
        });

    }
    
    /**
     * Sets the DataSet Count Label according to single instance amount in dataList
     */
    private void setDataSetAmountLabel(){
        int insAmount = 0;
        for(MyInstances instances: dataList){
            insAmount += instances.getMyInstances().size();
        }                    
                    
        dataSetCountLabel.setText("Data set amount: "+ insAmount);
    }
    
    /**
     * Initializes dropzone
     */
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

                createInstancesFromFiles(db.getFiles());

                session.setUnlabeledData(dataList);
                changeDataButtonsVisibility(true);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        /**
         * Show file chooser if user clicks on the dropzone instead of dragging
         * a file into it
         */
        dropzoneArea.setOnMouseClicked((MouseEvent event) -> {
            startChooseFileDialog(event);
        });
    }
    
    /**
     * Changes the visibility of the add and clearbutton
     * @param visibility Boolean variable to set the visibility.
     */
    private void changeDataButtonsVisibility(boolean visibility) {
        if (visibility) {
            addButton.show();
            clearButton.show();
        } else {
            addButton.hide();
            clearButton.hide();
        }
    }
    
    /**
     * Starts the file dialog to open the unclassified data
     * @param event Event
     */
    private void startChooseFileDialog(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open unclassified data");

        Window window = ((Node) event.getTarget()).getScene().getWindow();

        List<File> dataFile = fileChooser.showOpenMultipleDialog(window);

        if (dataFile != null) {

            createInstancesFromFiles(dataFile);

            session.setUnlabeledData(dataList);
            changeDataButtonsVisibility(true);
        }
    }

    private void createInstancesFromFiles(List<File> files) {
        if (dropzoneListView.getItems().size() == 0) {
            dropzoneLabel.setVisible(false);
        }
        
        ArffFile arff;
        MyInstances instances;
        for (File file : files) {
            
            try {
                if (!filepaths.add(file.getPath())) throw new FileAlreadyAddedException();
                arff = new ArffFile(file.getPath());
                try {
                    instances = arff.getInstances();
                    Trainer.classifyData(session.getModel(), instances);
                    dataList.add(instances);
                } catch (ArffFile.ArffFileInvalidException | ArffFileIncompatibleException ex) {
                    // @todo: show error message
                    System.err.println("invalid arff file");
                }                
            } catch (FileAlreadyAddedException ex) {
                System.out.println("File already added!");
            }
        }
    }
    
    /**
     * Checks if the data list is empty and sets the visibility of the buttons accordingly
     */
    private void checkIfDatalistIsEmpty() {
        if (dataList.isEmpty()) {
            dropzoneLabel.setVisible(true);
            changeDataButtonsVisibility(false);
        }
    }
    
    /**
     * Sets the session
     * @param session Session object which contains the data
     */
    public void setSession(Session session) {
        this.session = session;

        if (this.session.getUnlabeledData() != null) {

            if (this.session.getUnlabeledData().isEmpty()) {
                dropzoneLabel.setVisible(true);
                changeDataButtonsVisibility(false);
            } else {
                nextButton.show();
                dropzoneLabel.setVisible(false);
                changeDataButtonsVisibility(true);
            }
        }
    }
    
    /**
     * Is called when the next button is clicked. Loads the next screen to show
     * and sets the session for it.
     * @param event MouseEvent
     */
    @FXML
    private void onNextClicked(MouseEvent event) {
        // Only proceed if button is visible
        if (nextButton.isHidden()) {
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
    
    /**
     * Is called when the prev button is clicked. Loads the prev screen to show
     * and sets the session for it.
     * @param event MouseEvent
     */
    @FXML
    private void onPrevClicked(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
    
    /**
     * Is called when the add button is clicked. 
     * Shows the file dialog to open new unclassified data.
     * @param event MouseEvent
     */
    @FXML
    private void onAddClicked(MouseEvent event) {
        startChooseFileDialog(event);
    }

    /**
     * Clears the dataList and the UnlabeledData from the Session
     *
     * @param event
     */
    @FXML
    private void onClearClicked(MouseEvent event) {
        dataList.clear();
        filepaths.clear();
        session.setUnlabeledData(dataList);
        dropzoneLabel.setVisible(true);
        changeDataButtonsVisibility(false);
    }

}
