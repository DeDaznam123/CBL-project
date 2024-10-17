
/**
 * Player.
 */
public class Player {

    // Field of view of the Player
    private static final int FOV = 60;

    // Depth of field (Largest side of the grid.)
    private static final int DOF = Grid.getSize() * Grid.getCellSize();

    // How much to rotate the player.
    private static final double ROTATION_INCREMENT = 0.02;

    // Value of pi.
    private static final double PI = Math.PI;

    // Position of the player.
    private double x;
    private double y;
    private int health;

    // Orientation in radians relative to absolute east (like unit circle).
    private double orientation;

    private double speedMultiplier = 2;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        orientation = 0;
    }

    public double castForHorizontalDistance(double rayAngle) {
        double aTan = 1 / Math.tan(rayAngle);
        double xIntercept = 0;
        double yIntercept = 0;
        double dx = 0;
        double dy = 0;
        double depth = 0;

        // Facing up.
        if (Math.sin(rayAngle) > 0.0001) {
            yIntercept = (((int) y >> 6) << 6)  - 0.0001;
            xIntercept = (y - yIntercept) * aTan + x;
            dy = -64;
            dx = -dy * aTan;
        // Facing down.
        } else if (Math.sin(rayAngle) < -0.0001) {
            yIntercept = (((int) y >> 6) << 6) + 64;
            xIntercept = (y - yIntercept) * aTan + x;
            dy = 64;
            dx = -dy * aTan;
        // Facing directly left or right.
        } else if (Math.sin(rayAngle) == 0) {
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth < DOF) {
            int xIndex = (int) xIntercept >> 6;
            int yIndex = (int) yIntercept >> 6;

            if(yIndex >= 0 && xIndex >= 0 && yIndex < Grid.getSize() && xIndex < Grid.getSize() && Grid.getGrid()[yIndex][xIndex]==1) {
                depth = DOF;
                break;
                
            } else {
                xIntercept += dx;
                yIntercept += dy;
                depth++;
            }
        }

        return Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept));
    }

    public double castForVerticalDistance(double rayAngle) {
        double tan = Math.tan(rayAngle);
        double xIntercept = 0;
        double yIntercept = 0;
        double dx = 0;
        double dy = 0;
        double depth = 0;

        // If the ray is facing left.
        if (Math.cos(rayAngle) > 0.0001) {
            xIntercept = (((int) x >> 6) << 6) + 64;
            yIntercept = (x - xIntercept) * tan + y;
            dx = 64;
            dy = -dx * tan;
        // If the ray is facing right.
        } else if (Math.cos(rayAngle) < -0.0001) {
            xIntercept = (((int) x >> 6) << 6) - 0.0001;
            yIntercept = (x - xIntercept) * tan + y;
            dx = -64;
            dy = -dx * tan;
        // Up / Down.
        } else if (Math.cos(rayAngle) == 0) {
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth < DOF) {
            int xIndex = (int) xIntercept >> 6;
            int yIndex = (int) yIntercept >> 6;

            if (yIndex < Grid.getSize() && xIndex < Grid.getSize() && yIndex >= 0 && xIndex >= 0 && Grid.getGrid()[yIndex][xIndex] == 1) {      
                depth = DOF;
                break;
                
            } else {
                xIntercept += dx;
                yIntercept += dy;
                depth++;
            }
        }

        return Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept));
    }

    // Cast one ray from the player.
    public double[] rayCast() {

        // Number of rays to cast.
        int nOfRays = 500;

        // Position and angle of the ray.
        double rayAngle = orientation + (PI / 6);
        double[] distances = new double[nOfRays];

        // Cast rays.
        for (int i = 0; i < nOfRays; i++) {

            // Normalize the angle.
            if (rayAngle < 0) {
                rayAngle += 2 * PI;
            } else if (rayAngle > 2 * PI) {
                rayAngle -= 2 * PI;
            }

            // Avoid values where tan is Undefined and avoid division by 0
            if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2 || rayAngle == 0 || rayAngle == PI) {
                rayAngle += 0.0001;
            }
            
            // Check horizontal Lines and vertical lines
            double distH = castForHorizontalDistance(rayAngle);
            double distV = castForVerticalDistance(rayAngle);
            distances[i] = Math.min(distH, distV);
            
            rayAngle -= App.getAngleIncrement();
        }

        return distances;
    }

    public void moveForward() {
        double newPosX = x + Math.cos(orientation) * speedMultiplier;
        double newPosY = y + Math.sin(orientation) * speedMultiplier;
        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }
    
    public void moveBackward() {
        double newPosX = x - Math.cos(orientation) * speedMultiplier;
        double newPosY = y - Math.sin(orientation) * speedMultiplier;
        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public void rotateRight() {
        orientation -= ROTATION_INCREMENT;
        if (orientation < 0) {
            orientation += 2 * PI;
        }
    }

    public void rotateLeft() {
        orientation += ROTATION_INCREMENT;
        if (orientation > 2 * PI) {
            orientation -= 2 * PI;
        }
    }

    public double getOrientation() {
        return orientation;
    }

    public static int getFOV() {
        return FOV;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public int getHealth() {
        return health;
    }   

    public void takeDamage(int damage) {
        health -= damage;
    }
}