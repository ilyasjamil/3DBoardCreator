package ibis.boardcreator.datamodel;

import javafx.scene.paint.Color;

public class Tile {
	
	private int row;
	private int column;
	private double elevation;
	private int multiplier;
	
    public Tile(int row, int column,  double elevation) {
        this.multiplier = 0;
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
    
    public int getMultiplier() {
		return multiplier;
    }
    public void incrementMultiplier() {
    	this.multiplier++;
    }
    

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
   

}
