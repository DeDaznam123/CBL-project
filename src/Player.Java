
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

    public static double[] rayCast() {
        double[] distances = new double[ProjectionPlane.PLANE_WIDTH];

        for (double i = 0; i < ProjectionPlane.PLANE_WIDTH; i += ProjectionPlane.getAngleIncrement()) {
            distances[(int) (i / ProjectionPlane.getAngleIncrement())] = castRay(i);
        }
        
        return distances;
    }

    // Cast one ray from the player.
    public static double castRay(double i) {

        // Will store the distance of the closest collision to the player (vertical vs horizontal).
        double shortestDistance = 10000000;

        // Position and angle of the ray.
        double rayX = 0;
        double rayY = 0;
        double rayAngle = orientation + i;

        // Ammount to offset the position of the ray for faster calculation of next grid cell.
        double dy = 0;
        double dx = 0;

        double arctan = Math.tan(rayAngle + PI / 2);
        int depth = 0;

        // ======================
        // Check horizontal Lines
        // ======================

        System.out.println(arctan);

        // If the ray is facing up.
        if (rayAngle > PI) {
            rayY = (((int) y / 64) * 64) - 0.0001;
            rayX = (y - rayY) * arctan + x;
            dx = -dy * arctan;
            dy = -64;
            
        // If the ray is facing down.
        } else if (rayAngle < PI) {
            rayY = (((int) y / 64) * 64) + 64;
            rayX = (y - rayY) * arctan + x;
            dx = -dy * arctan;
            dy = 64;
            
        } else if (rayAngle == 0 || rayAngle == PI) {
            rayX = x;
            rayY = y;
            depth = 8;
        }

        // Keep adding the delta until wall is found
        while (depth < DOF) {
            int mx = (int) rayX / 64;
            int my = (int) rayY / 64;
            int mp = my * 5 + mx;

            // Hit wall.
            if (mp > 0 && mp < 25 && Grid.getGrid()[mx][my] == 1) {
                shortestDistance = Math.sqrt(rayX * rayX + rayY * rayY);
                depth = 8;
                System.out.println(shortestDistance);
                break;
            }
            
            // Next boundary.
            rayX += dx;
            rayY += dy;
            depth++;
        }

        // ====================
        // Check vertical Lines
        // ====================

        double ntan = -Math.tan(rayAngle);
        depth = 0;

        // If the ray is facing right.
        if (rayAngle < (PI / 2) || rayAngle > ((3 * PI) / 2)) {
            rayX = ((int) x / 64) * 64 - 1;
            dx = -64;
            
        // If the ray is facing right.
        } else if (rayAngle > (PI / 2) && rayAngle < ((3 * PI) / 2)) {
            rayX = ((int) x / 64) * 64 + 64;
            dx = 64;
        }

        rayY = (x - rayX) * ntan + y;
        dy = -dx * ntan;

        // Keep adding the delta until wall is found
        while (depth < DOF) {
            int mx = (int) rayX / 64;
            int my = (int) rayY / 64;
            int mp = my * 5 + mx;

            if (mp > 0 && mp < 25 && (Grid.getGrid()[mx][my] == 1) && (shortestDistance < Math.sqrt(rayX * rayX + rayY * rayY))) {
                shortestDistance = Math.sqrt(rayX * rayX + rayY * rayY);
                System.out.println("Shortest Distance: " + shortestDistance);
                break;
            }

            rayX += dx;
            rayY += dy;
            depth++;
        }

        return shortestDistance;
    }

    public void moveForward() {
        double newPosX = x + Math.cos(orientation) * 5;
        double newPosY = y + Math.sin(orientation) * 5;

        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public void moveBackward() {
        double newPosX = x - Math.cos(orientation) * 5;
        double newPosY = y - Math.sin(orientation) * 5;

        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public void rotateLeft() {
        orientation += ROTATION_INCREMENT;
        if (orientation >= (2 * PI)) {
            orientation = 0;
        }
    }

    public void rotateRight() {
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