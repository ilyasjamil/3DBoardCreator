package ibis.boardcreator.ui;

import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MainEditorController {

	@FXML 
	private Canvas canvasGrid;

	@FXML
	private void initialize() {
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();
		
		//TODO: store the grid somewhere - we don't want to keep creating new Grid objects
		Grid grid = new Grid(3,5);
		
		//TODO: move drawing code into separate method, to make it easy to redraw
		//      whenever the grid changes
		
		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				gc.setStroke(Color.BLUE);
				double x = c * 50;
				double y = r * 50;
				gc.strokeRect(x, y, 50, 50);
				Tile tile = grid.getTileAt(r, c);
				double elev = tile.getElevation();
				//TODO fillRect using an appropriate color, based on the elevation
				
			}
		}
	}
	
    @FXML
    private void switchToThreeDPreview() throws IOException {
        App.setRoot("Three_D_Preview");
    }
    

}
