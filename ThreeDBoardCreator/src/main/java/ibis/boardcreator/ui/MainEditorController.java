package ibis.boardcreator.ui;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

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
	private void initialize() {

		drawGrid();
		// canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
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

				// TODO fillRect using an appropriate color, based on the elevation
//				gc.setFill(Color.GREY);
//				gc.fillRect(x,y, TILE_SIZE, TILE_SIZE);

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
		// TODO write code to catch "java.lang.IllegalArgumentException: where color
		// opacity goes below 0"
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int r = (int) (evt.getX() / TILE_SIZE);
		int c = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		if(clickedTile.getElevation() <2.0) {
			alert.setContentText("Reached lowest elevation");
			alert.show();
		}else {
			clickedTile.setElevation(clickedTile.getElevation() - 2);
			Color color = new Color(1, 1, 1, clickedTile.getElevation() / 10);
			gc.setFill(color);
			gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			drawGrid();
		}
		
	}

	private void handleCanvasMousePressAdd(MouseEvent evt) {

		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int r = (int) (evt.getY() / TILE_SIZE);
		int c = (int) (evt.getX() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		if(clickedTile.getElevation() >= 10.0) {
			alert.setContentText("Reached highest elevation");
			alert.show();
		}else {
			clickedTile.setElevation(clickedTile.getElevation() + elevationSlider.getValue());
			Color color = new Color(0, 0, 0, elevationSlider.getValue() / 10);
			gc.setFill(color);
			gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);

			drawGrid();
		}
		

	}

	
	@FXML
	private void switchToThreeDPreview() throws IOException {
		App.setRoot("Three_D_Preview");
	}

}
