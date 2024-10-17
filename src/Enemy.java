import java.util.Random;

/**
 * Enemy class.
 */
public class Enemy {

    protected double speed;
    protected int health;

    protected double x;
    protected double y;

    protected int scoreValue;
    protected Player player;
    protected int damage;
    double orientation;

    /**
     * Enemy constructor.
     * @param player Player.
     */
    public Enemy(Player player) { 
        this.speed = 1;
        this.scoreValue = 50;
        this.player = player;
        this.damage = 1;
        this.health = 100;
    }

    /**
     * Moves the enemy towards the player.
     */
    public void move(Player p) {
        
        orientation = Math.atan2(p.getY() - y, p.getX() - x);
        if (orientation > 2 * Math.PI) {
            orientation -= 2 * Math.PI;
        }

        double newPosX = x + Math.cos(orientation) * speed;
        double newPosY = y + Math.sin(orientation) * speed;

        if (!Grid.isInWall(newPosX, newPosY)) {
            x = newPosX;
            y = newPosY;
        }
    }

    /**
     * Removes health from the player.
     * @param damage How much health to remove.
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.spawn();
        }
    }

    /**
     * Spawns the enemy in a random square on the grid.
     */
    public void spawn() {
        health = 100;

        Random rand = new Random();
        int gridWidth = Grid.getWidth();
        int gridHeight = Grid.getHeight();
        int cellSize = Grid.getCellSize();
        
        do {
            x = rand.nextInt(gridWidth * cellSize);
            y = rand.nextInt(gridHeight * cellSize);
        } while (Grid.isInWall(x, y));
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
}