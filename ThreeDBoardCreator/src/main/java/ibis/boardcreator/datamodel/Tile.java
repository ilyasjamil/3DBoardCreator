package ibis.boardcreator.datamodel;

import javafx.scene.paint.Color;

public class Tile {
	public static final double MAX_ELEVATION = 10.0;
	private int row;
	private int column;
	private double elevation;
	
    public Tile(int row, int column,  double elevation) {
        this.row = row;    
        this.column = column;    
        this.elevation = elevation;
    }

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
    public double getElevation() {
		return elevation;
	}
    

	public void setElevation(double newElevation) {
		if (newElevation >= MAX_ELEVATION) {
			newElevation = MAX_ELEVATION;
		} else if (newElevation <= 0) {
			newElevation = 0;
		}
		this.elevation = newElevation;
	}
   

}
