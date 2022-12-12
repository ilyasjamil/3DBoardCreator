package ibis.boardcreator.ui;

import java.io.IOException;
import java.util.HashSet;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ibis.boardcreator.datamodel.Features;
import ibis.boardcreator.datamodel.FilesIO;
import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
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
	private Button clearTilesBtn;

	@FXML
	private ToggleButton clearMapBtn;

	@FXML
	private ToggleButton lowerElevationButton;

	@FXML
	private ToggleButton raiseElevationButton;

	@FXML
	private ToggleButton resizeButton;

	@FXML
	private Button pointyTileButton;

	@FXML
	private ToggleGroup toolButtonsGroup;

	@FXML
	private Menu aboutScreen;

	private HashSet<Tile> clickedTileSet;

	private UndoRedoHandler undoRedoHandler;

	@FXML
	private TextField numColumns;

	@FXML
	private ComboBox<String> featuresComboBox;

	@FXML
	private TextField numRows;

	FilesIO file = new FilesIO();

	/**
	 * Initialize the controller
	 */
	@FXML
	private void initialize() {
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
		canvasGrid.setOnMouseReleased(evt -> handleCanvasMouseReleased(evt));
		clickedTileSet = new HashSet<>();
		undoRedoHandler = new UndoRedoHandler(createMemento());

		featuresComboBox.getItems().add("Mountain");
		featuresComboBox.getItems().add("Pit");
		featuresComboBox.getItems().add("Volcano");
		featuresComboBox.getItems().add("Augie A");
		featuresComboBox.getItems().add("Road");

		clearMapBtn.setStyle("-fx-background-color: #32a5cb");
		resizeButton.setStyle("-fx-background-color: #7ababb");
		drawGrid();
		Platform.runLater(new Runnable() {
			public void run() {
				initShortcuts();
			}
		});

	}

	/**
	 * Returns the size of a tile
	 * 
	 * @return the size of a tile
	 */

	private double getTileSize() {
		Grid grid = App.getGrid();
		return canvasGrid.getHeight() / Math.max(grid.getNumColumns(), grid.getNumRows());
	}

	/**
	 * draws the grid depending on the number of rows and columns and sets the
	 * elevation of tiles checks if the tiles are selected and if they are, it draws
	 * the tile differently to show that it is selected checks if the tiles are
	 * pointy and if they are, it draws the tiles differently to show that it is
	 * selected
	 */

	private void drawGrid() {
		Grid grid = App.getGrid();
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasGrid.getHeight(), canvasGrid.getWidth());
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				double x = c * getTileSize();
				double y = r * getTileSize();
				Tile tile = grid.getTileAt(r, c);
				gc.setStroke(Color.CYAN);
				gc.strokeRect(x, y, getTileSize(), getTileSize());
				double elev = tile.getElevation();
				double grayVal = 1 - elev / 10;
				Color color = new Color(grayVal, grayVal, grayVal, 1);
				gc.setFill(color);
				gc.fillRect(x, y, getTileSize(), getTileSize());
				if (tile.getPointy()) {
					gc.setStroke(color.BLUE);
					gc.strokeLine(x, y, x + getTileSize(), y + getTileSize());
					gc.strokeLine(x, y + getTileSize(), x + getTileSize(), y);

				}
			}
		}

		for (Tile tile : clickedTileSet) {
			double x = tile.getColumn() * getTileSize();
			double y = tile.getRow() * getTileSize();
			gc.setStroke(Color.RED);
			gc.strokeRect(x, y, getTileSize(), getTileSize());
		}
	}

	/**
	 * adds multiple objects to the program like mountains, pitts, and more to draw
	 * them with one button click
	 * 
	 * @param event
	 */
	@FXML
	void addFeatureAction(ActionEvent event) {
		if (clickedTileSet.size() == 0) {
			new Alert(AlertType.ERROR, "Select one tile").showAndWait();
		} else {
			if (clickedTileSet.size() == 1) {
				try {
					int[][] feature = getFeature();
					setElevationForFeature(feature);
				}catch(NullPointerException ex) {
					new Alert(AlertType.ERROR, "Select a feature from the drop down").showAndWait();
				}
			}else {
				new Alert(AlertType.WARNING, "Select only one tile to avoid overlaps and to make sure the feature fits in the grid for each of the tiles selected").showAndWait();
			}

		}
		drawGrid();
		undoRedoHandler.saveState(createMemento());
	}
	
	/**
	 * returns the feature chosen from the drop down
	 * 
	 * @return the feature chosen from the drop down
	 */
	private int[][] getFeature() {
		int[][] feature = null;
		if (featuresComboBox.getValue().equals("Mountain")) {
			feature = Features.getMountain();
		} else if (featuresComboBox.getValue().equals("Pit")) {
			feature = Features.getPit();
		} else if (featuresComboBox.getValue().equals("Road")) {
			feature = Features.getRoad();
		} else if (featuresComboBox.getValue().equals("Augie A")) {
			feature = Features.getAugieA();
		} else if (featuresComboBox.getValue().equals("Volcano")) {
			feature = Features.getVolcano();
		}
		return feature;	
	}
	
	/**
	 * Sets the elevation of the tiles starting from selected tile in order to create the feature
	 * 
	 * @param the feature to be drawn on the grid
	 */
	private void setElevationForFeature(int[][] feature) {
		Grid grid = App.getGrid();
		Tile clickedTile = null;
		for (Tile tile : clickedTileSet) {
			clickedTile = tile;
		}
		int r = clickedTile.getRow();
		int c = clickedTile.getColumn();
		if (!(r + feature.length > grid.getNumColumns() || c + feature[0].length > grid.getNumRows())) {
			for (int i = r; i < r + feature.length; i++) {
				for (int j = c; j < c + feature[0].length; j++) {
					App.getGrid().getTileAt(i, j).setElevation(feature[i - r][j - c]);
				}
			}
		}else {
			new Alert(AlertType.ERROR, "Cannot draw the feature in this area because it don't fit in the grid").showAndWait();
		}
	}
	/**
	 * Allows you to select a region when two tiles (corners) are selected
	 */

	@FXML
	void selectedARegion() {
		Grid grid = App.getGrid();
		if (clickedTileSet.size() != 2) {
			new Alert(AlertType.ERROR, "Select two tiles to create a region.").showAndWait();
		} else {
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

				}
			}
		}
		drawGrid();
		undoRedoHandler.saveState(createMemento());

	}

	/**
	 * When a tile is selected and button for pointy is selected, it sets the tile
	 * to pointy.
	 */
	@FXML
	void pointyTileSelected(ActionEvent event) {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();

		} else {
//			for (Tile tile : clickedTileSet) {
//				if (!tile.getPointy()) {
//					continue;
//				} else {
//					new Alert(AlertType.ERROR, "The one selected is already pointy").showAndWait();
//				}
//			}

			for (Tile tile : clickedTileSet) {
				tile.setPointy(true);
			}
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
	}

	/**
	 * clears the selected tiles
	 */

	@FXML
	void clearSelected() {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();
		} else {
			for (Tile tile : clickedTileSet) {
				tile.setElevation(0);
				tile.setPointy(false);
			}
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
	}

	/**
	 * Sets the selected tiles to the specified elevation
	 */

	@FXML
	void setPressed(ActionEvent event) {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();
		} else {
			for (Tile tile : clickedTileSet) {
				double newElevation = elevationSlider.getValue();
				tile.setElevation(newElevation);
			}
			drawGrid();
		}
		undoRedoHandler.saveState(createMemento());

	}

	/**
	 * Selects all the tiles that have the same elevation as the selected tile
	 */

	@FXML
	void selectHeight() {
		Grid grid = App.getGrid();
		HashSet<Double> selectHeights = new HashSet<>();
		if (clickedTileSet.size() != 1) {
			new Alert(AlertType.ERROR, "Select one tile to see all tiles with same height.").showAndWait();
		} else {
			for (Tile tile : clickedTileSet) {
				selectHeights.add(tile.getElevation());
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

	}

	/**
	 * unSelects currently selected tiles
	 */
	@FXML
	void unSelectPressed() {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "There are no selected tiles.").showAndWait();
		} else {
			clickedTileSet.clear();
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
	}

	/**
	 * handles mouse clicks on the grid
	 * 
	 * @param evt- the event
	 */
	private void handleCanvasMousePress(MouseEvent evt) {
		int c = (int) (evt.getX() / getTileSize());
		int r = (int) (evt.getY() / getTileSize());
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
		if (buttonClicked == selectButton) {
			selectButtonSelected(clickedTile);
		} else if (buttonClicked != null) {
			adjustTileHeight(clickedTile);
		}
	}

	/**
	 * handles mouse drags on the grid
	 * 
	 * @param evt -drag event
	 */

	private void handleCanvasMouseDrag(MouseEvent evt) {
		int x = (int) (evt.getX());
		int y = (int) (evt.getY());
		if (!(x < 0 || x > canvasGrid.getWidth() || y < 0 || y > canvasGrid.getHeight())) {
			int c = (int) (evt.getX() / getTileSize());
			int r = (int) (evt.getY() / getTileSize());
			Tile dragTile = App.getGrid().getTileAt(r, c);
			ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
			if (buttonClicked == selectButton) {
				selectButtonSelected(dragTile);
			} else if (dragTile != currentTileModified && buttonClicked != selectButton && buttonClicked != null) {
				adjustTileHeight(dragTile);
			}

		}
	}

	/**
	 * handles the release of the mouse from the grid
	 * 
	 * @param evt- the event
	 */

	private void handleCanvasMouseReleased(MouseEvent evt) {
		undoRedoHandler.saveState(createMemento());
	}

	/**
	 * adjusts the height of a tile
	 * 
	 * @param tile
	 */
	private void adjustTileHeight(Tile tile) {
		double sliderValue = elevationSlider.getValue();
		double newElevation = 0;
		ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
		if (buttonClicked == raiseElevationButton) {
			if (sliderValue == 0) {
				new Alert(AlertType.ERROR, "Select an elevation to raise the tiles by").showAndWait();
			} else {
				newElevation = tile.getElevation() + sliderValue;
			}
		} else if (buttonClicked == lowerElevationButton) {
			newElevation = tile.getElevation() - sliderValue;
		}
		tile.setElevation(newElevation);
		drawGrid();
		currentTileModified = tile;
	}

	/**
	 * stores the selected tiles in the clickedTilesSet
	 * 
	 * @param tile
	 */

	private void selectButtonSelected(Tile tile) {
		clickedTileSet.add(tile);
		drawGrid();
	}

	/**
	 * resets the map to an empty one
	 * 
	 * @param event
	 */

	@FXML
	void clearMapPressed(ActionEvent event) {
		clickedTileSet.clear();
		Grid grid = App.getGrid();
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				grid.getTileAt(r, c).setElevation(0);
				grid.getTileAt(r, c).setPointy(false);
			}
		}
		drawGrid();
		undoRedoHandler.saveState(createMemento());
	}

	/**
	 * handles file opening
	 * 
	 * @param event
	 * @throws IOException 
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 */

	@FXML
	void openFileAction(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
		Grid grid = file.openFile(event);
		App.setGrid(grid);
		drawGrid();
	}

	/**
	 * handles files saving
	 * 
	 * @param event
	 */
	@FXML
	void saveFileAsAction(ActionEvent event) {
		file.saveFileAs(event);
	}

	/**
	 * exports the current file to a 3D .OBJ file
	 */

	@FXML
	public void exportOBJAction() {
		file.exportOBJ();
	}

	/**
	 * handles redo button press
	 * 
	 * @param event
	 */

	@FXML
	void redoAction(ActionEvent event) {
		EditorState state = undoRedoHandler.redo();
		state.restore();
	}

	/**
	 * handles undo button press
	 * 
	 * @param event
	 */
	@FXML
	void undoAction(ActionEvent event) {
		EditorState state = undoRedoHandler.undo();
		state.restore();

	}

	/**
	 * switches current display to the 3D Preview
	 * 
	 * @throws IOException
	 */
	@FXML
	private void switchToThreeDPreview() throws IOException {
		Stage stage = new Stage();
		ThreeDPreviewController j = new ThreeDPreviewController();
		j.start(stage);

	}

	/**
	 * switches current display to the about screen
	 * 
	 * @throws IOException
	 */
	@FXML
	private void switchToAboutScreen() throws IOException {
		// App.setRoot("AboutScreen");
		Stage aboutScreenDialog = new Stage();
		aboutScreenDialog.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("AboutScreen.fxml"));
		Parent root = fxmlLoader.load();
		Scene aboutScreenScene = new Scene(root, 600, 400);
		aboutScreenDialog.setScene(aboutScreenScene);
		aboutScreenDialog.initOwner(App.getMainWindow());
		aboutScreenDialog.initModality(Modality.APPLICATION_MODAL);
		aboutScreenDialog.showAndWait();
		drawGrid();

	}

	/**
	 * switches current display to the preloaded view screen
	 * 
	 * @throws IOException
	 */
	@FXML
	private void switchToPreloadedScreen() throws IOException {
		App.setRoot("preLoaded_Preview");

	}

	/**
	 * switches current display to the empty grid view screen
	 * 
	 * @throws IOException
	 */
	@FXML
	private void switchToEmptyGrid() throws IOException {
		App.setRoot("emptyGridGenerator");

	}

	/**
	 * resizes the current map
	 * 
	 * @throws IOException
	 */

	@FXML
	private void switchToResizeChoice() throws IOException {
		clickedTileSet.clear();
		// App.setRoot("ResizeChoiceController");
		Stage resizeDialog = new Stage();

		// populate dialog with controls.
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ResizeChoiceController.fxml"));
		Parent root = fxmlLoader.load();
		Scene resizeScene = new Scene(root, 840, 680);
		resizeDialog.setScene(resizeScene);

		resizeDialog.initOwner(App.getMainWindow());
		resizeDialog.initModality(Modality.APPLICATION_MODAL);
		resizeDialog.showAndWait();
		drawGrid();
		undoRedoHandler.saveState(createMemento());
	}

	/**
	 * sets keyboard shortcuts for four buttons
	 */
	private void initShortcuts() {
		// ALT+D to clear the map
		clearMapBtn.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.ALT_DOWN),
				new Runnable() {
					@Override
					public void run() {
						clearMapBtn.fire();
					}
				});
		// ALT+X to delete a selection
		clearTilesBtn.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.ALT_DOWN),
				new Runnable() {
					@Override
					public void run() {
						clearTilesBtn.fire();
					}
				});

	}

	/**
	 * creates a Memento
	 * 
	 * @return an EditorState object
	 */
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
			// and add the corresponding tile (at7 row,col) from the clonedgrid
			// into the new set;
			for (Tile tile : clickedTileSet) {
				selectionSet.add(clonedGrid.getTileAt(tile.getRow(), tile.getColumn()));
			}

		}

		public void restore() {
			Grid clonedAgainGrid = clonedGrid.clone();
			App.setGrid(clonedAgainGrid);
			clickedTileSet = new HashSet<Tile>();

			for (Tile tile : selectionSet) {
				clickedTileSet.add(clonedAgainGrid.getTileAt(tile.getRow(), tile.getColumn()));
			}

			drawGrid();
		}
	}

}
