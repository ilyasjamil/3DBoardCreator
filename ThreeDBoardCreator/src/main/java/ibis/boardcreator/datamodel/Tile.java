package ibis.boardcreator.datamodel;

public class Tile {
	
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

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
   

}
