package ibis.boardcreator.ui;

public class Features {
	private static final int[][] MOUNTAIN = { { 2, 2, 2 }, { 2, 6, 2 }, { 2, 2, 2 } };

	private static final int[][] PIT = { { 6, 6, 6 }, { 6, 2, 6 }, { 6, 6, 6 } };

	private static final int[][] VOLCANOS = { { 4, 4, 4, 4, 4 }, { 4, 6, 6, 6, 4 }, { 4, 6, 1, 6, 4 },
			{ 4, 6, 6, 6, 4 }, { 4, 4, 4, 4, 4 } };

	private static final int[][] AugieA = { { 0, 2, 2, 0 }, { 2, 0, 0, 2 }, { 2, 0, 0, 2 }, { 2, 2, 2, 2 },
			{ 2, 0, 0, 2 }, { 2, 0, 0, 2 } };
	
	private static final int[][] Road = {{1,1},{1,1},{1,1},{1,1},{1,1}};

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
	 * @return the mountain
	 */
	public static int[][] getMountain() {
		return MOUNTAIN;
	}

	/**
	 * @return the pit
	 */
	public static int[][] getPit() {
		return PIT;
	}

	/**
	 * @return the volcanos
	 */
	public static int[][] getVolcanos() {
		return VOLCANOS;
	}
}
