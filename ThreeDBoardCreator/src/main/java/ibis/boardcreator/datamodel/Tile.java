package ibis.boardcreator.datamodel;

/**
 * This class creates the tiles to be drawn in the grid. It has methods to
 * access the location of the tile and methods to set the tile to pointy, set
 * the tile's elevation, and clone the tile.
 */

public class Tile implements Cloneable {
	public static final double MAX_ELEVATION = 10.0;
	public static final double MIN_ELEVATION = 0.0;
	private int row;
	private int column;
	private double elevation;
	private boolean pointy;

	/**
	 * constructor that creates the tile at the row and column of the grid with the
	 * specified elevation
	 * 
	 * @param row       The row of the grid to create the tile
	 * @param column    The column of the grid to create the tile
	 * @param elevation The elevation of the tile
	 */
	public Tile(int row, int column, double elevation) {
		this.row = row;
		this.column = column;
		this.elevation = elevation;
	}

	/**
	 * returns the row of the tile
	 * 
	 * @return the row of the tile
	 */
	public int getRow() {
		return row;
	}

	/**
	 * returns the column of the tile
	 * 
	 * @return the column of the tile
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * returns true if the tile is pointy and false otherwise
	 * 
	 * @return true if the tile is pointy and false otherwise
	 */
	public boolean getPointy() {
		return pointy;
	}

	/**
	 * Sets the tile pointy or not pointy
	 * 
	 * @param isPointy makes the tile pointy or not pointy
	 */
	public void setPointy(boolean isPointy) {
		this.pointy = isPointy;
	}

	/**
	 * returns the elevation of the tile
	 * 
	 * @return the elevation of the tile
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * Sets the elevation of the tile to the specified elevation. If the elevation
	 * is greater than max elevation, it sets the elevation to max elevation and if
	 * the elevation is less than the min elevation, it sets the elevation to min
	 * elevation.
	 * 
	 * @param newElevation the value to set the elevation of the tile to
	 */
	public void setElevation(double newElevation) {
		if (newElevation >= MAX_ELEVATION) {
			newElevation = MAX_ELEVATION;
		} else if (newElevation <= MIN_ELEVATION) {
			newElevation = MIN_ELEVATION;
		}
		this.elevation = newElevation;
	}

	/**
	 * Creates a clone of the tile
	 */
	@Override
	public Tile clone() {
		try {
			return (Tile) (super.clone());
		} catch (CloneNotSupportedException ex) {
			throw new IllegalStateException("can't happen");
		}
	}

}
