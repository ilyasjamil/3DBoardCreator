package ibis.boardcreator.datamodel;


public class Grid {
	private Tile[][] board;
	
	/**
	 * constructor that creates the grid with 20 rows and 20 columns 
	 * 
	 */
	public Grid() {
		this(20,20);		
	}
	
	/**
	 * constructor that creates the grid with numRows rows and numColumns columns 
	 * 
	 * @param numRows The number of rows for the grid
	 * @param numColumns The number of columns for the grid
	 */
	public Grid(int numRows, int numColumns) {
		board = new Tile[numRows][numColumns];
		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {
				board[row][column] = new Tile(row, column, 0);
			}
		}
	}
	
	/**
	 * returns the 2D board of row and columns of the tiles created in the grid
	 * 
	 * @return the 2D board
	 */
	public Tile[][] getBoard(){
		return this.board;
	}
	
	/**
	 * returns the tile at specific the row and column of the grid
	 * 
	 *  @param row The specific row on the grid
	 *  @param column The specific column on the grid
	 * 
	 * @return the tile at the row and column of the grid 
	 */
	public Tile getTileAt(int row, int column) {
		return board[row][column];
	}
	
	/**
	 * returns the number of rows of the grid
	 * 
	 * @return the number of rows
	 */
	public int getNumRows() {
		return board.length;
	}

	/**
	 * returns the number of columns of the grid
	 * 
	 * @return the number of columns
	 */
	public int getNumColumns() {
		return board[0].length;
	}
	
	/**
	 * returns the a copy of the grid created
	 * 
	 * @return a copy of the grid
	 */
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
	
	/**
	 * returns a resized grid with the number of rows and columns specified. Keeps the tiles modified the same
	 * 
	 * @param newRows The number of rows for the new grid
	 * @param newColumns The number of columns for the new grid
	 * 
	 * @return the number of columns
	 */
	public void resize(int newRows, int newColumns) {
		Tile[][] newBoard = new Tile[newRows][newColumns];
		for(int i = 0; i < newRows; i++) {
			for(int j = 0; j < newColumns; j++) {
				if(i >= board.length || j >= board[0].length) {
					newBoard[i][j] = new Tile(i, j, 0);
				}
				else {
					newBoard[i][j] = board[i][j];
				}
			}	
		}
		this.board = newBoard;
		
	}
	
	/**
	 * returns a description for the grid 
	 * 
	 * @return A string that describes the grid
	 */
	@Override
	public String toString() {
		return "The grid has " + this.getNumRows() + " rows and " + this.getNumColumns() + " columns."; 
	}

}
