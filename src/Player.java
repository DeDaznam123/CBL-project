import java.util.Random;

/**
 * Player.
 */
public class Player {

    // Field of view of the Player
    private static final int FOV = 60;

    // Value of pi.
    private static final double PI = Math.PI;

    // Depth of field (Largest side of the grid.)
    private static final int DOF = Grid.getSize() * Grid.getSize();
    // How much to rotate the player.
    private static final double ROTATION_INCREMENT = 0.015;

    // Distance at which player collides with a wall.
    private static final int HITBOX_SIZE = 5;

    // Maximum health of the player.
    private static final int MAX_HEALTH = 100;

    // Position of the player.
    private double x;
    private double y;

    // Orientation in radians relative to absolute east (like unit circle).
    private double orientation;

    // Health and score of the player.
    private int health;
    private int score;

    private int damage = 20;

    private double rayAngle;

    private double speedMultiplier = 2;

    private boolean isAlive = true;

    /**
     * Constructor.
     * @param x x.
     * @param y y.
     */
    public Player(double x, double y) {
        this.score = 0;
        this.health = MAX_HEALTH;
        orientation = 0;
        respawn();
    }

    public void shoot(Enemy enemy) {
        if (enemy.isAimedAt()) {
            enemy.takeDamage(damage);
        }
    }

    public double[] castForHorizontalDistance(double rayAngle) {
        double aTan = -1 / Math.tan(rayAngle);
        double xIntercept = 0;
        double yIntercept = 0;
        double dx = 0;
        double dy = 0;
        double depth = 0;

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

            if(yIndex >= 0 && xIndex >= 0 && yIndex < Grid.getSize() && xIndex < Grid.getSize() && Grid.getGrid()[xIndex][yIndex]==1){
                depth = DOF;
                break;
                
            } else {
                xIntercept += dx;
                yIntercept += dy;
                depth++;
            }
        }

        double[] result = {Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept)), xIntercept, yIntercept};
        return result;
    }

    public double[] castForVerticalDistance(double rayAngle) {
        double nTan = -Math.tan(rayAngle);
        double xIntercept = 0;
        double yIntercept = 0;
        double dx = 0;
        double dy = 0;
        double depth = 0;

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

            if(yIndex < Grid.getSize() && xIndex < Grid.getSize() && yIndex >= 0 && xIndex >= 0 && Grid.getGrid()[xIndex][yIndex]==1){      
                depth = DOF;
                break;
                
            } else {
                xIntercept += dx;
                yIntercept += dy;
                depth++;
            }
        }

        double[] result = {Math.sqrt((y - yIntercept) * (y - yIntercept) + (x - xIntercept) * (x - xIntercept)), xIntercept, yIntercept};
        return result;
    }

    // Cast one ray from the player.
    public double[][] castRay(double i) {

        // Angle of the ray.
        rayAngle = orientation - (PI / 6) + (i * App.getAngleIncrement());
        rayAngle = (rayAngle + 2 * PI) % (2 * PI);

        // Avoid values where tan is Undefined and avoid division by 0
        if (rayAngle == PI / 2 || rayAngle == 3 * PI / 2 || rayAngle == 0 || rayAngle == PI) {
            rayAngle += 0.0001;
        }
        
        // Check horizontal Lines and vertical lines
        double[] distH = castForHorizontalDistance(rayAngle);
        double[] distV = castForVerticalDistance(rayAngle);

        // Adjust for fish eye effect.
        distH[0] = distH[0] * Math.cos(orientation - rayAngle);
        distV[0] = distV[0] * Math.cos(orientation - rayAngle);

        return new double[][] {distH, distV};
    }

    public void moveForward() {
        double newPosX = x + Math.cos(orientation) * speedMultiplier;
        double newPosY = y + Math.sin(orientation) * speedMultiplier;
    
        // Check for collision along the x-axis (including the hitbox).
        if (!Grid.isInWall(newPosX + Math.signum(Math.cos(orientation)) * HITBOX_SIZE, y)) {
            x = newPosX;
        }
    
        // Check for collision along the y-axis (including the hitbox).
        if (!Grid.isInWall(x, newPosY + Math.signum(Math.sin(orientation)) * HITBOX_SIZE)) {
            y = newPosY;
        }
    }
    
    public void moveBackward() {
        double newPosX = x - Math.cos(orientation) * speedMultiplier;
        double newPosY = y - Math.sin(orientation) * speedMultiplier;
    
        // Check for collision along the x-axis (including the hitbox).
        if (!Grid.isInWall(newPosX - Math.signum(Math.cos(orientation)) * HITBOX_SIZE, y)) {
            x = newPosX;
        }
    
        // Check for collision along the y-axis (including the hitbox).
        if (!Grid.isInWall(x, newPosY - Math.signum(Math.sin(orientation)) * HITBOX_SIZE)) {
            y = newPosY;
        }
    }

    public void rotateLeft() {
        orientation -= ROTATION_INCREMENT;
        if (orientation < 0) {
            orientation += 2 * PI;
        }
    }

    public void rotateRight() {
        orientation += ROTATION_INCREMENT;
        if (orientation > (2 * PI)) {
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
    
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void takeDamage(int damage) {
        if (health - damage <= 0) {
            isAlive = false;
            return;
        }
        health -= damage;
    }

    public void respawn() {
        health = MAX_HEALTH;
        score = 0;

        Random rand = new Random();
        int gridSize = Grid.getSize();
        int cellSize = Grid.getCellSize();
        int spawnX;
        int spawnY;

        do {
            spawnX = rand.nextInt(gridSize * cellSize);
            spawnY = rand.nextInt(gridSize * cellSize);
        } while (Grid.isInWall(spawnX, spawnY));

        x = spawnX;
        y = spawnY;
        
        isAlive = true;
        Grid.generateGrid();
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void addPowerUp(Powerup powerup) {
        health = Math.min(MAX_HEALTH, (int) (health + powerup.getHealthBonus()));
        score += powerup.getScoreBonus();
        speedMultiplier += powerup.getSpeedBonus();
        damage += powerup.getDamageBonus();
    }
}