package ibis.boardcreator.datamodel;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;




public class GridIO {
	
	public static void save2dMapAsJSONFile(Grid grid, File outputFile) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(outputFile);
		gson.toJson(grid, writer);
		writer.close();
	}
	
	public static Grid load2dMapFromJSONFile(File inputFile) throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(inputFile);
		Grid grid = gson.fromJson(reader, Grid.class);
		reader.close();
		return grid;
	}
	
	
	
}
