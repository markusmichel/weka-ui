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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import wekaui.Session;
import wekaui.logic.MyInstance;
import wekaui.logic.MyInstances;

/**
 * FXML Controller class
 *
 * @author FreshXL
 */
public class ResultMainController implements Initializable {

    private Session session;
    
    @FXML
    private ImageView exportBtn;
    @FXML
    private BorderPane container;
    @FXML
    private GridPane gridPane;
    @FXML
    private Slider thresholdSlider;
    
    /**
     * Contains the data for the piechart structured by the classes
     */
    private LinkedHashMap<String, List<MyInstance>> pieChartHashList;
    
    /**
     * List which contains the merged and ordered data
     */
    private List<MyInstance> mergedOrderedList;
    
    private List<MyInstance> mergedOrderedThresholdList;
    
    private double probabilityThreshold;
            
    private PieChart chart;
    @FXML
    private TextFlow detailTextContainer;
        
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        intitializeThresholdSlider();
        mergedOrderedThresholdList = new ArrayList<>();
        exportBtn.setCursor(Cursor.HAND);
    }    
    
    /**
     * Sets the session
     * @param s Session object which contains the data
     */
    public void setSession(Session s){
        this.session = s;        
        
        prepareDataForPieChart(this.session.getUnlabeledData());
        
        initializePieChart(mergedOrderedList);       
        
        exportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                exportInstances(event, s);
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
                        
                        for(MyInstance ins: l){                            
                            Label text = new Label(ins.getInstance().toString());
                            text.setMaxWidth(detailTextContainer.getWidth());
                            text.setWrapText(true);
                            detailTextContainer.getChildren().add(text);
                            Label seperator = new Label("----------------------");
                            detailTextContainer.getChildren().add(seperator);
                        }
                        
                                    
                        //@TODO show clicked data
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
     * Initializes the threshold slider and adds change listener to it
     */
    private void intitializeThresholdSlider(){
        // Listenener for slider changes
        thresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {                    
                    mergedOrderedThresholdList = getThresholdList(new_val);
                    initializePieChart(mergedOrderedThresholdList);
            }
        });
    }
    
    /**
     * Prepares a list of MyInstance according to the threshold
     * @param new_val The value from the slider
     * @return List of MyInstance according to the Threshold from the slider
     */
    private List<MyInstance> getThresholdList(Number new_val) {
        System.out.println("Slider value: " + (Double)new_val/100);
        
        mergedOrderedThresholdList.clear();
        for(MyInstance ins: mergedOrderedList){
            
            if(ins.maxProbability < ((Double)new_val/100)){
                System.out.println("MaxProbability: " + ins.maxProbability);
                System.out.println("BREAK");
                break;
            }else{
                System.out.println("Instance added");
                mergedOrderedThresholdList.add(ins);
            }
        }
        
        System.out.println("List size: " + mergedOrderedThresholdList.size());
        return mergedOrderedThresholdList;
    }
        
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
    
    private void safeArffFile(File file) {
        Instances dataSet = session.getUnlabeledData().get(0);
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
        mergedOrderedList = MyInstances.getMergedData(classifiedData);
        // sort the data
        mergedOrderedList = MyInstances.getOrderedData(mergedOrderedList);
    }
}
