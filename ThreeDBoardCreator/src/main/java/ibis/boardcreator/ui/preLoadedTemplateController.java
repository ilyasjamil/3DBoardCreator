package ibis.boardcreator.ui;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
import ibis.boardcreator.datamodel.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class preLoadedTemplateController {

    
    @FXML
    private Canvas canvasGridDisplay;
    
    private Grid selectedGrid;
    
    private void drawSelected() {
    	double TILE_SIZE = canvasGridDisplay.getHeight()/Math.max(selectedGrid.getNumColumns(), selectedGrid.getNumRows());
    	GraphicsContext gc = canvasGridDisplay.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasGridDisplay.getHeight(), canvasGridDisplay.getWidth());
		for (int r = 0; r < selectedGrid.getNumRows(); r++) {
			for (int c = 0; c < selectedGrid.getNumColumns(); c++) {
				gc.setStroke(Color.CYAN);
				double x = c * TILE_SIZE;
				double y = r * TILE_SIZE;
				gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				Tile tile = selectedGrid.getTileAt(r, c);
				double elev = tile.getElevation();
				double grayVal = 1 - elev / 10;
				Color color = new Color(grayVal, grayVal, grayVal, 1);
				gc.setFill(color);
				gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);

			}
		}
    }

    @FXML
    void switchToMainEditor(ActionEvent event) throws IOException {
    	App.setGrid(selectedGrid);
    	App.setRoot("Main_Editor");
    	
    }

    @FXML
    void switchToMountains(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("mountains.OBJ"));
    	drawSelected();
    }
    

    @FXML
    void switchToBuildings(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("buildings.OBJ"));
    	drawSelected();
    }


    @FXML
    void switchToMaze(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("maze.OBJ"));
    	drawSelected();
    }

    @FXML
    void switchToRailroads(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("railroads.OBJ"));
    	drawSelected();
    }

    @FXML
    void switchToRoads(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("roads.OBJ"));
    	drawSelected();
    }

    @FXML
    void switchToStadium(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("stadium.OBJ"));
    	drawSelected();
    }

    @FXML
    void switchToValleys(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("valley.OBJ"));
    	drawSelected();
    }

    @FXML
    void switchToVolcanoes(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
    	selectedGrid = GridIO.load2dMapFromJSONFile(new File("volcano.OBJ"));
    	drawSelected();
    }
    
    

}
