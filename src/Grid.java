
/**
 * Grid.
 */
public class Grid {
    
    private static int[][] grid = 
        {{1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1},
        {1, 0, 1, 0, 1},
        {1, 0, 0, 0, 1},
        {1, 1, 1, 1, 1}};

    // Size of the grid.
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    /**
     * Grid.
     */
    public Grid() {
        Grid.grid = new int[WIDTH][HEIGHT];

        generateMap();
    }

    public void generateMap() {
        // TODO: Implement algorithm for map generation.

    }

    /**
     * Check if a coordinate on the grid is within a wall.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Whether the coordinates are in a wall.
     */
    public static boolean isInWall(double x, double y) {

        if (grid[(int) x / CELL_SIZE][(int) y / CELL_SIZE] == 0) {
            return false;
        }

        return true;
    }

    public static int getCellSize() {
        return CELL_SIZE;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static int[][] getGrid() {
        return grid;
    }
}
