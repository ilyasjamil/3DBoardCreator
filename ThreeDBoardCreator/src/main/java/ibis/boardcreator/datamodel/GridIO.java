package ibis.boardcreator.datamodel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ibis.boardcreator.ui.App;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GridIO {

	/**
	 * Saves the grid to the output file to Json format using Gson library
	 * 
	 * @param grid      The grid to be saved
	 * @param ouputFile The file to save the grid to
	 * 
	 */
	public static void save2dMapAsJSONFile(Grid grid, File outputFile) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(outputFile);
		gson.toJson(grid, writer);
		writer.close();
	}

	/**
	 * returns the grid that is in the file by converting it to java object from
	 * Json file using Gson library
	 * 
	 * @param inputFile The file that contains the grid
	 * 
	 * @return the grid that is in the file
	 */
	public static Grid load2dMapFromJSONFile(File inputFile) throws JsonSyntaxException, JsonIOException, IOException, NullPointerException {
		Gson gson = new Gson();
		try {
			FileReader reader = new FileReader(inputFile);
			Grid grid = gson.fromJson(reader, Grid.class);
			reader.close();
			return grid;
		}catch (NullPointerException ex) {
			new Alert(AlertType.INFORMATION, "You didn't open any files.").showAndWait();
		}
		return App.getGrid();
		
		
	}

}
