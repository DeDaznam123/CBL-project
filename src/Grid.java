
/**
 * Grid.
 */
public class Grid {
    
    private static int[][] grid = 
        {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

    // Size of the grid.
    private static final int SIZE = grid[0].length;
    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    /**
     * Check if a coordinate on the grid is within a wall.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Whether the coordinates are in a wall.
     */
    public static boolean isInWall(double x, double y) {

        if (grid[(int) y / CELL_SIZE][(int) x / CELL_SIZE] == 0) {
            return false;
        }

        return true;
    }

    public static int getCellSize() {
        return CELL_SIZE;
    }

    public static int getSize() {
        return SIZE;
    }

    public static int[][] getGrid() {
        return grid;
    }
}
