import java.util.Random;

public class Powerup {
    private double healthBonus;
    private double damageBonus;
    private double speedBonus;
    private double scoreBonus;

    private double x;
    private double y;
    
    public Powerup(int healthBonus, int damageBonus, int speedBonus, int scoreBonus) {
        this.healthBonus = healthBonus;
        this.damageBonus = damageBonus;
        this.speedBonus = speedBonus;
        this.scoreBonus = scoreBonus;
        spawn();
    }

    public void spawn() {
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
    }

    public double getDamageBonus() {
        return damageBonus;
    }

    public double getHealthBonus() {
        return healthBonus;
    }

    public double getSpeedBonus() {
        return speedBonus;
    }

    public double getScoreBonus() {
        return scoreBonus;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
