import java.util.*;

/**
 * Grid.
 */
public class Grid {

    // Directions for moving in the 4 cardinal directions (up, down, left, right)
    private static final int[][] DIRECTIONS = {
        {0, 1},  // Right
        {1, 0},  // Down
        {0, -1}, // Left
        {-1, 0}  // Up
    };

    // Node class to represent each grid point
    static class Node implements Comparable<Node> {
        int x;
        int y;
        int g; // Distance from start to this node
        int f;  // Total cost (g + h)

        Node parent;

        /**
         * Node constructor.
         * @param x X coordinate.
         * @param y Y coordinate.
         * @param g Distance from start to this node.
         * @param f Total cost (g + h).
         * @param parent Parent node.
         */
        public Node(int x, int y, int g, int f, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.f = f;
            this.parent = parent;
        }

        // Compare nodes based on their f-value (for priority queue)
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    // Heuristic function (Manhattan distance)
    private static int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Perform A* search to find the shortest path from start to goal.
     * @param start Start coordinates.
     * @param goal Goal coordinates.
     * @return List of coordinates representing the path from start to goal.
     */
    public static List<int[]> performAStar(int[] start, int[] goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<String, Node> allNodes = new HashMap<>();

        // Start node
        Node startNode = new Node(start[0], start[1], 0,
            heuristic(start[0], start[1], goal[0], goal[1]), null);
        openSet.add(startNode);
        allNodes.put(start[0] + "," + start[1], startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // If we reached the goal, reconstruct the path
            if (current.x == goal[0] && current.y == goal[1]) {
                return reconstructPath(current);
            }

            // Explore neighbors
            for (int[] direction : DIRECTIONS) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                // Check if the neighbor is within bounds and walkable
                if (isInBounds(grid, neighborX, neighborY) && grid[neighborX][neighborY] == 0) {
                    int tentativeG = current.g + 1; // Distance between nodes is 1

                    String neighborKey = neighborX + "," + neighborY;
                    Node neighbor = allNodes.getOrDefault(neighborKey, new Node(neighborX,
                        neighborY, Integer.MAX_VALUE, Integer.MAX_VALUE, null));

                    // If this path to neighbor is better, update it
                    if (tentativeG < neighbor.g) {
                        neighbor.g = tentativeG;
                        neighbor.f = neighbor.g + heuristic(neighborX, neighborY, goal[0], goal[1]);
                        neighbor.parent = current;

                        if (!allNodes.containsKey(neighborKey)) {
                            openSet.add(neighbor);
                            allNodes.put(neighborKey, neighbor);
                        }
                    }
                }
            }
        }
        // No path found, return an empty list
        return new ArrayList<>();
    }

    // Check if a position is within the grid bounds
    private static boolean isInBounds(int[][] grid, int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    // Reconstruct the path by backtracking from the goal
    private static List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.x, node.y});
            node = node.parent;
        }
        Collections.reverse(path);  // Path should be from start to goal
        return path;
    }

    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    // Size of grid.
    private static final int SIZE = 15;

    private static int[][] grid = new int[SIZE][SIZE];
    
    /**
     * Grid.
     */
    public static void generateGrid() {
        int[][] newg = 
        {{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
         {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};

         grid = newg;
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
