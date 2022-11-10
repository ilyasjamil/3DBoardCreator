package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
import ibis.boardcreator.datamodel.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainEditorController {

	private final double TILE_SIZE = 50;

	@FXML
	private Canvas canvasGrid;

	@FXML
	private Slider elevationSlider;
	@FXML
	private ToggleButton LowerElevationButton;

	@FXML
	private ToggleButton RaiseElevationButton;

	@FXML
	private Menu aboutScreen;

	@FXML
	private void initialize() {

		drawGrid();
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
	}

	Alert alert = new Alert(Alert.AlertType.WARNING);

	private void drawGrid() {
		Grid grid = App.getGrid();
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();

		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				gc.setStroke(Color.BLACK);
				double x = c * TILE_SIZE;
				double y = r * TILE_SIZE;
				gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				Tile tile = grid.getTileAt(r, c);

				// tile.setElevation(elevationSlider.getValue());
				double elev = tile.getElevation();
				// Color color = new Color(0, 0, 0, elev / 10);
				Color color = new Color(elev / 10, elev / 10, elev / 10, 1.0);
				gc.setFill(color);
				gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);

			}
		}

	}

	@FXML
	private void removeTileAction() throws IOException {
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePressRemove(evt));
	}

	@FXML
	private void addTileAction() throws IOException {
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePressAdd(evt));
	}

	private void handleCanvasMousePressRemove(MouseEvent evt) {
		// TODO Auto-generated method stub
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		if (clickedTile.getElevation() <= 2.0) {
			alert.setContentText("Reached lowest elevation");
			alert.show();
		} else {
			clickedTile.setElevation(clickedTile.getElevation() - 2);

			System.out.print("Elevation: " + clickedTile.getElevation());

			drawGrid();
		}
	}

	private void handleCanvasMousePressAdd(MouseEvent evt) {

		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		if (clickedTile.getElevation() >= 10.0) {
			alert.setContentText("Reached highest elevation");
			alert.show();
		} else {
			double sliderValue = elevationSlider.getValue();
			double newElevation = clickedTile.getElevation() + sliderValue;
			if (newElevation >= 10) {
				newElevation = 10;
			}
			clickedTile.setElevation(newElevation);
			drawGrid();
			// clickedTile.setRow(r);
			// clickedTile.setColumn(c);
			// Color color = new Color(0, 0, 0, sliderValue / 10);
			// clickedTile.incrementMultiplier();
			// gc.setFill(color);
			// gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		}

	}

	private void handleCanvasMouseDrag(MouseEvent evt) {

		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		
			double sliderValue = elevationSlider.getValue();
			double newElevation = clickedTile.getElevation() + sliderValue;
			if (newElevation >= 10) {
				newElevation = 10;
			}
			clickedTile.setElevation(newElevation);
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
//				Tile[][] board = grid.getBoard();
//				for (int i = 0; i < board.length; i++) {
//					for (int j = 0; j < board[i].length; j++) {
//						if (board[i][j].getElevation() > 0) {
//							GraphicsContext gc = canvasGrid.getGraphicsContext2D();
//							Color color = new Color(0, 0, 0, board[i][j].getElevation() / 10);
//							for (int k = 0; k < board[i][j].getMultiplier(); k++) {
//								gc.setFill(color);
//								gc.fillRect(board[i][j].getColumn() * TILE_SIZE, board[i][j].getRow() * TILE_SIZE,
//										TILE_SIZE, TILE_SIZE);
//							}
//
//						}
//					}

				// movieListView.getItems().addAll(mc.getMovieList());
				// collectionNameTextField.setText(mc.getName());
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
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.OBJ)", "*.OBJ");
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
	void saveFileAction(ActionEvent event) {

	}

	@FXML
	private void switchToThreeDPreview() throws IOException {
		App.setRoot("Three_D_Preview");

	}

	@FXML
	private void switchToAboutScreen() throws IOException {
		App.setRoot("AboutScreen");

	}

}
