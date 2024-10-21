import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int WIDTH = (int) screenSize.getWidth();
    public static final int HEIGHT = (int) screenSize.getHeight();

    // Font for all text in UI.
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    
    // Distance between the player and the projection plane.
    private static final int DISTANCE_PLAYER_TO_PLANE =  (int) (WIDTH / 2 
        / Math.tan(Math.toRadians(Player.getFOV()) / 2));
    // How much to rotate after each ray cast.
    private static final double ANGLE_INCREMENT = Math.toRadians(Player.getFOV()) / (double) WIDTH;

    private Player player = new Player(100, 100);
    private Enemy enemy = new Enemy(player);
    private static boolean paused = false;

    // FPS goal.
    private static final int FPS = 60;
    private static final double TIME_PER_FRAME = 1000000000 / FPS;

    // Variables for FPS counter.
    private static long fpsTimer = System.nanoTime();  // Timer to reset every second
    private static long lastTime = System.nanoTime();   // To track time between frames
    private static int frames = 0;
    private static int fps = 0;

    // KeyHandler.
    InputHandler inputHandler = new InputHandler();
    Thread gameThread;

    // Texture of the skydome.
    private BufferedImage skyTexture;
    private int skyWidth;


    /**
     * App constructor.
     */
    public App() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.addKeyListener(inputHandler);
        this.addMouseListener(inputHandler);
        this.setFocusable(true);

        // Load textures.
        try {
            skyTexture = ImageIO.read(new File("resources/sky3.png"));
            skyWidth = skyTexture.getWidth();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        enemy.respawn();
        Grid.generateGrid();

        while (gameThread != null) {
            if (!paused) {

                // Calculate time elapsed since last frame.
                long now = System.nanoTime();
                long elapsed = now - lastTime;
                lastTime = now;

                //Update, render, increase frame count.
                updatePlayer();
                repaint();
                frames++;

                // Update enemy position and check if should deal damage.
                updateEnemy();

                // FPS Calculation every 1 second (1e9 ns).
                if (now - fpsTimer >= 1e9) {
                    updateFPS();
                }

                // If frame finished early, sleep to reach FPS target.
                if (elapsed < TIME_PER_FRAME) {
                    try {
                        Thread.sleep((long) ((TIME_PER_FRAME - elapsed) / 1e6));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }   
                
            }
        }
    }
    
    /**
     * Enemy logic.
     */
    public void updateEnemy() {
        // Calculate distance from player to enemy
        double deltaX = enemy.getX() - player.getX();
        double deltaY = enemy.getY() - player.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < 32) {
            player.takeDamage(enemy.getDamage());
        } else {
            enemy.move();
        }
    }
    
    /**
     * Updates the player's position.
     */
    public void updatePlayer() {
        if (inputHandler.wPressed) {
            player.moveForward();
        }
        if (inputHandler.sPressed) {
            player.moveBackward();
        }
        if (inputHandler.aPressed) {
            player.rotateLeft();
        }
        if (inputHandler.dPressed) {
            player.rotateRight();
        }
        if (inputHandler.mouseClicked) {
            player.shoot(enemy);
            inputHandler.mouseClicked = false;
        }
    }

    /**
     * Updates the FPS counter.
     */
    public void updateFPS() {
        fps = frames;
        frames = 0;
        fpsTimer += 1e9;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw world and enemy.
        drawSky(g2d);
        drawWalls(g2d);
        drawEnemy(g2d);

        // Draw UI.
        drawMiniMap(g2d);
        drawFPSCounter(g2d);
        drawScore(g2d);
        drawHealthBar(g2d);
        drawCursor(g2d);
    }

    public void drawSky(Graphics2D g2d) {
        int halfHeight = HEIGHT;

        // How much to offset the texture depending on player orientation.
        int offset = (int) ((-player.getOrientation() / (2 * Math.PI)) * skyWidth * (360.0 / Player.getFOV()) % skyWidth);
    
        if (offset < 0) {
            offset += skyWidth;
        }

        // Draw the sky texture with wrapping
        for (int x = -offset; x < WIDTH; x += skyWidth) {
            g2d.drawImage(skyTexture, x, 0, skyWidth, halfHeight, null);
        }
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

        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 15);
        g2d.setFont(f);
        g2d.drawString("HP: " + health, 185, 180 + f.getSize());

        g2d.fillRect(24, 182, 158, 33);

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

        g2d.fillRect(28,  186, (int) ((double) health * 1.5), 25);
    }

    /**
     * Draws the FPS text.
     * @param g2d Graphics2D.
     */
    public void drawFPSCounter(Graphics g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(FONT);
        g2d.drawString("FPS: " + fps,  180, 40);
    }

    /**
     * Draws the raycasted walls.
     * @param g2d Graphics2D.
     */
    public void drawWalls(Graphics2D g2d) {
        double distance;
        double projectedHeight;
        double[] distanceTypes;

        // Draw floor.
        g2d.setColor(new Color(146,71,56));
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT);

        for (int x = 0; x < WIDTH; x++) {
            distanceTypes = player.castRay(x);

            if (distanceTypes[0] < distanceTypes[1]) {
                distance = distanceTypes[0];
                g2d.setColor(new Color(80, 80, 80));
            } else {
                distance = distanceTypes[1];
                g2d.setColor(new Color(100, 100, 100));
            }

            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(x, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
        }


    }

    /**
     * Draws mini map.
     * @param g2d Graphics2D.
     */
    public void drawMiniMap(Graphics2D g2d) {

        int size = Grid.getSize();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(24, 24, 151, 151);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Grid.getGrid()[i][j] == 1) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(25 + i * (150 / size),
                        25 + j * (150 / size), (150 / size) - 1, (150 / size) - 1);
                }
            }
        }
        // Draw player position
        int playerX = scaleToMiniMap(player.getX());
        int playerY = scaleToMiniMap(player.getY());
        int enemyX = scaleToMiniMap(enemy.getX());
        int enemyY = scaleToMiniMap(enemy.getY());

        // Draw player.
        g2d.fillRect(playerX, playerY, 3, 3);

        // Draw player direction.
        int indicatorX = (int) (playerX + 10 * Math.cos(player.getOrientation()));
        int indicatorY = (int) (playerY + 10 * Math.sin(player.getOrientation()));
        g2d.setColor(Color.RED);
        g2d.drawLine(playerX + 1, playerY + 1, indicatorX + 1, indicatorY + 1);

        // Draw enemy.
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(enemyX, enemyY, 3, 3);
    }

    public int scaleToMiniMap(double x) {
        return (int) (x / (Grid.getCellSize() / (150 / (double) Grid.getSize()))) + 25;
    }
    
    /**
     * Draws the enemy on the screen in a 2.5D raycasted style.
     * @param g2d Graphics2D.
     * @param enemy Enemy.
     * @param player Player.
     */
    
    public void drawEnemy(Graphics2D g2d) {

        // Calculate distance from player to enemy
        double deltaX = enemy.getX() - player.getX();
        double deltaY = enemy.getY() - player.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
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

        if (distance > Math.min(player.castRay(adjustedX + enemySize / 2)[0],
            player.castRay(adjustedX + enemySize / 2)[1])) {
            return;
        }

        g2d.setColor(Color.RED);
        g2d.fillRect(adjustedX, adjustedY, enemySize, enemySize);

        if (enemyScreenX < WIDTH / 2 + enemySize / 2 && enemyScreenX > WIDTH / 2 - enemySize / 2) {
            enemy.setAimedAt(true);
        } else {
            enemy.setAimedAt(false);
        }
        
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
        g2d.setFont(FONT);
        g2d.drawString("Score: " + player.getScore(), 185, 180 + 2 * FONT.getSize());
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