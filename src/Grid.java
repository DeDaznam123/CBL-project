
/**
 * Grid.
 */
public class Grid {
    
    private int[][] grid = {{1, 1, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 0, 1},
                            {1, 0, 1, 0, 1, 0, 1},
                            {1, 0, 0, 0, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 1}};

    // Size of the grid.
    private int xSize;
    private int ySize;

    private final int cellSize = 64;

    /**
     * Grid.
     */
    public Grid(int xSize, int ySize) {
        this.grid = new int[xSize][ySize];
        this.xSize = xSize;
        this.ySize = ySize;

        generateMap();
    }

    public void generateMap() {
        // TODO: Implement algorithm for map generation.

    }

}
