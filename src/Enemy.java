import java.util.*;

/**
 * Enemy class.
 */
public class Enemy {

    protected double speed;
    protected int health;

    protected double x;
    protected double y;
    protected double nextX;
    protected double nextY;

    protected int scoreValue;
    protected int damage;
    protected double orientation;
    protected int size = 16;

    protected Player player;
    protected boolean aimedAt = false;

    List<int[]> path = new ArrayList<int[]>();
    int counter;

    int[] oldEnd;

    /**
     * Enemy constructor.
     * @param player Player.
     */
    public Enemy(Player player) { 
        this.speed = 0.05;
        this.scoreValue = 50;
        this.player = player;
        this.damage = 1;
        this.health = 100;

    }

    public void move() {
        int cellSize = Grid.getCellSize();
    
        // Position of enemy on grid.
        int[] start = {(int) x / cellSize, (int) y / cellSize};
        // Position of player on grid.
        int[] end = {(int) player.getX() / cellSize, (int) player.getY() / cellSize};
    
        // Recalculate path if the player has moved or no path found.
        if (!Arrays.equals(oldEnd, end) || path == null || path.isEmpty()) {
            path = Grid.performAStar(start, end);
            oldEnd = end;
            counter = 0;  
        }
    
        // Move enemy along path.
        if (path != null && !path.isEmpty()) {

            // Next step in the path.
            int[] step = path.get(counter);
            nextX = step[0] * cellSize + cellSize / 2;
            nextY = step[1] * cellSize + cellSize / 2;
    
            // Smooth interpolation.
            x = lerp(x, nextX, speed);
            y = lerp(y, nextY, speed);
    
            // Avoid getting stuck at tiny distances.
            if (Math.abs(x - nextX) < 1 && Math.abs(y - nextY) < 1) {
                counter++;  // Move to the next step in the path.
    
                // Enemy reached end of path.
                if (counter >= path.size()) {
                    counter = 0;
                }
            }
        }
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Removes health from the player.
     * @param damage How much health to remove.
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.respawn();
        }
    }

    public void setAimedAt(boolean aimedAt) {
        this.aimedAt = aimedAt;
    }

    public boolean isAimedAt() {
        return aimedAt;
    }

    /**
     * Spawns the enemy in a random square on the grid.
     */
    public void respawn() {
        health = 100;
        player.addScore(scoreValue);

        Random rand = new Random();
        int gridSize = Grid.getSize();
        int cellSize = Grid.getCellSize();
        
        do {
            x = rand.nextInt(gridSize * cellSize);
            y = rand.nextInt(gridSize * cellSize);
        } while (Grid.isInWall(x, y));

        path.clear();
        oldEnd = null;
        counter = 0;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getHealth() {
        return health;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}