
/**
 * Grid.
 */
public class Grid {

    private static final int SIZE = 30;
    private static int[][] grid = new int[SIZE][SIZE];
        // {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        // {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};


    // Size of the grid.
    

    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    /**
     * Grid.
     */

    public static void generateGrid() {
        for (int i=0;i<SIZE;i++) {
            for (int j=0;j<SIZE;j++) {
                if(i == 0 || j == 0 || i == SIZE-1 || j == SIZE-1) {
                    grid[i][j] = 1;
                } else {
                    grid[i][j] = 0;
                }
            }
        }

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

    public static int getSize() {
        return SIZE;
    }

    public static int[][] getGrid() {
        return grid;
    }
}
