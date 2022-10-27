package ibis.boardcreator.ui;

import java.io.IOException;

import javafx.fxml.FXML;

public class MainEditorController {

    @FXML
    private void switchToThreeDPreview() throws IOException {
        App.setRoot("Three_D_Preview");
    }
    

}
