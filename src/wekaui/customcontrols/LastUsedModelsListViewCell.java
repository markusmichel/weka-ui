/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaui.customcontrols;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import wekaui.LastUsedModel;

/**
 * FXML Controller class
 *
 * @author markus
 */
public class LastUsedModelsListViewCell extends ListCell<LastUsedModel> {

    @Override
    protected void updateItem(LastUsedModel item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            LastUsedModelsListViewCellController ctrl = new LastUsedModelsListViewCellController();
            VBox content = ctrl.createCell(item);
            setGraphic(content);
            
            setFocused(false);
            setPressed(false);
        }
    }
}
