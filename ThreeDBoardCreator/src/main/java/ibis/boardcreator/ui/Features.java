package ibis.boardcreator.ui;

public class Features {
	private static final int[][] MOUNTAIN = { { 2, 2, 2 }, { 2, 6, 2 }, { 2, 2, 2 } };

	private static final int[][] PIT = { { 6, 6, 6 }, { 6, 0, 6 }, { 6, 6, 6 } };

	private static final int[][] VOLCANO = { { 4, 4, 4, 4, 4 }, { 4, 6, 6, 6, 4 }, { 4, 6, 1, 6, 4 },

			{ 4, 6, 6, 6, 4 }, { 4, 4, 4, 4, 4 } };

	private static final int[][] AugieA = { { 0, 2, 2, 0 }, { 2, 0, 0, 2 }, { 2, 0, 0, 2 }, { 2, 2, 2, 2 },
			{ 2, 0, 0, 2 }, { 2, 0, 0, 2 } };

	private static final int[][] Road = { { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 } };

	/**
	 * @return the road
	 */
	public static int[][] getRoad() {
		return Road;
	}

	/**
	 * @return the augiea
	 */
	public static int[][] getAugieA() {
		return AugieA;
	}

	/**
	 * returns the mountain which is a 2D array of elevations of the tiles to be
	 * drawn on the grid which represents the mountain.
	 * 
	 * @return the mountain
	 */
	public static int[][] getMountain() {
		return MOUNTAIN;
	}

	/**
	 * returns the pit which is a 2D array of elevations of the tiles to be drawn on
	 * the grid which represents the pit.
	 * 
	 * @return the pit
	 */
	public static int[][] getPit() {
		return PIT;
	}

	/**
	 * returns the volcano which is a 2D array of elevations of the tiles to be
	 * drawn on the grid which represents the volcano.
	 * 
	 * @return the volcano
	 */
	public static int[][] getVolcano() {
		return VOLCANO;
	}

}
