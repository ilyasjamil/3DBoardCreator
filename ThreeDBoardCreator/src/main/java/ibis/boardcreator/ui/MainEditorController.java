package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
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
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
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
	private Button undoBtn;
	
	@FXML
	private Button redoBtn;
	
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
	

	@FXML
	private void initialize() {
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
		canvasGrid.setOnMouseReleased(evt -> handleCanvasMouseReleased(evt));
		clickedTileSet = new HashSet<>();
		undoRedoHandler = new UndoRedoHandler(createMemento());
		featuresComboBox.getItems().add("Mountains");
		featuresComboBox.getItems().add("Pitt");
		featuresComboBox.getItems().add("Volcanos");
		clearMapBtn.setStyle("-fx-background-color: #32a5cb");
		resizeButton.setStyle("-fx-background-color: #7ababb");
		drawGrid();
		Platform.runLater(new Runnable() {
		    public void run() {
		        initShortcuts();
		    }
		});

	}
	
	
	private double getTileSize() {
		Grid grid = App.getGrid();
		return canvasGrid.getHeight() / Math.max(grid.getNumColumns(), grid.getNumRows());
	}

	
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
				if(tile.getPointy()) {
					gc.setStroke(color.BLUE);
					gc.strokeLine(x, y, x+getTileSize(), y+getTileSize());
					gc.strokeLine(x, y+ getTileSize(), x+getTileSize(), y);
					
				}
			}

				

		}
		
		for (Tile tile : clickedTileSet) {
			double x = tile.getColumn()*getTileSize();
			double y = tile.getRow()* getTileSize();
			gc.setStroke(Color.RED);
			gc.strokeRect(x, y, getTileSize(), getTileSize());
		}
	}
	
	
	
	@FXML
	void selectedARegion() {
		Grid grid = App.getGrid();
		if (clickedTileSet.size() != 2) {
			new Alert(AlertType.ERROR, "Select two tiles to create a region.").showAndWait();
		}else {
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


	@FXML
	void pointyTileSelected(ActionEvent event) {
		if(clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();
		}else {
			for (Tile tile : clickedTileSet) {
				tile.setPointy(true);
			}
			drawGrid();
		}
		undoRedoHandler.saveState(createMemento());
	}
	
	
	@FXML
	void clearSelected() {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();
		}else {
			for (Tile tile : clickedTileSet) {
				tile.setElevation(0);
			}
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
	}

	

	@FXML
	void setPressed(ActionEvent event) {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "Select atleast one tile.").showAndWait();
		}else {
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
		if (clickedTileSet.size() != 1) {
			new Alert(AlertType.ERROR, "Select one tile to see all tiles with same height.").showAndWait();
		}else {
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
	

	@FXML
	public void unSelectPressed() {
		if (clickedTileSet.isEmpty()) {
			new Alert(AlertType.ERROR, "There are no selected tiles.").showAndWait();
		}else {
			clickedTileSet.clear();
			drawGrid();
			undoRedoHandler.saveState(createMemento());
		}
	}

	private void handleCanvasMousePress(MouseEvent evt) {
		int c = (int) (evt.getX() / getTileSize());
		int r = (int) (evt.getY() / getTileSize());
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
		if (buttonClicked == selectButton) {
			selectButtonSelected(clickedTile);
		}else if (buttonClicked != null) {			
			adjustTileHeight(clickedTile);
		}
	}
		
	private void handleCanvasMouseDrag(MouseEvent evt) {
		try {
			int c = (int) (evt.getX() / getTileSize());
			int r = (int) (evt.getY() / getTileSize());
			Tile dragTile = App.getGrid().getTileAt(r, c);
			ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
			if (buttonClicked == selectButton) {
				selectButtonSelected(dragTile);
			}
			else if (dragTile != currentTileModified && buttonClicked != selectButton
					&& buttonClicked != null) {
				adjustTileHeight(dragTile);
			}
		}catch(ArrayIndexOutOfBoundsException ex) {
			new Alert(AlertType.ERROR, "Please only drag inside the grid").showAndWait();

		}
	}
	
	private void handleCanvasMouseReleased(MouseEvent evt) {
		undoRedoHandler.saveState(createMemento());
	}	

	private void adjustTileHeight(Tile tile) {
		double sliderValue = elevationSlider.getValue();
		double newElevation = 0;
		ToggleButton buttonClicked = (ToggleButton) toolButtonsGroup.getSelectedToggle();
		if (buttonClicked == raiseElevationButton) {
			newElevation = tile.getElevation() + sliderValue;
		} else if (buttonClicked == lowerElevationButton) {
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
	}


	private void selectButtonSelected(Tile tile) {
		clickedTileSet.add(tile);
		drawGrid();
	}
	
	@FXML
	void addFeatureAction(ActionEvent event){
		int r;
		int c;
		int[][] feature;
		if (clickedTileSet.size() == 0) {
			new Alert(AlertType.ERROR, "Select atleast one tile").showAndWait();
		}else {
			for (Tile tile : clickedTileSet) {
				r = tile.getRow();
				c = tile.getColumn();
				try {
					if (featuresComboBox.getValue().equals("Mountains")) {
						feature = Features.getMountain();
					} else if (featuresComboBox.getValue().equals("Pitt")) {
						feature = Features.getPit();
					} else {
						feature = Features.getVolcanos();
					}
					for (int i = r; i < r + feature.length; i++) {
						for (int j = c; j < c + feature[0].length; j++) {
							App.getGrid().getTileAt(i, j).setElevation(feature[i - r][j - c]);
						}
					}
				}catch(ArrayIndexOutOfBoundsException ex){
					new Alert(AlertType.ERROR, "Cannot draw the feature in this area").showAndWait();
				}catch(NullPointerException ex) {
					new Alert(AlertType.ERROR, "Select a feature from the drop down").showAndWait();
				}
			}
			drawGrid();
			undoRedoHandler.saveState(createMemento());

		}

	}
	
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


	@FXML
	void openFileAction(ActionEvent event) {
		FileChooser openChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.MAP)", "*.MAP");
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
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.MAP)", "*.MAP");
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
	

	@FXML
	private void switchToThreeDPreview() throws IOException {
		Stage stage = new Stage();
		ThreeDPreviewController j = new ThreeDPreviewController();
		j.start(stage);

	}

	@FXML
	private void switchToAboutScreen() throws IOException {
		//App.setRoot("AboutScreen");
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

	@FXML
	private void switchToPreloadedScreen() throws IOException {
		App.setRoot("preLoaded_Preview");

	}

	@FXML
	private void switchToEmptyGrid() throws IOException {
		App.setRoot("emptyGridGenerator");

	}
	
	@FXML
	private void switchToResizeChoice() throws IOException {
		clickedTileSet.clear();
		//App.setRoot("ResizeChoiceController");
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
	private void initShortcuts() {
		//ALT+D to clear the map
		clearMapBtn.getScene().getAccelerators().put(
				  new KeyCodeCombination(KeyCode.D, KeyCombination.ALT_DOWN), 
				  new Runnable() {
				    @Override public void run() {
				      clearMapBtn.fire();
				    }
				  }
				);
		//ALT+X to delete a selection
		clearTilesBtn.getScene().getAccelerators().put(
				  new KeyCodeCombination(KeyCode.X, KeyCombination.ALT_DOWN), 
				  new Runnable() {
				    @Override public void run() {
				      clearTilesBtn.fire();
				    }
				  }
				);
		//CTRL+Z to undo
		undoBtn.getScene().getAccelerators().put(
				  new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN), 
				  new Runnable() {
				    @Override public void run() {
				      undoBtn.fire();
				    }
				  }
				);
		//CTRL+R to redo
		redoBtn.getScene().getAccelerators().put(
				  new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN), 
				  new Runnable() {
				    @Override public void run() {
				      redoBtn.fire();
				    }
				  }
				);
		
		
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
