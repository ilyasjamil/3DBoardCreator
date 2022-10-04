module edu.augustana.UX_mockup {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.augustana.UX_mockup to javafx.fxml;
    exports edu.augustana.UX_mockup;
}
