
import java.util.Random;

public class Enemy {

    protected int speed;
    protected int health;
    protected double x,y;
    protected int score;
    protected Player player;

    public Enemy(Player player){ 
        this.speed = 3;
        this.score = 50;
        this.player = player;
    }

    public void move(){
        double newX=this.x,newY=this.y;
        if (player.getX() > this.x) {
            newX += this.speed;
        } 
        if (player.getX() < this.x) {
            newX -= this.speed;
        } 
        if (player.getY() > this.y) {
            newY += this.speed;
        } 
        if (player.getY() < this.y) {
            newY -= this.speed;
        }
        if(!Grid.isInWall(newX, newY)){
            this.x = newX;
            this.y = newY;
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
            p = rand.nextInt(Grid.getWidth() * Grid.getHeight());
            x = p / Grid.getWidth() * 64;
            y = p % Grid.getHeight() * 64;
        } while (Grid.isInWall(x, y));
        health = 100;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    
   

}