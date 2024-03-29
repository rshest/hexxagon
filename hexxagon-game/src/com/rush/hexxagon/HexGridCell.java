package com.rush.hexxagon;

/**
 * Uniform hexagonal grid cell's metrics utility class.
 * 
 * @author Ruslan Shestopalyuk
 */
public class HexGridCell {
    private static final int[] NEIGHBORS_DI = { 0, 1, 1, 0, -1, -1 };
    private static final int[][] NEIGHBORS_DJ = { { -1, -1, 0, 1, 0, -1 },
            { -1, 0, 1, 1, 1, 0 } };

    private static final int[] NEIGHBORS2_DI = { 0, 1, 2, 2, 2, 1, 0, -1, -2,
            -2, -2, -1 };
    private static final int[][] NEIGHBORS2_DJ = {
            { -2, -2, -1, 0, 1, 1, 2, 1, 1, 0, -1, -2 },
            { -2, -1, -1, 0, 1, 2, 2, 2, 1, 0, -1, -1 } };

    private final int[] CORNERS_DX; // array of horizontal offsets of the cell's
                                    // corners
    private final int[] CORNERS_DY; // array of vertical offsets of the cell's
                                    // corners
    private final int SIDE;

    private int mX = 0; // cell's left coordinate
    private int mY = 0; // cell's top coordinate

    private int mI = 0; // cell's horizontal grid coordinate
    private int mJ = 0; // cell's vertical grid coordinate

    /**
     * Cell radius (distance from center to one of the corners)
     */
    public final int RADIUS;
    /**
     * Cell height
     */
    public final int HEIGHT;
    /**
     * Cell width
     */
    public final int WIDTH;

    public static final int NUM_CORNERS = 6;
    public static final int NUM_NEIGHBORS = 6;
    public static final int NUM_NEIGHBORS2 = 12;

    /**
     * @param radius
     *            Cell radius (distance from the center to one of the corners)
     */
    public HexGridCell(int radius) {
        RADIUS = radius;
        WIDTH = radius * 2;
        HEIGHT = (int) (((float) radius) * Math.sqrt(3));
        SIDE = radius * 3 / 2;

        int cdx[] = { RADIUS / 2, SIDE, WIDTH, SIDE, RADIUS / 2, 0 };
        CORNERS_DX = cdx;
        int cdy[] = { 0, 0, HEIGHT / 2, HEIGHT, HEIGHT, HEIGHT / 2 };
        CORNERS_DY = cdy;
    }

    /**
     * @return X coordinate of the cell's top left corner.
     */
    public int getLeft() {
        return mX;
    }

    /**
     * @return Y coordinate of the cell's top left corner.
     */
    public int getTop() {
        return mY;
    }

    /**
     * @return X coordinate of the cell's center
     */
    public int getCenterX() {
        return mX + RADIUS;
    }

    /**
     * @return Y coordinate of the cell's center
     */
    public int getCenterY() {
        return mY + HEIGHT / 2;
    }

    /**
     * @return Horizontal grid coordinate for the cell.
     */
    public int getIndexI() {
        return mI;
    }

    /**
     * @return Vertical grid coordinate for the cell.
     */
    public int getIndexJ() {
        return mJ;
    }

    /**
     * @return Horizontal grid coordinate for the given neighbor.
     */
    public static int getNeighborI(int i, int j, int neighborIdx) {
        return i + NEIGHBORS_DI[neighborIdx];
    }

    /**
     * @return Vertical grid coordinate for the given neighbor.
     */
    public static int getNeighborJ(int i, int j, int neighborIdx) {
        return j + NEIGHBORS_DJ[i % 2][neighborIdx];
    }

    /**
     * @return Horizontal grid coordinate for the given 2-circle neighbor.
     */
    public static int getNeighbor2I(int i, int j, int neighborIdx) {
        return i + NEIGHBORS2_DI[neighborIdx];
    }

    /**
     * @return Vertical grid coordinate for the given 2-circle neighbor.
     */
    public static int getNeighbor2J(int i, int j, int neighborIdx) {
        return j + NEIGHBORS2_DJ[i % 2][neighborIdx];
    }

    /**
     * Computes X and Y coordinates for all of the cell's 6 corners, clockwise,
     * starting from the top left.
     * 
     * @param cornersX
     *            Array to fill in with X coordinates of the cell's corners
     * @param cornersY
     *            Array to fill in with Y coordinates of the cell's corners
     */
    public void computeCorners(int[] cornersX, int[] cornersY) {
        for (int k = 0; k < NUM_NEIGHBORS; k++) {
            cornersX[k] = mX + CORNERS_DX[k];
            cornersY[k] = mY + CORNERS_DY[k];
        }
    }

    /**
     * Sets the cell's horizontal and vertical grid coordinates.
     */
    public void setCellIndex(int i, int j) {
        mI = i;
        mJ = j;
        mX = i * SIDE;
        mY = HEIGHT * (2 * j + (i % 2)) / 2;
    }

    /**
     * Sets the cell as corresponding to some point inside it (can be used for
     * e.g. mouse picking).
     */
    public void setCellByPoint(int x, int y) {
        int ci = (int) Math.floor((float) x / (float) SIDE);
        int cx = x - SIDE * ci;
        int odd = Math.abs(ci % 2);
        int ty = y - odd * HEIGHT / 2;
        int cj = (int) Math.floor((float) ty / (float) HEIGHT);
        int cy = ty - HEIGHT * cj;

        if (cx > Math.abs(RADIUS / 2 - RADIUS * cy / HEIGHT)) {
            setCellIndex(ci, cj);
        } else {
            setCellIndex(ci - 1, cj + odd - ((cy < HEIGHT / 2) ? 1 : 0));
        }
    }

    /**
     * Returns the minimum number of cells to cross to get from i1, j1 to i2, j2
     */
    public static int walkDistance(int i1, int j1, int i2, int j2) {
        int x1 = i1;
        int z1 = j1 - i1 / 2;
        int y1 = 1 - x1 - z1;
        int x2 = i2;
        int z2 = j2 - i2 / 2;
        int y2 = 1 - x2 - z2;
        return Math.max(Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)),
                Math.abs(z1 - z2));
    }

    public static class Extents
    {
        public Extents(int _x, int _y, int _w, int _h) {
            x = _x;
            y = _y;
            w = _w;
            h = _h;
        }
        public int x;
        public int y;
        public int w;
        public int h;
    }

    public Extents getAreaExtents(int cl, int ct, int cr, int cb) {
        int x = SIDE*cl;
        int w = SIDE*cr + RADIUS*2 - x;
        int y = HEIGHT * (2 * ct + (cl % 2)) / 2;
        int h = HEIGHT * (2 * cb + (cr % 2)) / 2 + HEIGHT - y;
        return new Extents(x, y, w, h);
    }
}
