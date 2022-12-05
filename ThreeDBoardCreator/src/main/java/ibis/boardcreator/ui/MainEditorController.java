package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import javafx.scene.control.TextField;
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
	private ToggleButton resizeButton;

	@FXML
	private ToggleGroup toolButtonsGroup;
	

	@FXML
	private Menu aboutScreen;

	private HashSet<Tile> clickedTileSet;

	private double TILE_SIZE;
	private UndoRedoHandler undoRedoHandler;
	
	@FXML
	private TextField numColumns;

	@FXML
	private TextField numRows;

	@FXML
	private void initialize() {
		Grid grid = App.getGrid();

		TILE_SIZE = canvasGrid.getHeight() / Math.max(grid.getNumColumns(), grid.getNumRows());
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
		clickedTileSet = new HashSet<>();
		undoRedoHandler = new UndoRedoHandler(createMemento());
		drawGrid();
	}

	// draws grid
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
				} else if (toolButtonsGroup.getSelectedToggle() == clearMapBtn) {
					tile.setElevation(0);
					gc.setStroke(Color.CYAN);
				} else {
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
		if (clickedTileSet.size() == 2) {
			int startRow = 1000;
			int endRow = -1;
			int startCol = 1000;
			int endCol = -1;
			for (Tile tile : clickedTileSet) {
				if (tile.getRow() > endRow) {
					endRow = tile.getRow();
				}
				if (tile.getRow() < startRow) {
					startRow = tile.getRow();
				}
				if (tile.getColumn() > endCol) {
					endCol = tile.getColumn();
				}
				if (tile.getColumn() < startCol) {
					startCol = tile.getColumn();
				}
			}
			for (int r = startRow; r <= endRow; r++) {
				for (int c = startCol; c <= endCol; c++) {

					clickedTileSet.add(grid.getTileAt(r, c));
					drawGrid();
				}
			}

		}

		drawGrid();
		undoRedoHandler.saveState(createMemento());

	}

	@FXML
	void clearSelected() {
		for (Tile tile : clickedTileSet) {
			tile.setElevation(0);
		}
		drawGrid();
		undoRedoHandler.saveState(createMemento());
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
		undoRedoHandler.saveState(createMemento());

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
		undoRedoHandler.saveState(createMemento());
	}

	@FXML
	public void unSelectPressed() {
		clickedTileSet.clear();
		drawGrid();
		undoRedoHandler.saveState(createMemento());
	}

	private void handleCanvasMousePress(MouseEvent evt) {
		if (toolButtonsGroup.getSelectedToggle() == selectButton) {
			int c = (int) (evt.getX() / TILE_SIZE);
			int r = (int) (evt.getY() / TILE_SIZE);
			Tile clickedTile = App.getGrid().getTileAt(r, c);
			clickedTileSet.add(clickedTile);
			drawGrid();
			undoRedoHandler.saveState(createMemento());

		} else if (toolButtonsGroup.getSelectedToggle() != null) {
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
		} else if (toolButtonsGroup.getSelectedToggle() == lowerElevationButton) {
			newElevation = tile.getElevation() - sliderValue;
		}
		if (newElevation >= 10) {
			newElevation = 10;
		} else if (newElevation <= 0) {
			newElevation = 0;
		}
		tile.setElevation(newElevation);
		drawGrid();
		currentTileModified = tile;
		undoRedoHandler.saveState(createMemento());
	}

	private void handleCanvasMouseDrag(MouseEvent evt) {
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile dragTile = App.getGrid().getTileAt(r, c);
		if (toolButtonsGroup.getSelectedToggle() == selectButton) {
			clickedTileSet.add(dragTile);
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
		if (dragTile != currentTileModified && toolButtonsGroup.getSelectedToggle() != selectButton
				&& toolButtonsGroup.getSelectedToggle() != null) {
			adjustTileHeight(dragTile);
		}
	}

	@FXML
	void clearMapPressed(ActionEvent event) {
		drawGrid();

		undoRedoHandler.saveState(createMemento());
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
	public void exportOBJAction() {
		FileChooser saveChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OBJ File(*.OBJ)", "*.OBJ");
		saveChooser.getExtensionFilters().add(extFilter);
		File outputFile = saveChooser.showSaveDialog(App.getMainWindow());
		if (outputFile != null) {
			Grid grid = App.getGrid();
			Tile[][] board = grid.getBoard();
		    try {
				FileWriter writer = new FileWriter(outputFile);
				writer.write("o "+1+System.lineSeparator());
				for (int i = 0; i < board.length;i++) {
					for (int j = 0; j < board[0].length;j++) {
						Tile tile = board[i][j];
						int r = tile.getRow();
						int c = tile.getColumn();
						double e = tile.getElevation();
						writer.write("v "+c+" "+r+" "+e+System.lineSeparator());
						writer.write("v "+c+" "+r+" "+0+System.lineSeparator());
						writer.write("v "+c+" "+String.valueOf(r+1)+" "+0+System.lineSeparator());
						writer.write("v "+c+" "+String.valueOf(r+1)+" "+e+System.lineSeparator());
						writer.write("v "+String.valueOf(c+1)+" "+r+" "+e+System.lineSeparator());
						writer.write("v "+String.valueOf(c+1)+" "+r+" "+0+System.lineSeparator());
						writer.write("v "+String.valueOf(c+1)+" "+String.valueOf(r+1)+" "+0+System.lineSeparator());
						writer.write("v "+String.valueOf(c+1)+" "+String.valueOf(r+1)+" "+e+System.lineSeparator());
					}
				}
				writer.write("usemtl Default"+System.lineSeparator());
				
				for (int i = 0; i < board.length*board[0].length;i++) {
						writer.write("f "+ String.valueOf(8*i+4)+ " "+ String.valueOf(8*i+3) +" "+ String.valueOf(8*i+2) +" "+ String.valueOf(8*i+1) +System.lineSeparator());
						writer.write("f "+ String.valueOf(8*i+2)+ " "+ String.valueOf(8*i+6) +" "+ String.valueOf(8*i+5) +" "+ String.valueOf(8*i+1) +System.lineSeparator());
						writer.write("f "+ String.valueOf(8*i+3)+ " "+ String.valueOf(8*i+7) +" "+ String.valueOf(8*i+6) +" "+ String.valueOf(8*i+2) +System.lineSeparator());
						writer.write("f "+ String.valueOf(8*i+8)+ " "+ String.valueOf(8*i+7) +" "+ String.valueOf(8*i+3) +" "+ String.valueOf(8*i+4) +System.lineSeparator());
						writer.write("f "+ String.valueOf(8*i+5)+ " "+ String.valueOf(8*i+8) +" "+ String.valueOf(8*i+4) +" "+ String.valueOf(8*i+1) +System.lineSeparator());
						writer.write("f "+ String.valueOf(8*i+6)+ " "+ String.valueOf(8*i+7) +" "+ String.valueOf(8*i+8) +" "+ String.valueOf(8*i+5) +System.lineSeparator());
				}
				writer.close();
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	@FXML
	void redoAction(ActionEvent event) {
		EditorState state = undoRedoHandler.redo();
		state.restore();
	}

	@FXML
	void undoAction(ActionEvent event) {
		EditorState state = undoRedoHandler.undo();
		state.restore();

	}
	
	
	private void resizeAction() throws IOException {
		if (toolButtonsGroup.getSelectedToggle() == resizeButton) {
			try {
				Grid grid = App.getGrid();
				grid.resize(Integer.parseInt(numRows.getText()), Integer.parseInt(numColumns.getText()));
				App.setGrid(grid);
				App.setRoot("Main_Editor");
			}catch (NumberFormatException ext){
				Alert alert = new Alert(Alert.AlertType.WARNING,"Please insert a number");
				alert.show();
			}
			
		}
		drawGrid();
		undoRedoHandler.saveState(createMemento());
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

	public EditorState createMemento() {
		return new EditorState();
	}
	

	public class EditorState {
		private Grid clonedGrid;
		private HashSet<Tile> selectionSet;

		public EditorState() {
			clonedGrid = App.getGrid().clone();
			selectionSet = new HashSet<Tile>();
			// loop through the current selected tile set
			// and add the corresponding tile (at row,col) from the clonedgrid
			// into the new set;
			for (Tile tile : clickedTileSet) {
				selectionSet.add(clonedGrid.getTileAt(tile.getRow(), tile.getColumn()));
			}
		}

		public void restore() {
			App.setGrid(clonedGrid);
			clickedTileSet = selectionSet;
			drawGrid();
		}
	}
	

}
