/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes.result;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import wekaui.Session;
import wekaui.logic.MyInstance;
import wekaui.logic.MyInstances;
import wekaui.scenes.ChooseModelController;
import wekaui.scenes.ChooseUnclassifiedTextsController;

/**
 * FXML Controller class
 *
 * @author FreshXL
 */
public class ResultMainController implements Initializable {
    
    /**
     * Session object which contains the whole data 
     * like the model and the unclassified data.
     */
    private Session session;
    
    /**
     * Imageview which used as a Button. 
     * If it's clicked the data can be exported as a arrf file.
     */
    @FXML
    private ImageView exportBtn;
    /**
     * BorderPane of the screen.
     */
    @FXML
    private BorderPane container;
    /**
     * GridPane which contains the other FXML elements.
     */
    @FXML
    private GridPane gridPane;
    /**
     * Slider to control the probability threshold of the data.
     */
    @FXML
    private Slider thresholdSlider;
    
    /**
     * Contains the data for the piechart structured by the classes
     */
    private LinkedHashMap<String, List<MyInstance>> pieChartHashList;
    
    /**
     * Source list which contains the whole merged and ordered data. 
     * This list is populated intially with the data and is 
     * later used to copy data in the threshold list.
     */
    private List<MyInstance> mergedOrderedSourceList;
    
    /**
     * List which contains only the data to show. 
     * It initially gets all the data from mergeOrderedSourceList 
     * and also if the threshold changes.     * 
     */
    private List<MyInstance> mergedOrderedThresholdList;
            
    /**
     * The PieChar which visualizes the data.
     */
    private PieChart chart;
    /**
     * FXML Item which is used as a container to show the detail data.
     */
    @FXML
    private TextFlow detailTextContainer;
    /**
     * Imageview which used as a Button. 
     * If it's clicked the PieChart can be exported.
     */
    @FXML
    private ImageView exportChartBtn;
    /**
     * Scrollpane for the detailTextContainer.
     */
    @FXML
    private ScrollPane textScroll;
    @FXML
    private TextFlow dataInfoText;
    @FXML
    private ImageView restartButton;
    
    private double avgProbability;
    @FXML
    private Slider detailSlider;
        
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        mergedOrderedThresholdList = new ArrayList<>();
        initializeRestartAppButton();
    }    
    
    /**
     * Sets the session
     * @param s Session object which contains the data
     */
    public void setSession(Session s){
        this.session = s;        
        
        prepareDataForPieChart(this.session.getUnlabeledData());
        
        initializePieChart(mergedOrderedThresholdList);       
        
        initializeExportDataBtn(s);
        initializeExportChartButton(s);
        
        setInfoText();
        
        intitializeThresholdSlider();
    }
    
    /**
     * Initializes the export data button and adds event listener to it
     * @param s The session object, which contains the data to export
     */
    public void initializeExportDataBtn(Session s){
        exportBtn.setCursor(Cursor.HAND);
        
        exportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                exportInstances(event, s);
        });
        
        exportBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                exportBtn.setImage(new Image(getClass().getResourceAsStream("resources/export-button-hover.png")));
        });
        
        exportBtn.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                exportBtn.setImage(new Image(getClass().getResourceAsStream("resources/export-button.png")));
        });
    }
    
    /**
     * Initializes the export chart button and adds event listener to it
     * @param s The session object, which contains the data to export
     */
    public void initializeExportChartButton(Session s){
        exportChartBtn.setCursor(Cursor.HAND);
        
        exportChartBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                exportChart(event);
        });
        
        exportChartBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                exportChartBtn.setImage(new Image(getClass().getResourceAsStream("resources/export-chart-hover.png")));
        });
        
        exportChartBtn.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                exportChartBtn.setImage(new Image(getClass().getResourceAsStream("resources/export-chart.png")));
        });
    }
    
    /**
     * Initializes the restart application button and adds event listener to it     
     */
    public void initializeRestartAppButton(){
        restartButton.setCursor(Cursor.HAND);
        
        restartButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {                
                onRestartButtonClicked(event);
        });
        
        restartButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent event) -> {                
                restartButton.setImage(new Image(getClass().getResourceAsStream("resources/restart-button-hover.png")));
                Tooltip t = new Tooltip("Restarts the application");
                Tooltip.install(restartButton, t);
        });
        
        restartButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent event) -> {
                restartButton.setImage(new Image(getClass().getResourceAsStream("resources/restart-button.png")));
        });
    }
    
    /**
     * Initializes the pie chart
     */
    private void initializePieChart(List<MyInstance> classifiedData){
        
        if(gridPane.getChildren().contains(chart))
            gridPane.getChildren().remove(chart);
        
        ObservableList<PieChart.Data> pieChartData = preparePieChartData(classifiedData);        
        
        chart = new PieChart(pieChartData);
        
        for (final PieChart.Data data : chart.getData()) {
            
            Tooltip pieTip = new Tooltip("Amount: " + data.getPieValue());
            
            // adds the mouse enter event: ToolTip is shown
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        double x = e.getScreenX();
                        double y = e.getScreenY();                    
                        pieTip.show(data.getNode(), x + 10, y + 5);
                     }
                });
            
            // adds the mouse leave event: ToolTip is removed
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        pieTip.hide();
                     }
                });
            
            // adds the mouse click event: the data of the pie slice is shown
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        detailTextContainer.getChildren().clear();                        
                        List<MyInstance> l = getPieData(data.getName());
                        
                        int instanceCounter = 1;
                        
                        for(MyInstance ins: l){
                            Text instance = new Text("Instance No. " + instanceCounter +"\n\n");
                            instance.setStyle("-fx-font-weight: bold;");
                            detailTextContainer.getChildren().add(instance);    
                            
                            for(int i = 0; i < ins.getInstance().numAttributes(); i++){
                                
                                
                                String[] attributes = ins.getInstance().attribute(i).toString().split("\\s+");                                
                                String attrString = (attributes.length != 3) ? ins.getInstance().attribute(i).toString() : attributes[1];        
                                Text attr = new Text( attrString + ":\n\n");
                                attr.setUnderline(true);
                                
                                String contentString = ins.getInstance().stringValue(i).replace("\\", "");
                                Text content = new Text(contentString + "\n\n");
                                
                                detailTextContainer.getChildren().addAll(attr, content);
                            }
                            
                            Text seperator = new Text("\n\n-------------------------------------\n\n");
                            detailTextContainer.getChildren().add(seperator);
                            instanceCounter++;
                        }
                     }
                //returns the associated data of the pie slice
                private List<MyInstance> getPieData(String name) {
                    return pieChartHashList.get(name);
                }
            });
        }
                
        gridPane.add(chart,1,1,1,2);
    }
    
    /**
     * Prepares the data for the PieChart.
     * @param classifiedData List which contains the MyInstances
     * @return PieChartData
     */
    private ObservableList<PieChart.Data> preparePieChartData(List<MyInstance> classifiedData) {   
        
        pieChartHashList = getFormattedPieChartData(classifiedData);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();        
        Iterator it = pieChartHashList.entrySet().iterator();
        
        // fill the piechart according to the hashmap
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int value = ((List)pair.getValue()).size();
            pieChartData.add(new PieChart.Data((String)pair.getKey(), value));
        }
        return pieChartData;        
    }
    
    /**
     * Iterates through MyInstance List and fills a hashmap according to their classes
     * @param list List containing the merged data
     * @return A hashmap which contains the the formatted data for the piechart
     */    
    private LinkedHashMap<String, List<MyInstance>> getFormattedPieChartData(List<MyInstance> list){
        LinkedHashMap<String, List<MyInstance>> hashList = new LinkedHashMap<String, List<MyInstance>> ();
        
        // check list and fill hashlist according to the classes of the instances
        for(MyInstance ins: list){
            String classifiedClass = ins.getInstance().classAttribute().value((int)ins.getInstance().classValue());                
            
            // class is not yet in the hashmap, so a new list is added
            if(!hashList.containsKey(classifiedClass)){
                List<MyInstance> l = new ArrayList<>();
                l.add(ins);
                hashList.put(classifiedClass, l);
            }
            // class is allready in the hashmap, so the instance is added to the right list
            else{
                List<MyInstance> l = hashList.get(classifiedClass);
                l.add(ins);
                hashList.put(classifiedClass, l);
            }                
        }       
        
        return hashList;
    }
    
    /**
     * Initializes the thresholdSlider and detailSlider and adds change listener to it
     */
    private void intitializeThresholdSlider(){
        // Listenener for slider changes
        thresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {                    
                    mergedOrderedThresholdList = updateThresholdList(new_val);
                    initializePieChart(mergedOrderedThresholdList);
                    setInfoText();
                    updateDetailSlider((double)new_val);
            }
        });
        
        detailSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {                    
                    mergedOrderedThresholdList = updateThresholdList(new_val);
                    initializePieChart(mergedOrderedThresholdList);
                    setInfoText();
            }
        });
        
        updateDetailSlider(thresholdSlider.getValue());
    }
    
    /**
     * Updates the detailSlider according to the new value of the thresholdSlider
     * @param newValueToSet The new value of the thresholdSlider. It is used to set the detailSlider.
     */
    private void updateDetailSlider(double newValueToSet){
        double thresholdOffset = 20;
        double thresholdSliderMin = (newValueToSet - thresholdOffset < 0) ? 0 : newValueToSet - thresholdOffset ;        
        double thresholdSliderMax = (newValueToSet + thresholdOffset > 100) ? 100 : newValueToSet + thresholdOffset;        
        
        detailSlider.setMin((int)thresholdSliderMin);
        detailSlider.setMax((int)thresholdSliderMax);
        detailSlider.setMajorTickUnit(5);
        detailSlider.setMinorTickCount(5);
        detailSlider.setValue(newValueToSet);
    }
    
    /**
     * Prepares a list of MyInstance according to the threshold
     * @param new_val The value from the slider
     * @return List of MyInstance according to the Threshold from the slider
     */
    private List<MyInstance> updateThresholdList(Number new_val) {        
        
        mergedOrderedThresholdList.clear();
        for(MyInstance ins: mergedOrderedSourceList){
            
            if(ins.maxProbability < ((Double)new_val/100)){                
                break;
            }else{                
                mergedOrderedThresholdList.add(ins);
            }
        }
        
        return mergedOrderedThresholdList;
    }
    
    /**
     * Gets all the relevant data from the threshhold list and converts them to instances.
     * @param originalDataset
     * @return instances from thresholdlist
     */
    private Instances getInstancesFromThresholdlist(Instances originalDataset) {
        Instances newInstances = new Instances(originalDataset);
        newInstances.delete();
        for(MyInstance ins: mergedOrderedThresholdList) {
            newInstances.add(ins.getInstance());
        }
        
        return newInstances;
    }
    
    /**
     * Starts the FileChooser Dialog to save the data.
     * @param event MouseEvent
     * @param session Session object
     */    
    private void exportInstances(MouseEvent event, Session session) {    
        FileChooser fileChooser = new FileChooser();        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARFF files (*.arff)", "*.arff");
        fileChooser.getExtensionFilters().add(extFilter);
        Window window = ((Node)event.getTarget()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if(file != null){
            safeArffFile(file);
        }
    }
    
    /**
     * Saves the file in an arrf format.
     * @param file File which contains the data to export.
     */
    private void safeArffFile(File file) {
        //Instances dataSet = session.getUnlabeledData().get(0);
        Instances dataSet = getInstancesFromThresholdlist(session.getOriginalDataset());
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataSet);
        try {
            saver.setFile(file);
            saver.writeBatch();
        } catch (IOException ex) {
            Logger.getLogger(ResultMainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Merges and sorts a list of MyInstances and saves it in the mergedOrderedList variable
     * @param classifiedData A List of MyInstances
     */
    private void prepareDataForPieChart(List<MyInstances> classifiedData) {
        // merges the data
        mergedOrderedSourceList = MyInstances.getMergedData(classifiedData);
        // sort the data
        mergedOrderedSourceList = MyInstances.getOrderedData(mergedOrderedSourceList);
        
        // keep all the data in mergedOrderedList as a source
        for(MyInstance ins: mergedOrderedSourceList){
            mergedOrderedThresholdList.add(ins);
        }
    }
    
    /**
     * Starts the FileChooser Dialog to save the pie chart as png
     * @param event MouseEvent 
     */    
    private void exportChart(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        Window window = ((Node)event.getTarget()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if(file != null){
            safeChartAsFile(file);
        }
    }
    
    /**
     * Saves the piechart as png
     * @param file The File which contains the save path
     */
    private void safeChartAsFile(File file) {
        WritableImage img = chart.snapshot(new SnapshotParameters(), null);
        try{            
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
        } catch (IOException e){            
            Logger.getLogger(ResultMainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Sets the infotext for the user.
     */
    private void setInfoText() {        
        dataInfoText.getChildren().clear();
        
        //set dataset amount text
        Text amountText = new Text("Dataset amount: " + mergedOrderedThresholdList.size()+"\n\n");
        dataInfoText.getChildren().add(amountText);
        
        // set average classify probabilty
        double probSum = 0;
        for(MyInstance ins: mergedOrderedThresholdList){
            probSum += ins.maxProbability;
        }
        avgProbability = (probSum / mergedOrderedThresholdList.size()) * 100;
        avgProbability = Math.floor(avgProbability * 100)/100.0;
        Text probAccuracyText = new Text("Average classify accuracy: " + avgProbability + "\n\n");
        
        dataInfoText.getChildren().add(probAccuracyText);
        
        // set the relative frequency text
        Text relFreqText = new Text("Relative frequency: " + "\n");       
        
        Iterator it = pieChartHashList.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int value = ((List)pair.getValue()).size();
            String dataClass = pair.getKey().toString();
            double relFreq = ((double)value / (double)mergedOrderedThresholdList.size()) * 100;
            relFreq = Math.floor(relFreq * 100)/100.0;
            relFreqText.setText(relFreqText.getText() + "Class: " + dataClass + " --> " + relFreq + "\n");            
        }   
        dataInfoText.getChildren().add(relFreqText);
    }
    
    /**
     * Is called when the user clicks on the restart application button.
     * Resets all relevant variables and renders the first screen.
     * @param event 
     */
    private void onRestartButtonClicked(MouseEvent event) {
        
        session.resetSession();
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/wekaui/scenes/ChooseModel.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            ChooseModelController ctrl = loader.getController();
            ctrl.setSession(session);
        } catch (IOException ex) {
            Logger.getLogger(ChooseUnclassifiedTextsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
