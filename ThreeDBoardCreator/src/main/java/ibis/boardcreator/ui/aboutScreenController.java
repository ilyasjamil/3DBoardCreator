package ibis.boardcreator.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class aboutScreenController {
	@FXML
	private Button exitButton;
	
	
	@FXML
    private void switchToMainEditor() throws IOException {
        exitButton.getScene().getWindow().hide();
    }
}
