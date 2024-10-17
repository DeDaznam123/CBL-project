import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 1537;
    public static final int HEIGHT  = 795;
    
    // planeCenter / tan(FOV / 2)
    private static final int DISTANCE_PLAYER_TO_PLANE =  (int) (WIDTH / 2 
        / Math.tan(Math.toRadians(Player.getFOV()) / 2));

    // How much to rotate after each ray cast.
    private static final double ANGLE_INCREMENT = Math.toRadians(Player.getFOV()) / (double) WIDTH;

    private Player player = new Player(100, 100);
    private Enemy enemy = new Enemy(player);
    
    private static final int FPS = 60;
    private long lastTime = System.nanoTime();   // To track time between frames

    // Variables for FPS counter
    private int frames = 0;
    private long fpsTimer = System.nanoTime();  // Timer to reset every second
    private int fps = 0;  // Store calculated FPS

    boolean paused = false;

    // KeyHandler.
    KeyHandler keyHandler = new KeyHandler();

    Thread gameThread;

    /**
     * App constructor.
     */
    public App() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        startGame();
    }

    /**
     * Start the thread attach Runnable App.
     */
    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pauseGame() { }

    public void run() {
        double timePerFrame = 1000000000 / FPS;
        enemy.respawn();

        while (gameThread != null) {

            if (!paused) {
                // Calculate time elapsed since last frame.
                long now = System.nanoTime();
                long elapsed = now - lastTime;
                lastTime = now;

                //Update, render, increase frame count.
                update();
                repaint();
                frames++;

                // FPS Calculation every 1 second (1e9 ns).
                if (now - fpsTimer >= 1e9) {
                    fps = frames;
                    frames = 0;
                    fpsTimer += 1e9;
                }

                // If frame finished early, sleep to reach FPS target.
                if (elapsed < timePerFrame) {
                    try {
                        // Convert nanoseconds to milliseconds
                        Thread.sleep((long) ((timePerFrame - elapsed) / 1e6));  
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }   
            }
        }
    }
    
    /**
     * Updates the player's position.
     */
    public void update() {
        if (keyHandler.wPressed) {
            player.moveForward();
        }
        if (keyHandler.sPressed) {
            player.moveBackward();
        }
        if (keyHandler.aPressed) {
            player.rotateLeft();
        }
        if (keyHandler.dPressed) {
            player.rotateRight();
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawMap(g2d);
        drawMiniMap(g2d);
        drawEnemy(g2d, enemy, player);
        drawFPSCounter(g2d);
        drawHealthBar(g2d);
        drawScore(g2d);
        drawCursor(g2d);
    }

    /**
     * Draws the cursor.
     * @param g2d Graphics2D.
     */
    public void drawCursor(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawLine(WIDTH / 2 - 10, HEIGHT / 2, WIDTH / 2 + 10, HEIGHT / 2);
        g2d.drawLine(WIDTH / 2, HEIGHT / 2 - 10, WIDTH / 2, HEIGHT / 2 + 10);
    }

    /**
     * Draws the health bar.draw
     * @param g2d Graphics2D.
     */
    public void drawHealthBar(Graphics2D g2d) {
        int health = player.getHealth();
        
        g2d.setColor(Color.BLACK);
        g2d.drawString("HP: " + health, 24, 200);
        g2d.fillRect(25, 210, 158, 35);

        // Change color based on health.
        if (health < 25) {
            g2d.setColor(Color.RED);
        } else if (health < 50) {
            g2d.setColor(Color.YELLOW);
        } else if (health < 75) {
            g2d.setColor(new Color(0, 255, 0));
        } else {
            g2d.setColor(new Color(0, 102, 0));
        }

        g2d.fillRect(29, 214, (int) ((double) health * 1.5), 27);
    }

    /**
     * Draws the FPS text.
     * @param g2d Graphics2D.
     */
    public void drawFPSCounter(Graphics g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g2d.drawString("FPS: " + fps, 185, 40);
    }

    /**
     * Draws the 2D top down map.
     * @param g2d Graphics2D.
     */
    public void drawMap(Graphics2D g2d) {
        double distance;
        double projectedHeight;
        
        g2d.setColor(new Color(90, 90, 200));
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT);
        
        double[] distanceTypes;

        for (int i = 0; i < WIDTH; i++) {

            distanceTypes = player.castRay(i);

            if (distanceTypes[0] < distanceTypes[1]) {
                distance = distanceTypes[0];
                g2d.setColor(new Color(80, 80, 80));
            } else {
                distance = distanceTypes[1];
                g2d.setColor(new Color(100, 100, 100));
            }

            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(i, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
        }
    }

    /**
     * Draws mini map.
     * @param g2d Graphics2D.
     */
    public void drawMiniMap(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(24, 24, 151, 151);
        
        for (int i = 0; i < Grid.getWidth(); i++) {
            for (int j = 0; j < Grid.getHeight(); j++) {
                if (Grid.getGrid()[i][j] == 1) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(25 + i * 15,
                        25 + j * 15, 14, 14);
                }
            }
        }

        g2d.fillRect((int) (player.getX() / 6.4 * 1.5) + 25,
            (int) (player.getY() / 6.4 * 1.5) + 25, 3, 3);
        
        g2d.setColor(Color.ORANGE);
        g2d.fillRect((int) (enemy.getX() / 6.4 * 1.5) + 25,
            (int) (enemy.getY() / 6.4 * 1.5) + 25, 3, 3);
    }
    
    /**
     * Draws the enemy on the screen in a 2.5D raycasted style.
     * @param g2d Graphics2D.
     * @param enemy Enemy.
     * @param player Player.
     */
    public void drawEnemy(Graphics2D g2d, Enemy enemy, Player player) {

        // Calculate distance from player to enemy
        double deltaX = enemy.getX() - player.getX();
        double deltaY = enemy.getY() - player.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        enemy.takeDamage(1);
        
        if (distance < 32) {
            player.takeDamage(enemy.getDamage());
        } else {
            enemy.move(player);
        }
        
        // Calculate angle between player's orientation and enemy
        double relativeAngle = Math.atan2(deltaY, deltaX) - player.getOrientation();
        
        // Normalize angle to be between -PI and PI
        if (relativeAngle < -Math.PI) {
            relativeAngle += 2 * Math.PI;
        } else if (relativeAngle > Math.PI) {
            relativeAngle -= 2 * Math.PI;
        }

        // Clamp relative angle to field of view
        double halfFOV = Math.toRadians(Player.getFOV()) / 2;
        if (relativeAngle < -halfFOV || relativeAngle > halfFOV) {
            return; // Enemy is outside the field of view
        }

        // Project enemy onto screen
        int enemyScreenX = (int) 
            (WIDTH / 2 + Math.tan(relativeAngle) * DISTANCE_PLAYER_TO_PLANE * -1);
        // Adjust size based on distance
        int enemySize = (int) 
            (16 / distance * DISTANCE_PLAYER_TO_PLANE); 

        // Draw the enemy as a rectangle
        int adjustedX = enemyScreenX - enemySize / 2;
        int adjustedY = HEIGHT / 2 - enemySize / 2;
        g2d.setColor(Color.RED);
        g2d.fillRect(adjustedX, adjustedY, enemySize, enemySize);

        // Enemy health bar.
        int healthBarWidth = 30;
        int healthBarHeight = 5;
        int healthBarX = enemyScreenX - healthBarWidth / 2;
        int healthBarY = HEIGHT / 2 - enemySize / 2 - healthBarHeight - 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(healthBarX, healthBarY, 
            (int) (healthBarWidth * (enemy.health / 100.0)), healthBarHeight);
    }

    /**
     * Draws the player's score.
     * @param g2d Graphics2D.
     */
    public void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g2d.drawString("Score: " + player.getScore(), 185, 60);
    }

    public static double getAngleIncrement() {
        return ANGLE_INCREMENT;
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            
            JFrame frame = new JFrame("DOOM-Like");
            frame.add(app);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }

}