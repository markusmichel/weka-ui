/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.scenes.result;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import wekaui.Session;

/**
 * FXML Controller class
 *
 * @author FreshXL
 */
public class ResultMainController implements Initializable {

    private Session session;
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    public void setSession(Session s){
        this.session = s;
    }
    
}
