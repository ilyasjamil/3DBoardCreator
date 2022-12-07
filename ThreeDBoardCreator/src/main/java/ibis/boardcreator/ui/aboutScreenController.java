package ibis.boardcreator.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class aboutScreenController {
	@FXML
	private Button editButton;
	
	
	@FXML
    private void switchToMainEditor() throws IOException {
        editButton.getScene().getWindow().hide();
    }
}
