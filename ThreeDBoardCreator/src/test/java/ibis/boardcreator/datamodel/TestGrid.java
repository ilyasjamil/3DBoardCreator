package ibis.boardcreator.datamodel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class TestGrid {

	// Testing clone method of grids. Creating a new grid and setting some tile to
	// some elevation. Setting a different tile in clone grid and setting it to some
	// elevation.Checking that cloned grid has the same tile set to the same
	// elevation.Checking that original grid is not changed by the cloned grid.

	@Test
	void testClone() {
		Grid grid = new Grid(17, 17);
		Tile tile = grid.getTileAt(7, 15);
		tile.setElevation(10.0);
		Grid clonedGrid = grid.clone();
		Tile clonedTile = clonedGrid.getTileAt(7, 15);
		double clonedElevation = clonedTile.getElevation();
		clonedGrid.getTileAt(15, 4).setElevation(8.0);
		double originalElevation = grid.getTileAt(15, 4).getElevation();
		assertEquals(10.0, clonedElevation);
		assertEquals(0.0, originalElevation);
	}

	// Testing resize method for larger grid, by putting data into a new grid, then
	// calling the resize methods. Checking if the new rows and columns are correct.
	// Checking that the data is still there.
	@Test
	void testResizeLarger() {
		Grid grid = new Grid();
		Tile tile = grid.getTileAt(5, 5);
		tile.setElevation(6.0);
		grid.resize(30, 30);
		double value = tile.getElevation();
		int column = grid.getNumColumns();
		int row = grid.getNumRows();
		assertEquals(6.0, value);
		assertEquals(30, column);
		assertEquals(30, row);
	}

	// Testing resize method for smaller grid, by putting data into a new grid, then
	// calling the resize methods. Checking if the new rows and columns are correct.
	// Checking that the data is still there.
	@Test
	void testResizeSmaller() {
		Grid grid = new Grid(40, 40);
		Tile tile = grid.getTileAt(20, 20);
		tile.setElevation(6.0);
		grid.resize(10, 10);
		double value = tile.getElevation();
		int column = grid.getNumColumns();
		int row = grid.getNumRows();
		assertEquals(6.0, value);
		assertEquals(10, column);
		assertEquals(10, row);
	}

	// Testing the to String method in the Grid class. Creating a new grid with some
	// rows and columns. Checking whether the output of the toString Method is
	// correct
	@Test
	void testToString() {
		Grid grid = new Grid(123, 256);
		String output = grid.toString();
		int columns = grid.getNumColumns();
		int rows = grid.getNumRows();
		assertEquals("The grid has " + rows + " rows and " + columns + " columns.", output);
	}

	// Testing the set elevation methods in the Tile class. Checking that it sets
	// the elevation to max elevation when the elevation is greater than max
	// elevation. Also checking that it sets the elevation to min elevation when the
	// elevation is less than the min elevation. Finally checking that it sets the
	// elevation to the elevation specified when it is between or equals to min and
	// max elevation.
	@Test
	void testSetElevation() {
		Grid grid = new Grid(10, 20);
		grid.getTileAt(9, 19).setElevation(0);
		grid.getTileAt(4, 4).setElevation(20);
		grid.getTileAt(0, 0).setElevation(6);
		double elevationOne = grid.getTileAt(9, 19).getElevation();
		double elevationTwo = grid.getTileAt(4, 4).getElevation();
		double elevationThree = grid.getTileAt(0, 0).getElevation();
		assertEquals(0, elevationOne);
		assertEquals(10, elevationTwo);
		assertEquals(6, elevationThree);

	}

	// Testing the loading and saving methods in GridIO classCreated a small grid
	// Saved that grid then opened that grid Checking whether new grid object has
	// the same data as the original.
	@Test
	void testSavingAndLoadingFile() throws IOException {
		Grid grid = new Grid();
		grid.getTileAt(5, 6).setElevation(5.0);
		grid.getTileAt(19, 19).setElevation(10.0);
		grid.getTileAt(0, 0).setElevation(4.0);
		grid.getTileAt(5, 6).setPointy(true);
		GridIO.save2dMapAsJSONFile(grid, new File("tmp/testLoadSave.MAP"));
		Grid openedMap = GridIO.load2dMapFromJSONFile(new File("tmp/testLoadSave.MAP"));
		double openedElevOne = openedMap.getTileAt(5, 6).getElevation();
		double openedElevTwo = openedMap.getTileAt(19, 19).getElevation();
		double openedElevThree = openedMap.getTileAt(0, 0).getElevation();
		boolean openedPointyOne = openedMap.getTileAt(5, 6).getPointy();
		assertEquals(5.0, openedElevOne);
		assertEquals(10.0, openedElevTwo);
		assertEquals(4.0, openedElevThree);
		assertEquals(true, openedPointyOne);

	}

	//Testing the export method by loading an object exported through
	//the software and comparing it to the object it should look like
	@Test
	void testExportingFile() throws IOException{
		//mapObject stores a 3D object of a grid of one row, 2 columns with one of their elevation being 0, and the other being 10, exported via the app
		File MapObject = new File("tmp/OneRowMap.OBJ");
		//testMapObject stores what the 3D object of MapObject should look like
		File testMapObject = new File("tmp/testMap.OBJ");
	    assertEquals(-1, Files.mismatch(MapObject.toPath(), testMapObject.toPath()));
	}

}
