
import java.util.Random;

public class Enemy {

    protected int speed;
    protected int health;
    protected int[] position;
    protected int score;
    protected int[][] grid;

    public Enemy(int[][] grid){ 
        this.speed = 3;
        this.score = 50;
        this.grid = grid;
        this.position = new int[2];
    }

    public void move(Player player){
        if (player.position[0] > this.position[0]) {
            this.position[0] += this.speed;
        } 
        if (player.position[0] < this.position[0]) {
            this.position[0] -= this.speed;
        } 
        if (player.position[1] > this.position[1]) {
            this.position[1] += this.speed;
        } 
        if (player.position[1] < this.position[1]) {
            this.position[1] -= this.speed;
        }
    }

    public void takeDamage(int damage){
        this.health -= damage;
        if (this.health <= 0) {
            this.spawn();
        }
    }

    public void spawn(){
        Random rand = new Random();
        int p;
        do {
            p = rand.nextInt(grid.length * grid[0].length);
            position[0] = p / grid[0].length * 64;
            position[1] = p % grid[0].length * 64;
        } while (grid[position[0]][position[1]] != 0);
        health = 100;
    }

   

}