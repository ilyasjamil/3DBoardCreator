package ibis.boardcreator.ui;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ResizeChoiceController {
	@FXML
	private TextField numColumns;

	@FXML
	private TextField numRows;

	@FXML
    private void switchToModifiedMainEditor() throws IOException {
		try {
			Grid grid = App.getGrid();
			grid.resize(Integer.parseInt(numRows.getText()), Integer.parseInt(numColumns.getText()));
			numColumns.getScene().getWindow().hide();
		
		}catch (NumberFormatException ext){
			Alert alert = new Alert(Alert.AlertType.WARNING,"Please insert a number");
			alert.show();
		}
        
    }
	
	@FXML
    void switchToMain(ActionEvent event) throws IOException {
		numColumns.getScene().getWindow().hide();
    	
    }
	
	
	
}
