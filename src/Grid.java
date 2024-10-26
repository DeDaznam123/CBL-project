import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Grid.
 */
public class Grid {

    static class Node implements Comparable<Node> {
        // X and Y coordinates of the node.
        int x; 
        int y;

        // Distance from start to this node
        int g; 

        // Cost of the node.
        int f;

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

        // Compare nodes based on their f-value (PriorityQueue).
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    // Number of cells in X and Y directions.
    private static final int SIZE = 30;
    private static int[][] grid = new int[SIZE][SIZE];

    private static final int[][][] TEMPLATE_TYPE_1 = {
        {
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
            {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        }
    };
    
    private static final int[][][] TEMPLATE_TYPE_2 = {
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 1, 1, 0, 0, 0},
            {1, 0, 1, 0, 0, 1, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 1, 0, 0},
            {1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 0},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 0},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        }
    };
    
    private static final int[][][] TEMPLATE_TYPE_3 = {
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        },
        {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
            {0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
            {1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 1, 1, 1}
        }
    };    
        
    // Pixels per cell in grid.
    private static final int CELL_SIZE = 64;

    // Directions for moving in 4 directions (up, down, left, right).
    private static final int[][] DIRECTIONS = {
        {0, 1},  // Right.
        {1, 0},  // Down.
        {0, -1}, // Left.
        {-1, 0}  // Up.
    };

    // Heuristic function (Manhattan distance).
    private static int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // Reconstruct the path by backtracking from the goal.
    private static ArrayList<int[]> reconstructPath(Node node) {
        ArrayList<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.x, node.y});
            node = node.parent;
        }

        // Invert the path to be from start to goal.
        Collections.reverse(path);
        return path;

    }

    /**
     * Perform A* search to find the path from start to goal.
     * @param start Start coordinates.
     * @param goal Goal coordinates.
     * @return List of coordinates representing the path from start to goal.
     */
    public static ArrayList<int[]> performAStar(int[] start, int[] goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        HashMap<String, Node> allNodes = new HashMap<>();

        // Start node.
        Node startNode = new Node(start[0], start[1], 0,
            heuristic(start[0], start[1], goal[0], goal[1]), null);

        openSet.add(startNode);
        allNodes.put(start[0] + "," + start[1], startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // If reached the goal, reconstruct the path.
            if (current.x == goal[0] && current.y == goal[1]) {
                return reconstructPath(current);
            }

            // Explore neighbors.
            for (int[] direction : DIRECTIONS) {
                int neighborX = current.x + direction[0];
                int neighborY = current.y + direction[1];

                // Check if the neighbor is within bounds and not in wall.
                if (isInBounds(grid, neighborX, neighborY) && grid[neighborX][neighborY] == 0) {
                    // +1 distance.
                    int tentativeG = current.g + 1;
                    String neighborKey = neighborX + "," + neighborY;

                    Node neighbor = allNodes.getOrDefault(neighborKey, new Node(neighborX,
                        neighborY, Integer.MAX_VALUE, Integer.MAX_VALUE, null));

                    // If the path through neighbor is better, update it.
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

        // No path found.
        return new ArrayList<>();
    }

    /**
     * Generate the grid.
     */
    public static void generateGrid() {
        int randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_2[1], 0, 0, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_3[randomnumber], 1, 10, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_2[1], 1, 20, 0);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_3[randomnumber], 0, 0, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_1[randomnumber], 0, 10, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_3[randomnumber], 2, 20, 10);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_2[randomnumber], 3, 0, 20);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_3[randomnumber], 3, 10, 20);
        randomnumber = (int) (Math.random() * 3);
        useTemplate(TEMPLATE_TYPE_2[randomnumber], 2, 20, 20);
    }

    /**
     * Uses a template to modify the grid at a specified position with a specified rotation.
     * @param template The template to use.
     * @param rotation The number of 90-degree rotations to apply to the template.
     * @param x The x coordinate on the grid.
     * @param y The y coordinate on the grid.
     */
    private static void useTemplate(int[][] template, int rotation, int x, int y) {
        int[][] templateCopy = copyTemplate(template);

        templateCopy = rotateTemplate(templateCopy, rotation);

        applyTemplateToGrid(templateCopy, x, y);
    }

    /**
     * Copies the template to a new 2D array.
     * @param template The template to copy.
     * @return The copied template.
     */
    private static int[][] copyTemplate(int[][] template) {
        int[][] templateCopy = new int[template.length][template[0].length];
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                templateCopy[i][j] = template[i][j];
            }
        }
        return templateCopy;
    }

    /**
     * Rotates the template a specified number of times.
     * @param template The template to rotate.
     * @param rotation The number of 90-degree rotations to apply.
     * @return The rotated template.
     */
    private static int[][] rotateTemplate(int[][] template, int rotation) {
        int[][] rotatedTemplate = template;
        for (int i = 0; i < rotation; i++) {
            rotatedTemplate = rotate(rotatedTemplate);
        }
        return rotatedTemplate;
    }

    /**
     * Applies the template to the grid at the specified position.
     * @param template The template to apply.
     * @param x The x coordinate on the grid.
     * @param y The y coordinate on the grid.
     */
    private static void applyTemplateToGrid(int[][] template, int x, int y) {
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                grid[x + i][y + j] = template[i][j];
            }
        }
    }

    /**
     * Rotates a template.
     * @param template Template to rotate.
     * @return The rotated template.
     */
    private static int[][] rotate(int[][] template) {
        int[][] templateCopy = new int[template.length][template[0].length]; 
        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                templateCopy[i][j] = template[i][j];
            }
        }

        for (int i = 0; i < template.length; i++) {
            for (int j = 0; j < template[i].length; j++) {
                template[i][j] = templateCopy[j][template.length - i - 1];
            }
        }
        return template;
    }
    
    // Check if a position is within the grid bounds
    private static boolean isInBounds(int[][] grid, int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
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