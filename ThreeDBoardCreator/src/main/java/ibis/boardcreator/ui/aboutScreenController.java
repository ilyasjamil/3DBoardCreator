package ibis.boardcreator.ui;

import java.io.IOException;

import javafx.fxml.FXML;

public class aboutScreenController {
	@FXML
    private void switchToMainEditor() throws IOException {
        App.setRoot("Main_Editor");
    }
}
