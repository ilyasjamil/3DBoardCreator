module ibis.boardcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens ibis.boardcreator.ui to javafx.fxml;
    opens ibis.boardcreator.datamodel to com.google.gson;
    exports ibis.boardcreator.ui;
}
