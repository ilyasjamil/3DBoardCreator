package ibis.boardcreator.ui;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Grid grid = new Grid();

    public static Grid getGrid() {
		return grid;
	}

	public static void setGrid(Grid grid) {
		App.grid = grid;
	}

	@Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Main_Editor"), 640, 480);
        stage.setScene(scene);

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}