package ibis.boardcreator.ui;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MainEditorController {

	private final double TILE_SIZE = 50;

	@FXML
	private Canvas canvasGrid;
	@FXML
	private ToggleButton addButton;
	@FXML
	private Slider elevationSlider;

	@FXML
	private void initialize() {

		drawGrid();
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
	}

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

				tile.setElevation(elevationSlider.getValue());
				double elev = tile.getElevation();

				// TODO fillRect using an appropriate color, based on the elevation
//				gc.setFill(Color.GREY);
//				gc.fillRect(x,y, TILE_SIZE, TILE_SIZE);

			}
		}

	}

	private void handleCanvasMousePress(MouseEvent evt) {

		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		Color color = new Color(0.0, 0.0, 0.0, elevationSlider.getValue() / 10);
		gc.setFill(color);
		gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);

		drawGrid();

	}

	@FXML
	private void switchToThreeDPreview() throws IOException {
		App.setRoot("Three_D_Preview");
	}

}
