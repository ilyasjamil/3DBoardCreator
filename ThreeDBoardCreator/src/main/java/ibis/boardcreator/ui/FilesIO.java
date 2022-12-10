package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
import ibis.boardcreator.datamodel.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

/**
 * This class handles all the necessery  IO action
 * @author rijan
 *
 */
public class FilesIO {
	/**
	 * handles file opening
	 * 
	 * @param event
	 * @throws IOException
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 */

	@FXML
	Grid openFile(ActionEvent event) throws JsonSyntaxException, JsonIOException, IOException {
		FileChooser openChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.MAP)", "*.MAP");
		openChooser.getExtensionFilters().add(extFilter);
		File inputFile = openChooser.showOpenDialog(App.getMainWindow());
		Grid grid = GridIO.load2dMapFromJSONFile(inputFile);
		if (inputFile != null) {
			try {
				grid = GridIO.load2dMapFromJSONFile(inputFile);
				App.setGrid(grid);

			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open could not be found.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR,
						"Error opening file.  Did you choose a valid .movieList file (which uses JSON format?)").show();
			}
		}
		return grid;

	}

	/**
	 * handles files saving
	 * 
	 * @param event
	 */
	@FXML
	void saveFileAs(ActionEvent event) {
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

	/**
	 * exports the current file to a 3D .OBJ file
	 */

	@FXML
	void exportOBJ() {
		FileChooser saveChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OBJ File(*.OBJ)", "*.OBJ");
		saveChooser.getExtensionFilters().add(extFilter);
		File outputFile = saveChooser.showSaveDialog(App.getMainWindow());
		if (outputFile != null) {
			Grid grid = App.getGrid();
			Tile[][] board = grid.getBoard();
			try {
				FileWriter writer = new FileWriter(outputFile);
				writer.write("o " + 1 + System.lineSeparator());
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[0].length; j++) {
						Tile tile = board[i][j];
						int r = tile.getRow();
						int c = tile.getColumn();
						double e = tile.getElevation();
						writer.write("v " + c + " " + r + " " + e + System.lineSeparator());
						writer.write("v " + c + " " + r + " " + 0 + System.lineSeparator());
						writer.write("v " + c + " " + String.valueOf(r + 1) + " " + 0 + System.lineSeparator());
						writer.write("v " + c + " " + String.valueOf(r + 1) + " " + e + System.lineSeparator());
						writer.write("v " + String.valueOf(c + 1) + " " + r + " " + e + System.lineSeparator());
						writer.write("v " + String.valueOf(c + 1) + " " + r + " " + 0 + System.lineSeparator());
						writer.write("v " + String.valueOf(c + 1) + " " + String.valueOf(r + 1) + " " + 0
								+ System.lineSeparator());
						writer.write("v " + String.valueOf(c + 1) + " " + String.valueOf(r + 1) + " " + e
								+ System.lineSeparator());
					}
				}
				writer.write("usemtl Default" + System.lineSeparator());

				for (int i = 0; i < board.length * board[0].length; i++) {
					writer.write("f " + String.valueOf(8 * i + 4) + " " + String.valueOf(8 * i + 3) + " "
							+ String.valueOf(8 * i + 2) + " " + String.valueOf(8 * i + 1) + System.lineSeparator());
					writer.write("f " + String.valueOf(8 * i + 2) + " " + String.valueOf(8 * i + 6) + " "
							+ String.valueOf(8 * i + 5) + " " + String.valueOf(8 * i + 1) + System.lineSeparator());
					writer.write("f " + String.valueOf(8 * i + 3) + " " + String.valueOf(8 * i + 7) + " "
							+ String.valueOf(8 * i + 6) + " " + String.valueOf(8 * i + 2) + System.lineSeparator());
					writer.write("f " + String.valueOf(8 * i + 8) + " " + String.valueOf(8 * i + 7) + " "
							+ String.valueOf(8 * i + 3) + " " + String.valueOf(8 * i + 4) + System.lineSeparator());
					writer.write("f " + String.valueOf(8 * i + 5) + " " + String.valueOf(8 * i + 8) + " "
							+ String.valueOf(8 * i + 4) + " " + String.valueOf(8 * i + 1) + System.lineSeparator());
					writer.write("f " + String.valueOf(8 * i + 6) + " " + String.valueOf(8 * i + 7) + " "
							+ String.valueOf(8 * i + 8) + " " + String.valueOf(8 * i + 5) + System.lineSeparator());
				}
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
