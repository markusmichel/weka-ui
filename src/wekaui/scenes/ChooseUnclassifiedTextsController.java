/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import wekaui.ArffFile;
import wekaui.LastUsedModel;

import wekaui.Session;
import wekaui.customcontrols.AddUncheckedDataButton;
import wekaui.customcontrols.ClearUncheckedDataButton;
import wekaui.customcontrols.InfoDialog;
import wekaui.customcontrols.NextButton;
import wekaui.customcontrols.PrevButton;
import wekaui.exceptions.ArffFileIncompatibleException;
import wekaui.exceptions.FileAlreadyAddedException;
import wekaui.logic.MyInstances;
import wekaui.logic.Trainer;

/**
 * FXML Controller class
 *
 * Handles the unclassified datasets. The added files are verified.
 */
public class ChooseUnclassifiedTextsController implements Initializable {
    
    /**
     * 
     */
    @FXML
    private Label labelOpenData;
    /**
     * PrevButton to navigate to the previous scene.
     */
    @FXML
    private PrevButton prevButton;
    /**
     * InfoLabel
     */
    @FXML
    private Label modelInfoLabel;
    /**
     * Infotext to show if structure data is available.
     */
    @FXML
    private TextArea modelInfoText;
    /**
     * NextButton to navigate to the next scene.
     */
    @FXML
    private NextButton nextButton;
    /**
     * Pane which is the drag&drop area.
     */
    @FXML
    private Pane dropzoneArea;
    /**
     * Label for the drag&drop area.
     */
    @FXML
    private Label dropzoneLabel;
    /**
     * ListView which contains the MyInstances.
     */
    @FXML
    private ListView<MyInstances> dropzoneListView;
    /**
     * Clears the data from the listview.
     */
    @FXML
    private ClearUncheckedDataButton clearButton;
    /**
     * Data can be added to the listview via filechoser.
     */
    @FXML
    private AddUncheckedDataButton addButton;
    /**
     * The dataList for the Listview.
     */
    private static final ObservableList<MyInstances> dataList
            = FXCollections.observableArrayList();
    
    /**
     * Session object which contains the whole data 
     * like the model and the unclassified data.
     */
    private Session session;
    
    /**
     * Set which contains the filepaths of the different datasets.
     */
    private Set<String> filepaths;
    /**
     * Label which shows the amount of datasets, which are currently loaded.
     */
    @FXML
    private Label dataSetCountLabel;
    /**
     * Parent container of the controls.
     */
    @FXML
    private StackPane container;
    /**
     * 
     */
    @FXML
    private Button arrfFileContentTxtBtn;
    /**
     * Textarea which contains info of the arff file structure.
     */
    @FXML
    private TextArea arffFileContentTxtArea;
    /**
     * TitledPane which contains arffFileContentTxtArea.
     */
    @FXML
    private TitledPane arffFileContentTitledPane;
    /**
     * Imageview which used as a Button. 
     * If it's clicked the scene to create a new arff file is shown.
     */
    @FXML
    private ImageView newArffFileButton;
    /**
     * Loading indicator, which is shown if data is loading.
     */
    @FXML
    private ProgressIndicator loadingIndicator;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filepaths = new HashSet<>();
        initDataDropzone();
        initListView();
        initializeNewArffFileButton();
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
                    Platform.runLater(() -> {
                        nextButton.show();
                        setDataSetAmountLabel();
                    });
                } else {
                    Platform.runLater(() -> {
                        nextButton.hide();      
                        dataSetCountLabel.setText("Data set amount: 0");
                    });
                }
            }
        });

        dropzoneListView.setCellFactory((ListView<MyInstances> list) -> {
            final ListCell cell = setupCellFactory();
            return cell;            
        });

    }
    
    /**
     * Sets up the customized ListCell and adds EventListeners
     * @return The customized ListCell
     */
    private ListCell setupCellFactory(){
        final ListCell cell = new ListCell<MyInstances>() {
                @Override
                public void updateItem(MyInstances item, boolean empty) {
                    Platform.runLater(() -> {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                    });
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
                    checkIfDatalistIsEmptyAndSetVisibility();
                }
            });
            return cell;
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
                
                updateArffList();
                
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
            
            updateArffList();
        }
    }

    /**
     * Creates weka instances from files.
     * @param files List containing all the files
     */
    private void createInstancesFromFiles(List<File> files) {
        if (dropzoneListView.getItems().size() == 0) {
            dropzoneLabel.setVisible(false);
        }
        
        loadingIndicator.setVisible(true);
        
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        ArffFile arff;
                        MyInstances instances;

                        for (File file : files) {
                            try {
                                if (!filepaths.add(file.getPath())) throw new FileAlreadyAddedException();
                                arff = new ArffFile(file.getPath());
                                try {                    
                                    instances = arff.getInstances();
                                    session.setOriginalDataset(instances);
                                    Trainer.classifyData(session.getModel(), instances);
                                    dataList.add(instances);
                                } catch (ArffFile.ArffFileInvalidException | ArffFileIncompatibleException ex) {                    
                                    System.err.println("invalid arff file");
                                    if(filepaths.contains(file.getPath()))
                                        filepaths.remove(file.getPath());
                                    InfoDialog info = new InfoDialog("Invalid .arff-file!", container, "warning");
                                    checkIfDatalistIsEmptyAndSetVisibility();
                                }                
                            } catch (FileAlreadyAddedException ex) {
                                System.out.println("File already added!");
                                InfoDialog info = new InfoDialog("File already added!", container, "info");
                            }
                        }
                        return null;
                    }
                };
            }
            @Override
            protected void succeeded() {
                 loadingIndicator.setVisible(false);
                 updateArffList();                 
            }
        };
        service.start(); // starts Thread
    }
    
    /**
     * Checks if the data list is empty and sets the visibility of the buttons accordingly
     */
    private void checkIfDatalistIsEmptyAndSetVisibility() {
        if (dataList.isEmpty()) {
            dropzoneLabel.setVisible(true);
            changeDataButtonsVisibility(false);
        }else{
            dropzoneLabel.setVisible(false);
            changeDataButtonsVisibility(true);
        }
    }
    
    /**
     * Sets the session
     * @param session Session object which contains the data
     */
    public void setSession(Session session) {
        this.session = session;
        
        if(this.session.getModel().getEmptyArffFile() != null) {
            modelInfoText.setText("Arff-File structure: \n"
            + this.session.getModel().getEmptyArffFile().getArffFileContent() + "\n");            
        }else{
            modelInfoText.setText("No used arff-file found. \n");
            arffFileContentTitledPane.setVisible(false);
        }
        
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wekaui/scenes/ResultMain.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);            
            
            if(session.getModel().getEmptyArffFile() == null) {
                ArffFile arff = new ArffFile(filepaths.iterator().next());
                ArffFile f = arff.saveEmptyArffFile(arff, session.getModel().getFile().getName());
                session.getModel().setEmptyArffFile(f);                
                LastUsedModel.saveLastUsedModels(session.getLastUsedModelsList());
            }            

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
        
        checkIfDatalistIsEmptyAndSetVisibility();        
    }

    /**
     * Is called when the user clicks on the arffFileContentFileSaveButton.
     * Collects the data from the textarea and saves it as new a arff file.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void onArffFileContentFileSaveClicked(ActionEvent event) throws IOException {
        
        String fileToSave = getNewArffFileSaveLocation("dataset");
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
        writer.write(this.session.getModel().getEmptyArffFile().getArffFileContent() + arffFileContentTxtArea.getText());
        writer.flush();
        writer.close();
        
        List<File> tmp = new ArrayList<>();
        tmp.add(new File(fileToSave));                
        createInstancesFromFiles(tmp);
        
        updateArffList();
        
        //@TODO: what should be done if no valid data was added?!
        resetArffFileContentTxtArea();        
    }
    
    /**
     * Resets and empties the arffFileContentTxtArea
     */
    private void resetArffFileContentTxtArea(){
        arffFileContentTxtArea.setText("");
        arffFileContentTitledPane.setExpanded(false);        
    }
    
    /**
     * Is called when the user clicks on the newArffFileButton. Opens the new arff file dialog.
     * @param event 
     */
    private void onNewArffFileButtonClicked(MouseEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wekaui/scenes/NewArffFile.fxml"));            
            StackPane page = (StackPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create new Arff-File");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Node)event.getTarget()).getScene().getWindow());
            Scene scene = new Scene(page);            
            dialogStage.setScene(scene);
            
            NewArffFileController ctrl = loader.getController();
            ctrl.setDialogStage(dialogStage);
            dialogStage.showAndWait();            
            if(ctrl.getNewArffFileContent() == null || ctrl.getNewArffFileContent() == "")
                return;
                        
            ArffFile f = generateNewArffFile(ctrl.getNewArffFileContent());
                
            List<File> tmp = new ArrayList<>();
            tmp.add(f);
            createInstancesFromFiles(tmp);            
            updateArffList();
            
            InfoDialog infoAdded = new InfoDialog("Arff file created", container, "info");
            
        } catch (IOException ex) {
            Logger.getLogger(NewArffFileController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    /**
     * Generates new Arff File from given content
     * @param content
     * @return
     * @throws IOException 
     */
    private ArffFile generateNewArffFile(String content) throws IOException{
        
        String filePath = getNewArffFileSaveLocation("new_arff");        
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.flush();
        writer.close();       
        
        return new ArffFile(filePath);
    }
    
    /**
     * Gets the filepath according to the model location
     * @param fileName Name for the file to save
     * @return filepath as string
     */
    private String getNewArffFileSaveLocation(String fileName){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy HH-mm-ss");
        String dateToSave = dateFormat.format(new Date());
        
        String absolutePath = this.session.getModel().getFile().getAbsolutePath();
        String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
        String fileToSave = filePath + "/" + fileName + "_" + dateToSave + ".arff";
        
        return fileToSave;
    }
    
    /**
     * Initializes the NewArffFile button and adds event listener to it     
     */
    private void initializeNewArffFileButton(){
        newArffFileButton.setCursor(Cursor.HAND);
        
        newArffFileButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {                
                onNewArffFileButtonClicked(event);
        });
        
        newArffFileButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                newArffFileButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/create-new-arff-hover.png")));
                Tooltip t = new Tooltip("Create new arff file");
                Tooltip.install(newArffFileButton, t);
        });
        
        newArffFileButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                newArffFileButton.setImage(new Image(getClass().getResourceAsStream("/wekaui/customcontrols/create-new-arff.png")));
        });
    }
    
    /**
     * 
     */
    private void updateArffList(){
        session.setUnlabeledData(dataList);
        checkIfDatalistIsEmptyAndSetVisibility();
    }

}
