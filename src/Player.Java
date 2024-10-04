
/**
 * Player.
 */
public class Player {

    // Field of view of the Player
    private static final int FOV = 60;

    // Depth of field (Largest side of the grid.)
    private static final int DOF = 5;
    //(Grid.getHeight() < Grid.getWidth()) ? Grid.getWidth() : Grid.getHeight();

    // How much to rotate the player.
    private static final double ROTATION_INCREMENT = 0.1;

    // Value of pi.
    private static final double PI = Math.PI;

    // Position of the player.
    private static double x;
    private  static double y;

    // Orientation in radians relative to absolute east (like unit circle).
    private static double orientation;
    
    public Player(double x, double y) {
        Player.x = x;
        Player.y = y;
        orientation = 0;
    }

    // Cast one ray from the player.
    public static double castRay(double i) {

        // Store the distance of the closest collision to the player (vertical vs horizontal).
        double shortestDistance = 0;

        // Position and angle of the ray.
        double xIntercept = x;
        double yIntercept = y;
        double rayAngle = orientation + (i * App.ANGLE_INCREMENT);

        // Ammount to offset the position of the ray for faster calculation of next grid cell.
        double dy = 0;
        double dx = 0;

        int depth = 0;
        double tan;

        // Avoid values where tan is Undefined and avoid division by 0
        if (rayAngle == PI / 2) {                   tan = Double.MAX_VALUE;   } 
        else if (rayAngle == 3 * PI / 2) {          tan = Double.MAX_VALUE;   }
        else if (rayAngle == 0 || rayAngle == PI) { tan = Double.MIN_VALUE;   }
        else {                                      tan = Math.tan(rayAngle); }
        System.out.println(tan);

        // ======================
        // Check horizontal Lines
        // ======================

        // If the ray is facing up.
        if (rayAngle > PI) {
            yIntercept = (((int) y >> 8) << 8) - 1; // -1 ensures that the intercept belongs to cell above.
            dy = -64;
        // If the ray is facing down.
        } else if (rayAngle < PI) {
            yIntercept = (((int) y >> 8) << 8) + 64;
            dy = 64; 
        }

        xIntercept = x + (y - yIntercept) / tan;
        dx = 64 / tan;

        if (rayAngle == 0 || rayAngle == PI) {
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        // Keep adding the delta until wall is found
        while (depth < DOF) {
            depth++;
            
            if (Grid.getGrid()[(int) xIntercept >> 8][(int) yIntercept >> 8] == 1) {
                depth = DOF;
                shortestDistance = Math.abs((x - xIntercept) / Math.cos(rayAngle));
                break;
            }

            xIntercept += dx;
            yIntercept += dy;
        }

        // ====================
        // Check vertical Lines
        // ====================

        depth = 0;

        // If the ray is facing right.
        if (rayAngle < (PI / 2) || rayAngle > ((3 * PI) / 2)) {
            xIntercept = ((int) x >> 8) << 8 - 1;
            dx = 64;
            
        // If the ray is facing left.
        } else if (rayAngle > (PI / 2) && rayAngle < ((3 * PI) / 2)) {
            yIntercept = ((int) x >> 8) << 8 + 64;
            dx = -64;
        }

        yIntercept = (x - xIntercept) / tan;
        dy = 64 * tan;

        if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2) {
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }
        
        // Keep adding the delta until wall is found
        while (depth < DOF) {
            depth++;

            if (Grid.getGrid()[(int) xIntercept >> 8][(int) yIntercept >> 8] == 1) {
                depth = DOF;

                if (shortestDistance > Math.abs((x - xIntercept) / Math.cos(rayAngle))) {
                    shortestDistance = Math.abs((x - xIntercept) / Math.cos(rayAngle));
                }

                break;
            }

            xIntercept += dx;
            yIntercept += dy;
        }

        System.out.println(shortestDistance);
        return shortestDistance;
    }

    public static void moveForward() {
        double newPosX = x + Math.cos(orientation) * 5;
        double newPosY = y + Math.sin(orientation) * 5;

        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public static void moveBackward() {
        double newPosX = x - Math.cos(orientation) * 5;
        double newPosY = y - Math.sin(orientation) * 5;

        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public static void rotateLeft() {
        orientation += ROTATION_INCREMENT;
        if (orientation >= (2 * PI)) {
            orientation = 0;
        }
    }

    public static void rotateRight() {
        orientation -= ROTATION_INCREMENT;
        if (orientation < 0) {
            orientation = 2 * PI;
        }
    }

    public double getOrientation() {
        return orientation;
    }

    public static int getFOV() {
        return FOV;
    }

}