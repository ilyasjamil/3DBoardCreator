package ibis.boardcreator.datamodel;

public class Grid {
	private Tile[][] board;
	private double TILE_SIDE_LENGTH = 10.0; // TODO: should this change to an instance variable?

	public Grid() {
		this(20,20);		
	}

	public Grid(int numRows, int numColumns) {
		board = new Tile[numRows][numColumns];
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				board[row][column] = new Tile(row, column, 0);
			}
		}
	}
	public Tile[][] getBoard(){
		return this.board;
	}
	
	public Tile getTileAt(int row, int column) {
		return board[row][column];
	}
	
	public int getNumRows() {
		return board.length;
	}

	public int getNumColumns() {
		return board[0].length;
	}
	
	public Grid clone() {
		Grid copy = new Grid(this.getNumRows(), this.getNumColumns());
		
		Tile[][] copyBoard = copy.getBoard();
		for (int r = 0; r < copy.getNumRows(); r++) {
			for (int c = 0; c < copy.getNumColumns(); c++) {
				copyBoard[r][c] = board[r][c].clone();
			}
		}
		return copy;
	}
	
}
