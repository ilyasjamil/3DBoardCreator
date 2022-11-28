package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
import ibis.boardcreator.datamodel.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainEditorController {

	
	
	private Tile currentTileModified = new Tile(-1, -1, 0);

	@FXML
	private Canvas canvasGrid;

	@FXML
	private Slider elevationSlider;
	
    @FXML
    private ToggleButton selectButton;

    @FXML
    private Button setBtn;
    
    @FXML
    private Button clearMapBtn;
    
    @FXML
    private ToggleButton lowerElevationButton;

    @FXML
    private ToggleButton raiseElevationButton;

    @FXML
    private ToggleGroup toolButtonsGroup;

	@FXML
	private Menu aboutScreen;
	
	private HashSet<Tile> clickedTileSet;

	private double TILE_SIZE;

	@FXML
	private void initialize() {
		Grid grid = App.getGrid();
		TILE_SIZE = canvasGrid.getHeight()/Math.max(grid.getNumColumns(), grid.getNumRows());
		drawGrid();
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
		clickedTileSet = new HashSet<>();
	}

	//draws grid
	private void drawGrid() {
		Grid grid = App.getGrid();
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasGrid.getHeight(), canvasGrid.getWidth());
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				gc.setStroke(Color.CYAN);
				double x = c * TILE_SIZE;
				double y = r * TILE_SIZE;
				gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				Tile tile = grid.getTileAt(r, c);
				double elev = tile.getElevation();
				double grayVal = 1 - elev / 10;
				Color color = new Color(grayVal, grayVal, grayVal, 1);
				gc.setFill(color);
				gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			}
		}
	}
	
    @FXML
    void setPressed(ActionEvent event) {
    	if (!clickedTileSet.isEmpty()) {
    		for (Tile tile : clickedTileSet) {
    			double newElevation = tile.getElevation() + elevationSlider.getValue();
    			tile.setElevation(newElevation);
        	}
    		drawGrid();
    	}
		clickedTileSet.clear();
    }
	private void handleCanvasMousePress(MouseEvent evt) {
		if(toolButtonsGroup.getSelectedToggle() == selectButton) {
			int c = (int) (evt.getX() / TILE_SIZE);
			int r = (int) (evt.getY() / TILE_SIZE);
			Tile clickedTile = App.getGrid().getTileAt(r, c);
			clickedTileSet.add(clickedTile);
			highlightSelectedTile(c, r);
		}else if(toolButtonsGroup.getSelectedToggle() != null){
			int c = (int) (evt.getX() / TILE_SIZE);
			int r = (int) (evt.getY() / TILE_SIZE);
			Tile clickedTile = App.getGrid().getTileAt(r, c);
			adjustTileHeight(clickedTile);
		}
		
	}
	
	private void adjustTileHeight(Tile tile) {
		double sliderValue = elevationSlider.getValue();
		double newElevation = -1;
		if (toolButtonsGroup.getSelectedToggle() == raiseElevationButton) {
			newElevation = tile.getElevation() + sliderValue;
		}else if(toolButtonsGroup.getSelectedToggle() == lowerElevationButton) {
			newElevation = tile.getElevation() - sliderValue;
		}
		if (newElevation >= 10) {
			newElevation = 10;
		}else if (newElevation <= 0) {
			newElevation = 0;
		}
		tile.setElevation(newElevation);
		drawGrid();
		currentTileModified = tile;
	}
	
	private void handleCanvasMouseDrag(MouseEvent evt) {
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile dragTile = App.getGrid().getTileAt(r, c);
		if (toolButtonsGroup.getSelectedToggle() == selectButton) {
			clickedTileSet.add(dragTile);
			dragTile.setElevation(0);
			highlightSelectedTile(c, r);
		}
		if (dragTile != currentTileModified && toolButtonsGroup.getSelectedToggle() != selectButton && toolButtonsGroup.getSelectedToggle() != null) {
			adjustTileHeight(dragTile);
		}
		

	}
	
	private void highlightSelectedTile(int column, int row) {
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		gc.setStroke(Color.CRIMSON);
		gc.strokeRect(column * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		gc.setFill(Color.ALICEBLUE);
		gc.fillRect(column * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}
	
    @FXML
    void clearMapPressed(ActionEvent event) {
    	Grid grid = App.getGrid();
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				Tile tile = grid.getTileAt(r, c);
				tile.setElevation(0);
			}
		}
		drawGrid();
    }

	@FXML
	void openFileAction(ActionEvent event) {
		FileChooser openChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.OBJ)", "*.OBJ");
		openChooser.getExtensionFilters().add(extFilter);
		File inputFile = openChooser.showOpenDialog(App.getMainWindow());
		if (inputFile != null) {
			try {
				Grid grid = GridIO.load2dMapFromJSONFile(inputFile);
				App.setGrid(grid);
				drawGrid();
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open could not be found.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR,
						"Error opening file.  Did you choose a valid .movieList file (which uses JSON format?)").show();
			}
		}
	}

	@FXML
	void saveFileAsAction(ActionEvent event) {
		FileChooser saveChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.MAP)", "*.OBJ");
		saveChooser.getExtensionFilters().add(extFilter);
		File outputFile = saveChooser.showSaveDialog(App.getMainWindow());
		if (outputFile != null) {
			Grid grid = App.getGrid();
			try {
				GridIO.save2dMapAsJSONFile(grid, outputFile);
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "An I/O error occurred while trying to save this file.").showAndWait();
			}
		}
	}


	@FXML
	private void switchToThreeDPreview() throws IOException {
		Stage stage = new Stage();
		ThreeDPreviewController j = new ThreeDPreviewController();
		j.start(stage);

	}

	@FXML
	private void switchToAboutScreen() throws IOException {
		App.setRoot("AboutScreen");

	}
	
	@FXML
	private void switchToPreloadedScreen() throws IOException {
		App.setRoot("preLoaded_Preview");

	}
	@FXML
	private void switchToEmptyGrid() throws IOException {
		App.setRoot("emptyGridGenerator");

	}

}
