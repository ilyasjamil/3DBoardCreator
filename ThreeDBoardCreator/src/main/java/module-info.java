module ibis.boardcreator {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
    requires com.google.gson;
	requires javafx.base;
	requires org.junit.jupiter.api;
	

    opens ibis.boardcreator.ui to javafx.fxml;
    opens ibis.boardcreator.datamodel to com.google.gson;
    exports ibis.boardcreator.ui;
}
