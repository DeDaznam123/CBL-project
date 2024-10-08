
/**
 * Player.
 */
public class Player {

    // Field of view of the Player
    private static final int FOV = 60;

    // Depth of field (Largest side of the grid.)
    private static final int DOF = (Grid.getHeight() < Grid.getWidth()) ? Grid.getWidth() : Grid.getHeight();

    // How much to rotate the player.
    private static final double ROTATION_INCREMENT = 0.02;

    // Value of pi.
    private static final double PI = Math.PI;

    // Position of the player.
    private double x;
    private double y;
    private int health;

    // Orientation in radians relative to absolute east (like unit circle).
    private static double orientation;

    private double speedMultiplier = 4;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        orientation = 0;
    }

    public double castForHorizontalDistance(double rayAngle, double yIntercept, double dy, double xIntercept, double dx, double tan, double depth) {
        double aTan = -1 / tan;

        // Facing up.
        if (rayAngle > PI){
            yIntercept = y - (y % 64) - 0.0001;
            xIntercept = (y - yIntercept) * aTan + x;
            dy = -64;
            dx = -dy * aTan;
        }
        // Facing down.
        if (rayAngle < PI){
            yIntercept = y - (y % 64) + 64;
            xIntercept = (y - yIntercept) * aTan + x;
            dy = 64;
            dx = -dy * aTan;
        }
        // Facing directly left or right.
        if (rayAngle == 0 || rayAngle == PI){
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth < DOF){
            int xIndex = (int) xIntercept >> 6;
            int yIndex = (int) yIntercept >> 6;

            if(yIndex < Grid.getHeight() && xIndex < Grid.getWidth() && yIndex >= 0 && xIndex >= 0 && Grid.getGrid()[xIndex][yIndex]==1){

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

    public double castForVerticalDistance(double rayAngle, double yIntercept, double dy, double xIntercept, double dx, double tan, double depth) {
        double nTan = -tan;
        
        // If the ray is facing left.
        if (rayAngle > PI/2 && rayAngle < 3*PI/2){
            xIntercept = x - (x % 64) - 0.0001;
            yIntercept = (x - xIntercept) * nTan + y;
            dx = -64;
            dy = -dx * nTan;
        }
        // If the ray is facing right.
        if (rayAngle < PI/2 || rayAngle > 3*PI/2){
            xIntercept = x - (x % 64) + 64;
            yIntercept = (x - xIntercept) * nTan + y;
            dx = 64;
            dy = -dx * nTan;
        }
        // Up / Down.
        if (rayAngle == 0 || rayAngle == PI){
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth < DOF){
            int xIndex = (int)xIntercept >> 6;
            int yIndex = (int)yIntercept >> 6;

            if(yIndex < Grid.getHeight() && xIndex < Grid.getWidth() && yIndex >= 0 && xIndex >= 0 && Grid.getGrid()[xIndex][yIndex]==1){      
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
    public double[] castRay(double i) {

        // Position and angle of the ray.
        double xIntercept = 0;
        double yIntercept = 0;
        double rayAngle = orientation + (i * App.getAngleIncrement());
        rayAngle = (rayAngle + 2 * PI) % (2 * PI);
        
        // System.out.println("RayAngle: " + rayAngle);
        double tan;

        // Avoid values where tan is Undefined and avoid division by 0
        if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2) {
            rayAngle += 0.0001;
        } else if (rayAngle == 0 || rayAngle == PI) {
            rayAngle += 0.0001;
        }
        
        tan = Math.tan(rayAngle);

        // Check horizontal Lines and vertical lines
        double distH = castForHorizontalDistance(rayAngle, yIntercept, 0, xIntercept, 0, tan, 0);
        double distV = castForVerticalDistance(rayAngle, yIntercept, 0, xIntercept, 0, tan, 0);

        return new double[] {distH, distV};
    }

    public void moveForward() {
        double newPosX = x + Math.cos(orientation+((App.WIDTH * App.getAngleIncrement())/2)) * speedMultiplier;
        double newPosY = y + Math.sin(orientation+((App.WIDTH * App.getAngleIncrement())/2)) * speedMultiplier;
        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public void moveBackward() {
        double newPosX = x - Math.cos(orientation + ((App.WIDTH * App.getAngleIncrement()) / 2)) * speedMultiplier;
        double newPosY = y - Math.sin(orientation + ((App.WIDTH * App.getAngleIncrement()) / 2)) * speedMultiplier;
        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    public void rotateRight() {
        orientation += ROTATION_INCREMENT;
        if (orientation > (2 * PI)) {
            orientation -= 2 * PI;
        }
    }

    public void rotateLeft() {
        orientation -= ROTATION_INCREMENT;
        if (orientation < 0) {
            orientation += 2 * PI;
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