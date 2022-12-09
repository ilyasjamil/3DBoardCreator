package ibis.boardcreator.ui;

public class Features {
	private static final int[][] MOUNTAIN = { { 2, 2, 2, 2, 2 }, { 2, 4, 4, 4, 2 }, { 2, 4, 4, 4, 2 },
			{ 2, 4, 4, 4, 2 }, { 2, 2, 2, 2, 2 } };

	private static final int[][] PIT = { { 6, 6, 6, 6, 6 }, { 6, 2, 2, 2, 6 }, { 6, 2, 2, 2, 6 }, { 6, 2, 2, 2, 6 },
			{ 6, 6, 6, 6, 6 } };
	
	private static final int[][] VOLCANOS = { { 4, 4, 4, 4, 4 }, { 4, 6, 6, 6, 4 }, { 4, 6, 1, 6, 4 },
			{ 4, 6, 6, 6, 4 }, { 4, 4, 4, 4, 4 } };

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
