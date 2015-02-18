/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes.result;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import wekaui.Session;

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
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        initializePieChart();
    }    
    
    private void initializePieChart(){
        
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Grapefruit", 13),
                new PieChart.Data("Oranges", 25),
                new PieChart.Data("Plums", 10),
                new PieChart.Data("Pears", 22),
                new PieChart.Data("Apples", 30));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Imported Fruits");        
        
        gridPane.add(chart,1,1,1,2);
    }
    
    public void setSession(Session s){
        this.session = s;
    }
    
}
