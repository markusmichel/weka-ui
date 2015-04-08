/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import wekaui.customcontrols.InfoDialog;

/**
 * FXML Controller class
 *
 * Handles the NewArffFile inputs
 */
public class NewArffFileController implements Initializable {
    
    /**
     * VBox container for the attribute input fields.
     */
    @FXML
    private VBox attributeVbox;
    /**
     * Button to add a new attribute input field.
     */
    @FXML
    private Button addNewAttributeButton;
    /**
     * Button to create new arff file and send the content to the other scene.
     */
    @FXML
    private Button createNewArffFileButton;
    /**
     * Textfield for the relation attribute of the arff file.
     */
    @FXML
    private TextField relationText;
    /**
     * Textfield for the class attribute of the arff file.
     */
    @FXML
    private TextField classText;
    /**
     * Textarea for the data part of the arff file.
     */
    @FXML
    private TextArea dataTextArea;
    /**
     * String which contains the whole content for the new arff.
     */
    private String newArffFileContent = "";
    /**
     * Stage object to handle the visibility of the window.
     */
    private Stage dialogStage;
    /**
     * Stackpane container for the controls.
     * Is needed, to show the InfoDialog properly.
     */
    @FXML
    private StackPane newArffContainer;    
    /**
     * InfoText for the relation input field.
     */
    @FXML
    private Label relationInfoText;
    /**
     * InfoText for the attribute input field.
     */
    @FXML
    private Label attributeInfoText;
    /**
     * InfoText for the class input field.
     */
    @FXML
    private Label classInfoText;
    /**
     * InfoText for the data input field.
     */
    @FXML
    private Label dataInfoText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        addNewAttributeInput();
        setInfoTexts();
    }
    
    /**
     * Sets the texts for info fields.
     */
    private void setInfoTexts(){
        
        relationInfoText.setText("Info: The internal title of your ARFF-file.\n"
                + "Spaces has to be quoted and no special characters allowed!");
        
        attributeInfoText.setText("Info: The different attributes with the types of the data.\n"                
                + "Type: NUMERIC - Simple numbers, can be real or integer numbers.\n"
                + "Type: STRING - Plain text.\n"                
                + "Type: DATE - Date Format.\n"
                + "Type: NOMINAL - Option for listing the possible values: {<nominal-name1>, <nominal-name2>, ...}");
        
        classInfoText.setText("Info: the classes into which the data are categorized.\n"
                + "Important: has to be comma-seperated, e.g.: neg,pos !");
        
        dataInfoText.setText("Info: the actual data which has to be classified.\n"
                + "Important: has to match the specified attributes from above!");
    }
    /**
     * Adds a new Attribute Input Group to attributeVbox
     */
    private void addNewAttributeInput(){
        TextField text = new TextField();        
        Button button = new Button("X");
        button.setOnAction((ActionEvent event) -> {
            HBox buttonParent = (HBox) button.getParent();
            int index = attributeVbox.getChildren().indexOf(buttonParent);
            attributeVbox.getChildren().remove(index);
        });
        
        ComboBox combo = new ComboBox();
        combo.setPromptText("Attribute Type");
        combo.getItems().addAll( "NUMERIC", "STRING", "DATE", "NOMINAL" );
        
        HBox hbox = new HBox();
        hbox.getChildren().add(text);
        hbox.getChildren().add(combo);
        hbox.getChildren().add(button);
        
        attributeVbox.getChildren().add(hbox);        
    }
    
    /**
     * Is called when the user clicks on the NewAttribute Button
     * @param event The ActionEvent
     */
    @FXML
    private void onNewAttributeButtonClicked(ActionEvent event) {
        addNewAttributeInput();
    }
    
    /**
     * Is called when the user clicks on the NewArffFileButton. Collects the data from the input fields.
     * Sets the newArffFileContent variable and closes the window if valid.
     * @param event 
     */
    @FXML
    private void onNewArffFileButtonClicked(ActionEvent event) {        
        if(dataIsValid()){
            String relation = "@RELATION " + relationText.getText() + "\n\n";
            String attributes = "@ATTRIBUTE " + getAllAttributesText();
            String classAttr = "@ATTRIBUTE class {" + classText.getText() + "}\n\n";
            String data = "@DATA \n" + dataTextArea.getText();
        
            this.newArffFileContent = relation + attributes + classAttr + data;        
        
            dialogStage.close();
        }
    }
    
    /**
     * Iterates through all attribute inputs and collects the whole info
     * @return The whole text from all attribute inputs.
     */
    private String getAllAttributesText() {
        String attributes = "";
        for(int i = 0; i < attributeVbox.getChildren().size(); i++){
            if( attributeVbox.getChildren().get(i) instanceof HBox ){
                HBox hbox = (HBox) attributeVbox.getChildren().get(i);
                for(int j = 0; j < hbox.getChildren().size(); j++){                    
                    
                    if(hbox.getChildren().get(j) instanceof TextField){
                        TextField text = (TextField) hbox.getChildren().get(j);
                        attributes += text.getText();
                    }
                    
                    if(hbox.getChildren().get(j) instanceof ComboBox){
                        ComboBox combo = (ComboBox) hbox.getChildren().get(j);
                        if(combo.getValue() != null)
                            attributes +=  " " + combo.getValue().toString() + "\n";
                    }                   
                }
                
            }
        }        
        return attributes;
    }
    
    /**
     * Sets the stage context to close the pop up window
     * @param dialogStage 
     */
    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;        
    }
    
    /**
     * Returns the new content for the arff file as a string
     * @return the new content for the arff file
     */
    public String getNewArffFileContent(){
        return this.newArffFileContent;
    }
    
    /**
     * Checks if the attribute input fields have valid entries.
     * @return true if they are valid, false if not
     */
    private boolean checkIfAttributeInputFieldsAreValid(){
        boolean isValid = true;
        
        for(int i = 0; i < attributeVbox.getChildren().size(); i++){
            if( attributeVbox.getChildren().get(i) instanceof HBox ){
                HBox hbox = (HBox) attributeVbox.getChildren().get(i);
                for(int j = 0; j < hbox.getChildren().size(); j++){                    
                    
                    if(hbox.getChildren().get(j) instanceof TextField){
                        TextField text = (TextField) hbox.getChildren().get(j);
                        if(text.getText() == null || text.getText().length() == 0){
                            isValid = false;                            
                            break;
                        }
                    }
                    
                    if(hbox.getChildren().get(j) instanceof ComboBox){
                        ComboBox combo = (ComboBox) hbox.getChildren().get(j);
                        if(combo.getValue() == null){
                            isValid = false;
                            break;
                        }
                    }
                }
            }
            if(!isValid) break;
        }        

        return isValid;
    }
    
    /**
     * Checks if the form is valid.
     * @return true if the form is valid, false otherwise.
     */
    private boolean dataIsValid() {
        boolean isValid = true;
        String validateErrorMsg = "";
        if(relationText.getText() == null || relationText.getText().length() == 0){
            isValid = false;
            validateErrorMsg += "Invalid relation! \n";
        }
        if(!checkIfAttributeInputFieldsAreValid()){
            isValid = false;
            validateErrorMsg += "Invalid attributes! \n";
        }        
        if(classText.getText() == null || classText.getText().length() == 0){
            isValid = false;
            validateErrorMsg += "Invalid class! \n";
        }
        if(dataTextArea.getText() == null || dataTextArea.getText().length() == 0){
            isValid = false;
            validateErrorMsg += "Invalid data! \n";
        }
        
        if(!isValid){
            InfoDialog info = new InfoDialog(validateErrorMsg, newArffContainer, "warning");
        }
        
        return isValid;
    }
}
