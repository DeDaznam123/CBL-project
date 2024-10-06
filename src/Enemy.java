import java.util.Random;

/**
 * Enemy class.
 */
public class Enemy {

    protected int speed;
    protected int health;
    protected double x;
    protected double y;
    protected int score;
    protected Player player;

    /**
     * Enemy constructor.
     * @param player Player.
     */
    public Enemy(Player player) { 
        this.speed = 3;
        this.score = 50;
        this.player = player;
    }

    /**
     * Moves the enemy towards the player.
     */
    public void move() {
        double newX = this.x;
        double newY = this.y;

        if (player.getX() > this.x) {
            newX += this.speed;
        } else if (player.getX() < this.x) {
            newX -= this.speed;
        }
        
        if (player.getY() > this.y) {
            newY += this.speed;
        } else if (player.getY() < this.y) {
            newY -= this.speed;
        }
        
        if (!Grid.isInWall(newX, newY)) {
            this.x = newX;
            this.y = newY;
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
        Random rand = new Random();
        int p;
        do {
            p = rand.nextInt(Grid.getWidth() * Grid.getHeight());
            x = p / Grid.getWidth() * 64;
            y = p % Grid.getHeight() * 64;
        } while (Grid.isInWall(x, y));
        health = 100;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}