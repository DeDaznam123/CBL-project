
/**
 * Player.
 */
public class Player {

    // Field of view of the Player
    private static final int FOV = 60;

    // Depth of field (Largest side of the grid.)
    private static final int DOF = 15;
    //(Grid.getHeight() < Grid.getWidth()) ? Grid.getWidth() : Grid.getHeight();

    // How much to rotate the player.
    private static final double ROTATION_INCREMENT = 0.1;

    // Value of pi.
    private static final double PI = Math.PI;

    // Position of the player.
    private double x;
    private double y;

    // Orientation in radians relative to absolute east (like unit circle).
    private static double orientation;
    
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        orientation = 90;
    }

    public double castForHorizontalDistance(double rayAngle, double yIntercept, double dy, double xIntercept, double dx, double tan, double depth) {
        // If the ray is facing up.
        double aTan=-1/tan;
        if(rayAngle>PI){
            yIntercept = y-(y%64)-0.0001;
            xIntercept = (y-yIntercept)*aTan+x;
            dy = -64;
            dx = -dy*aTan;
        }
        if(rayAngle<PI){
            yIntercept = y-(y%64)+64;
            xIntercept = (y-yIntercept)*aTan+x;
            dy = 64;
            dx = dy*aTan;
        }
        if(rayAngle==0||rayAngle==PI){
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth<DOF){
            int xIndex = (int)xIntercept >> 6;
            int yIndex = (int)yIntercept >> 6;
            if(yIndex < Grid.getHeight() && xIndex < Grid.getWidth() && yIndex >= 0 && xIndex >= 0){
                if(Grid.getGrid()[xIndex][yIndex]==1){
                    depth = DOF;
                    break;
                }
            }
            xIntercept+=dx;
            yIntercept+=dy;
            depth++;

        }
        //System.out.println("xIntercept: " + xIntercept + " yIntercept: " + yIntercept);
        return Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept));
        // if (rayAngle > PI) {
        //     yIntercept = (((int) y >> 8) << 8) - 1; // -1 ensures that the intercept belongs to cell above.
        //     dy = -64;
        // // If the ray is facing down.
        // } else if (rayAngle < PI) {
        //     yIntercept = (((int) y >> 8) << 8) + 64;
        //     dy = 64; 
        // }

        // xIntercept = x + (y - yIntercept) / tan;
        // dx = 64 / tan;

        // if (rayAngle == 0 || rayAngle == PI) {
        //     xIntercept = x;
        //     yIntercept = y;
        //     depth = DOF;
        // }

        // System.out.println("Before DOF");

        // // Keep adding the delta until wall is found
        // while (depth < DOF) {
        //     depth++;
        //     System.out.println("After DOF");

        //     if (Grid.getGrid()[(int) xIntercept >> 8][(int) yIntercept >> 8] == 1) {
        //         depth = DOF;
        //         break;
        //     }

        //     xIntercept += dx;
        //     yIntercept += dy;
        // }

        //return Math.abs((x - xIntercept) / Math.cos(rayAngle));
        
    }

    public double castForVerticalDistance(double rayAngle, double yIntercept, double dy, double xIntercept, double dx, double tan, double depth) {
        // If the ray is facing right.
        double nTan=-tan;
        if(rayAngle>PI/2 && rayAngle<3*PI/2){
            xIntercept = x-(x%64)-0.0001;
            yIntercept = (x-xIntercept)*nTan+y;
            dx = -64;
            dy = -dy*nTan;
        }
        if(rayAngle<PI/2 || rayAngle>3*PI/2){
            xIntercept = x-(x%64)+64;
            xIntercept = (x-xIntercept)*nTan+y;
            dx = 64;
            dy = dy*nTan;
        }
        if(rayAngle==0||rayAngle==PI){
            xIntercept = x;
            yIntercept = y;
            depth = DOF;
        }

        while(depth<DOF){
            int xIndex = (int)xIntercept >> 6;
            int yIndex = (int)yIntercept >> 6;
            if(yIndex < Grid.getHeight() && xIndex < Grid.getWidth() && yIndex >= 0 && xIndex >= 0){
                if(Grid.getGrid()[xIndex][yIndex]==1){
                    depth = DOF;
                    break;
                }
            }
            xIntercept+=dx;
            yIntercept+=dy;
            depth++;

        }
        //System.out.println("xIntercept: " + xIntercept + " yIntercept: " + yIntercept);
        return Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept));
        // if (rayAngle < (PI / 2) || rayAngle > ((3 * PI) / 2)) {
        //     xIntercept = ((int) x >> 8) << 8 - 1;
        //     dx = 64;
            
        // // If the ray is facing left.
        // } else if (rayAngle > (PI / 2) && rayAngle < ((3 * PI) / 2)) {
        //     yIntercept = ((int) x >> 8) << 8 + 64;
        //     dx = -64;
        // }

        // yIntercept = (x - xIntercept) / tan;
        // dy = 64 * tan;

        // if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2) {
        //     xIntercept = x;
        //     yIntercept = y;
        //     depth = DOF;
        // }
        
        // // Keep adding the delta until wall is found
        // while (depth < DOF) {
        //     depth++;

        //     if (Grid.getGrid()[(int) xIntercept >> 8][(int) yIntercept >> 8] == 1) {
        //         depth = DOF;
        //         break;
        //     }

        //     xIntercept += dx;
        //     yIntercept += dy;
        // }

        //return Math.abs((x - xIntercept) / Math.cos(rayAngle));

    }


    // Cast one ray from the player.
    public double castRay(double i) {

        // Position and angle of the ray.
        double xIntercept = x;
        double yIntercept = y;
        double rayAngle = orientation + (i * App.ANGLE_INCREMENT);
        
        //System.out.println("RayAngle: " + rayAngle);

        double tan;

        // Avoid values where tan is Undefined and avoid division by 0
        if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2) {
            rayAngle += 0.0001;
        } else if (rayAngle == 0 || rayAngle == PI) {
            rayAngle += 0.0001;
        }
        tan = Math.tan(rayAngle);
        
        //System.out.println("Tan: " + tan);

        // Check horizontal Lines and vertical lines
        double distH = castForHorizontalDistance(rayAngle, yIntercept, 0, xIntercept, 0, tan, 0);
        double distV = castForVerticalDistance(rayAngle, yIntercept, 0, xIntercept, 0, tan, 0);

        //System.out.println("Distances (h, v): (" + distH + ", " + distV + ")");
        return Math.min(distH, distV);
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
        if (orientation > (2 * PI)) {
            orientation -= 2 * PI;
        }
    }

    public void rotateRight() {
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

}