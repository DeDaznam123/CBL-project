
/**
 * Faster enemy class.
 */
public class FasterEnemy extends Enemy {
    
    /**
     * Constructor for FasterEnemy.
     * @param player Player.
     */
    public FasterEnemy(Player player) {
        super(player);
        this.speed = 5;
        this.score = 100;
    }
    
}
