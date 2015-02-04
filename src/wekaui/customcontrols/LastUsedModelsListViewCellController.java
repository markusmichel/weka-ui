/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import wekaui.LastUsedModel;

/**
 * FXML Controller class
 *
 * @author markus
 */
public class LastUsedModelsListViewCellController {
    
    @FXML
    private VBox vBox;
    
    @FXML
    private Label labelFilename;
    
    @FXML
    private Label labelLastUsedDate;

    public LastUsedModelsListViewCellController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LastUsedModelsListViewCell.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(LastUsedModelsListViewCell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public VBox createCell(LastUsedModel model) {        
        labelFilename.setText("last used model name");
        labelLastUsedDate.setText("last used date");
        
        return vBox;
    }
    
}
