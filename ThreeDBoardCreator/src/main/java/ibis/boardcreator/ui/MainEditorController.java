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
    private ToggleButton unselectBtn;
    
    @FXML
    private ToggleButton clearMapBtn;
    
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
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
		clickedTileSet = new HashSet<>();
		drawGrid();
	}

	//draws grid
	private void drawGrid() {
		Grid grid = App.getGrid();	
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasGrid.getHeight(), canvasGrid.getWidth());
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				double x = c * TILE_SIZE;
				double y = r * TILE_SIZE;
				Tile tile = grid.getTileAt(r, c);
				if (clickedTileSet.contains(tile)) {
					gc.setStroke(Color.RED);
//					gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				}else if(toolButtonsGroup.getSelectedToggle() == clearMapBtn){
					tile.setElevation(0);
					gc.setStroke(Color.CYAN);
				}else {
					gc.setStroke(Color.CYAN);
				}
				gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				double elev = tile.getElevation();
				double grayVal = 1 - elev / 10;
				Color color = new Color(grayVal, grayVal, grayVal, 1);
				gc.setFill(color);
				gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
			}
				
		}
	}

	
	@FXML
	void selectedARegion() {
		Grid grid = App.getGrid();
		if(clickedTileSet.size()==2) {
			int startRow = 1000;
			int endRow=-1;
			int startCol=1000;
			int endCol=-1;
			for(Tile tile:clickedTileSet) {
				if(tile.getRow()>endRow) {
					endRow = tile.getRow();
				}if(tile.getRow()<startRow) {
					startRow=tile.getRow();
				}if(tile.getColumn()>endCol) {
					endCol= tile.getColumn();
				}if(tile.getColumn()<startCol) {
					startCol= tile.getColumn();
				}
			}
			for (int r = startRow; r <= endRow; r++) {
				for (int c = startCol; c <= endCol; c++) {
					clickedTileSet.add(grid.getTileAt(r, c));
					drawGrid();
				}
			}
		}
		
	}
	
	@FXML
	void clearSelected() {
		for (Tile tile: clickedTileSet) {
			tile.setElevation(0);
		}
		drawGrid();
	}
	

	
    @FXML
    void setPressed(ActionEvent event) {
    	if (!clickedTileSet.isEmpty()) {
    		for (Tile tile : clickedTileSet) {
    			double newElevation = elevationSlider.getValue();
    			tile.setElevation(newElevation);
        	}
    		drawGrid();
    	}
 
    }
    
    
    @FXML
    public void selectHeight() {
    	Grid grid = App.getGrid();
    	HashSet<Double> selectHeights = new HashSet<>();
    	if (clickedTileSet.size() == 1) {
    		for (Tile tile : clickedTileSet) {
        		selectHeights.add(tile.getElevation());
        	}
    	}
    	for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				if (selectHeights.contains(grid.getTileAt(r, c).getElevation())) {
					clickedTileSet.add(grid.getTileAt(r, c));
					drawGrid();
				}
			}
    	}
    }
    
//    @FXML
//    public void copy() {
//    	double[][] tileHeights = new double[5][5];
//    	for (Tile tile: clickedTileSet) {
//    		tileHeights.tile.getElevation()
//    	}
//    }
    
    @FXML
    public void unSelectPressed() {
    	clickedTileSet.clear();
		drawGrid();
    }
    
	private void handleCanvasMousePress(MouseEvent evt) {
		if(toolButtonsGroup.getSelectedToggle() == selectButton) {
			int c = (int) (evt.getX() / TILE_SIZE);
			int r = (int) (evt.getY() / TILE_SIZE);
			Tile clickedTile = App.getGrid().getTileAt(r, c);
			clickedTileSet.add(clickedTile);
			drawGrid();
		
		}else if(toolButtonsGroup.getSelectedToggle() != null){
			int c = (int) (evt.getX() / TILE_SIZE);
			int r = (int) (evt.getY() / TILE_SIZE);
			Tile clickedTile = App.getGrid().getTileAt(r, c);
			adjustTileHeight(clickedTile);
		}
	}
	
	private void adjustTileHeight(Tile tile) {
		double sliderValue = elevationSlider.getValue();
		double newElevation = 0;
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
			drawGrid();
		}
		if (dragTile != currentTileModified && toolButtonsGroup.getSelectedToggle() != selectButton && toolButtonsGroup.getSelectedToggle() != null) {
			adjustTileHeight(dragTile);
		}
	}
	
	
	@FXML
	void clearMapPressed(ActionEvent event) {   	
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
