package ibis.boardcreator.ui;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;



public class emptyBoardController {
	
	@FXML
	private TextField numColumns;

	@FXML
	private TextField numRows;

	@FXML
    private void switchToMainEditor() throws IOException {
		try {
			Grid grid = new Grid(Integer.parseInt(numRows.getText()), Integer.parseInt(numColumns.getText()));
			App.setGrid(grid);
			App.setRoot("Main_Editor");
		}catch (NumberFormatException ext){
			Alert alert = new Alert(Alert.AlertType.WARNING,"Please insert a number");
			alert.show();
		}
        
    }
	
	@FXML
    void switchToMain(ActionEvent event) throws IOException {
    	App.setRoot("Main_Editor");
    	
    }
}