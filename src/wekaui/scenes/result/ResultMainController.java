/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes.result;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
    private Button exportBtn;
    @FXML
    private BorderPane container;
    @FXML
    private GridPane gridPane;
    
    private LinkedHashMap<String, List<MyInstance>> list;
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    /**
     * Initializes the pie chart
     */
    private void initializePieChart(List<MyInstances> classifiedData){
        
        ObservableList<PieChart.Data> pieChartData = preparePieChartData(classifiedData);        
        
        final PieChart chart = new PieChart(pieChartData);
        
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
                        System.out.println("PieData: " + data);
                        List<MyInstance> l = getPieData(data.getName());
                        System.out.println("LIST: " + l);
                     }
                //returns the associated data of the pie slice
                private List<MyInstance> getPieData(String name) {
                    return list.get(name);
                }
            });
        }
                
        gridPane.add(chart,1,1,1,2);
    }
    
    /**
     * Sets the session
     * @param s Session object which contains the data
     */
    public void setSession(Session s){
        this.session = s;        
        
        initializePieChart(this.session.getUnlabeledData());
    }
    
    /**
     * Prepares the data for the PieChart.
     * @param classifiedData List which contains the MyInstances
     * @return PieChartData
     */
    private ObservableList<PieChart.Data> preparePieChartData(List<MyInstances> classifiedData) {
        
        // merges the data
        list = MyInstances.getMergedData(classifiedData);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();        
        Iterator it = list.entrySet().iterator();
        
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            int value = ((List)pair.getValue()).size();
            pieChartData.add(new PieChart.Data((String)pair.getKey(), value));
        }
        return pieChartData;        
    }
    
}
