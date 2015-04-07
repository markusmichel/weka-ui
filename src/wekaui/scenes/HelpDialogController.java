package wekaui.scenes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author FreshXL
 */
public class HelpDialogController implements Initializable {

    private Stage dialogStage;
    @FXML
    private TextArea generalText;
    @FXML
    private Text arffFileText;
    @FXML
    private VBox arffWebsiteContainer;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setGeneralHelpText();
        setArffHelpText();
    }
    
    private void setGeneralHelpText(){
        String generalHelpText = "First Step:\n"
                + "After a model was trained with the weka tool, it can be added on the 'Choose model' Screen. "
                + "This can be done by draging the model file in the dropzone or by clicking on "
                + "the dropzone and choose the file with a file-dialog.\n"
                + "Afterwards the training data for the model can be used to add "
                + "the arff-file structure to the model for future usages.\n"
                + "The selected model is saved in the list and can so be easily reused at a later time, "
                + "if it's not deleted from the system. "
                + "All the last used models is presented this way.\n\n\n"
                + "Second Step:\n"
                + "In the 'Choose unclassified data' screen the data, which is to be classified, is selected. "
                + "Again it can be done by dragging the file(s) in the dropzone or by clicking on "
                + "the dropzone and choose the file with a file-dialog. \n"
                + "Alternative if there was already an ARFF-File assigned to the model, the option to simply add just the '@DATA'-content "
                + "can be used. Simply open the intended input fields by clicking on 'Add arff-file content here'.\n"
                + "Another option to add unclassified data is to create a new ARFF-file with the 'Create new arff-file' button. This opens a form to "
                + "create an ARFF-file from scratch. After the form is filled out correctly, a new ARFF-file is saved to the system.\n\n\n"
                + "Third Step:\n"
                + "In the third step the result of the classified data is presented.\n"
                + "On the left general information is shown, like the amount classified data, "
                + "the average classify probabilty and the relative frequency of the different classes.\n"
                + "In the middle the classified data is visualized with a pie chart. Below the chart is a slider "
                + "to adjust the probability threshold. Adjusting the threshold changes the shown data. Any data below "
                + "the threshold is not shown.\n"
                + "On the right side the data details are shown. If the user clicks in the pie chart the related "
                + "data is shown here.\n"
                + "In upper right corner is the reset button to start the application from the beginning.\n"
                + "At the bottom are the export buttons:\n"
                + "One to export the pie chart and save it to the system as a PNG-File and the other to export and save the now "
                + "classified data as an ARFF-File.";
        
        generalText.setText(generalHelpText);
    }
    
    /**
     * Sets the ArffHelpText
     */
    private void setArffHelpText(){
        
        String arffUrl = "http://weka.wikispaces.com/ARFF+%28stable+version%29";
        
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.load(arffUrl);
        
        arffWebsiteContainer.getChildren().add(browser);
        VBox.setVgrow(browser, Priority.ALWAYS);
    }
    
    /**
     * Sets the stage context to close the pop up window
     * @param stage 
     */
    public void setDialogStage(Stage stage){
        this.dialogStage = stage;        
    }
    
}
