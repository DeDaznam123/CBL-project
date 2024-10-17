import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 1537;
    public static final int HEIGHT  = 795;
    
    private static final double PI = Math.PI;

    // planeCenter / tan(FOV / 2)
    private static final int DISTANCE_PLAYER_TO_PLANE =  (int) (WIDTH / 2 
        / Math.tan(Math.toRadians(Player.getFOV()) / 2));

    // How much to rotate after each ray cast. (1 degree)
    private static final double ANGLE_INCREMENT = PI / 180;

    private Player player = new Player(100, 100);
    private Enemy enemy = new Enemy(player);
    private double rayAngle = 0;
    
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
        enemy.spawn();

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

        System.out.println(player.getOrientation());
        
        // Sky and ground.
        g2d.setColor(new Color(90, 90, 200));
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT);
        
        double[] distances = player.rayCast();

        for (int i = 0; i < 500; i++) {
            projectedHeight = 64 / distances[i] * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(i * (WIDTH / 120), (int) (HEIGHT - projectedHeight) / 2, WIDTH / 120, (int) projectedHeight);
        }
    }

    /**
     * Draws mini map.
     * @param g2d Graphics2D.
     */
    public void drawMiniMap(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(24, 24, 151, 151);
        
        for (int i = 0; i < Grid.getSize(); i++) {
            for (int j = 0; j < Grid.getSize(); j++) {
                if (Grid.getGrid()[j][i] == 1) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(25 + i * 15,
                        25 + j * 15, 14, 14);
                }
            }
        }

        g2d.fillRect((int) (player.getX() * 0.234275) + 25,
            (int) (player.getY() / 6.4 * 1.5) + 25, 3, 3);
        
        g2d.setColor(Color.ORANGE);
        g2d.fillRect((int) (enemy.getX() / 6.4 * 1.5) + 25,
            (int) (enemy.getY() / 6.4 * 1.5) + 25, 3, 3);
    }
    
    /**
     * Draws the enemy on the screen.
     * @param g2d Graphics.
     * @param enemy Enemy.
     * @param player Player.
     */
    public void drawEnemy(Graphics2D g2d, Enemy enemy, Player player) {

        // Calculate the relative position of the enemy to the player
        double dx = enemy.getX() - player.getX();
        double dy = enemy.getY() - player.getY();
        
        // Calculate the distance to the enemy
        double distance = Math.sqrt(dx * dx + dy * dy);
        double enemySize = enemy.getSize();

        int projectedSize = (int) (enemySize * DISTANCE_PLAYER_TO_PLANE / (distance));

        if (distance > 20) {
            enemy.move(player);
        }

        // Calculate the angle between the player's orientation and the enemy
        double angleToEnemy = Math.atan2(dy, dx) - player.getOrientation();

        // Normalize the angle to the range [-pi, pi]
        if (angleToEnemy < -Math.PI) {
            angleToEnemy += 2 * Math.PI;
        }
        if (angleToEnemy > Math.PI) {
            angleToEnemy -= 2 * Math.PI;
        }

        // Check if the enemy is within the player's field of view
        double halfFOV = Math.toRadians(Player.getFOV() / 2.0);
        if (Math.abs(angleToEnemy) > Math.PI / 6) {
            return; // Enemy is outside the player's field of view
        }
        
        // Project the enemy's position onto the 2D screen
        int screenX = (int) (((angleToEnemy) / halfFOV) * (WIDTH / 2) + (WIDTH / 2));

        // Set the color for the enemy square and draw it.
        g2d.setColor(Color.RED);
        g2d.fillRect(screenX - projectedSize / 2, HEIGHT / 2, projectedSize, projectedSize);
    
        g2d.setColor(Color.BLACK);
        g2d.fillRect(screenX, HEIGHT / 2, 5, 5);

    }

    public static double getAngleIncrement() {
        return ANGLE_INCREMENT;
    }

    public double getRayAngle() {
        return rayAngle;
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