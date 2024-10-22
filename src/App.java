import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

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

    // FPS goal.
    private static final int FPS = 60;
    private static final double TIME_PER_FRAME = 1000000000 / FPS;

    // Variables for FPS counter.
    private static long fpsTimer = System.nanoTime();  // Timer to reset every second
    private static long lastTime = System.nanoTime();   // To track time between frames
    private static int frames = 0;
    private static int fps = 0;

    private static boolean paused = false;

    // KeyHandler.
    InputHandler inputHandler = new InputHandler();
    Thread gameThread;

    // Textures.
    private BufferedImage skyTexture;
    private int skyWidth;

    private BufferedImage wallTexture;
    private int wallTextureWidth;

    private BufferedImage powerupTexture;

    private static Map<String, Clip> soundMap;

    JButton resumeButton = new JButton("Resume");
    JButton restartButton = new JButton("Restart");
    JButton exitButton = new JButton("Exit");

    Powerup[] powerups = new Powerup[3];

    /**
     * App constructor.
     */
    public App() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.addKeyListener(inputHandler);
        this.addMouseListener(inputHandler);
        this.setFocusable(true);

        init();

        // Load textures.
        try {
            skyTexture = ImageIO.read(new File("resources/sky_textures/sky2.png"));
            skyWidth = skyTexture.getWidth();
            wallTexture = ImageIO.read(new File("resources/wall_textures/brick5.png"));
            wallTextureWidth = wallTexture.getWidth();
            powerupTexture = ImageIO.read(new File("resources/other/hp.png"));

            soundMap = new HashMap<>();
            File[] soundFiles = (new File("resources/sounds")).listFiles(
                (dir, name) -> (name.endsWith(".wav"))
            );

            for (File soundFile : soundFiles) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                soundMap.put(soundFile.getName(), clip);
                System.out.println("Loaded sound: " + soundFile.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        startGame();
    }

    public void init() {
        // Resume button.
        resumeButton.setBounds(WIDTH / 2 - 150, HEIGHT / 2 - 145, 300, 50);
        resumeButton.setFont(FONT);
        resumeButton.setFocusPainted(false);
        resumeButton.setBorderPainted(false);
        resumeButton.setBackground(new Color(45, 45, 45));
        resumeButton.setForeground(Color.WHITE);
        resumeButton.setOpaque(true);

        resumeButton.addActionListener(e -> {
            paused = false;
            resumeButton.setVisible(false);
            restartButton.setVisible(false);
            exitButton.setVisible(false);
        });

        resumeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resumeButton.setBackground(new Color(60, 60, 60));
                playSound("blip.wav");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                resumeButton.setBackground(new Color(45, 45, 45));
            }
        });

        // Restart button.
        restartButton.setBounds(WIDTH / 2 - 150, HEIGHT / 2 - 85, 300, 50);
        restartButton.setFont(FONT);
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.setBackground(new Color(45, 45, 45));
        restartButton.setForeground(Color.WHITE);
        restartButton.setOpaque(true);

        restartButton.addActionListener(e -> restartApplication());

        restartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(new Color(60, 60, 60));
                playSound("blip.wav");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(new Color(45, 45, 45));
            }
        });

        // Exit button.
        exitButton.setBounds(WIDTH / 2 - 150, HEIGHT / 2 - 25, 300, 50);
        exitButton.setFont(FONT);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setBackground(new Color(45, 45, 45));
        exitButton.setForeground(Color.WHITE);
        exitButton.setOpaque(true);

        exitButton.addActionListener(e -> exitApplication());

        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(60, 60, 60));
                playSound("blip.wav");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(45, 45, 45));
            }
        });

        // Hide until paused.
        resumeButton.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);

        this.add(resumeButton);
        this.add(restartButton);
        this.add(exitButton);
    }

    private void restartApplication() {
        try {
            String java = System.getProperty("java.home") + "/bin/java";
            String classpath = System.getProperty("java.class.path");
            // Build the command to restart the application
            ProcessBuilder processBuilder = new ProcessBuilder(java, "-cp", classpath, this.getClass().getName());
            processBuilder.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        exitApplication();
    }

    private void exitApplication() {
        System.exit(0);
    }

    /**
     * Start the thread attach Runnable App.
     */
    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        Grid.generateGrid();
        enemy.spawn();

        powerups[0] = new Powerup(50, 5, 0, 5);
        powerups[1] = new Powerup(50, 5, 0, 5);
        powerups[2] = new Powerup(50, 5, 0, 5);

        while (gameThread != null) {
            playTheme();
            handlePausingInput();

            // Calculate time elapsed since last frame.
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;

            // Only draw if unpaused.
            if (!paused) {
                updatePlayer();
                updateEnemy();
                updatePowerUps();
                
                // Invisible cursor.
                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(
                        16, 
                        16, 
                        BufferedImage.TYPE_INT_ARGB), 
                        new Point(0, 0), 
                        "Invisible Cursor")
                    );
            }

            repaint();
            frames++;

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
    
    public void playTheme() {
        Clip clip = soundMap.get("theme.wav");
        if (!clip.isRunning()) {
            clip.setFramePosition(0); 
            clip.start();
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

        if (distance < 64) {
            player.takeDamage(enemy.getDamage());
        } else {
            enemy.move();
        }
    }
    
    /**
     * Updates the enemy's aim.
     * @param g2d Graphics2D.
     * @param enemyScreenX Screen x-coordinate of the enemy.
     * @param enemySize Size of the enemy.
     */
    public void updateAim(Graphics2D g2d, int enemyScreenX, int enemySize) {
        if (enemyScreenX < WIDTH / 2 + enemySize / 2 && enemyScreenX > WIDTH / 2 - enemySize / 2) {
            enemy.setAimedAt(true);
        } else {
            enemy.setAimedAt(false);
        }
    }

    /**
     * Updates the player's position according to keystrokes.
     */
    public void updatePlayer() {
        handleMovementInput();
        handleShootingInput();
    }

    public void updatePowerUps() {
        for (Powerup powerup : powerups) {
            double distance = Math.sqrt(Math.pow(player.getX() - powerup.getX(), 2) + Math.pow(player.getY() - powerup.getY(), 2));
            if (distance < 64) {
                playSound("blip.wav");
                player.addPowerUp(powerup);
                powerup.spawn();
            }
        }
    }

    /**
     * Handles player movement.
     */
    public void handleMovementInput() {
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
    }

    /**
     * Handles player shooting.
     */
    public void handleShootingInput() {
        if (inputHandler.mouseClicked) {
            player.shoot(enemy);
            inputHandler.mouseClicked = false;
            
            playSound("gun.wav");
        }
    }

    public static void playSound(String name) {
        Clip clip = soundMap.get(name);
        if (clip.isRunning()) {
            clip.stop(); 
        }
        clip.setFramePosition(0); 
        clip.start();
    }

    /**
     * Handles pausing the game.
     */
    public void handlePausingInput() {
        if (inputHandler.escPressed) {
            paused ^= true;
            inputHandler.escPressed = false;
            resumeButton.setVisible(false);
            restartButton.setVisible(false);
            exitButton.setVisible(false);
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
        drawPowerUps(g2d);

        // Draw UI.
        drawMiniMap(g2d);
        drawFPSCounter(g2d);
        drawScore(g2d);
        drawHealthBar(g2d);
        drawCursor(g2d);

        if (paused) {
            drawPauseScreen(g2d);
        }
    }

    /**
     * Draws the sky texture.
     * @param g2d Graphics2D.
     */
    public void drawSky(Graphics2D g2d) {
        int halfHeight = HEIGHT;

        // How much to offset the texture depending on player orientation.
        int offset = (int) ((player.getOrientation() / (2 * Math.PI)) * skyWidth 
            * (360.0 / Player.getFOV()) % skyWidth);
    
        if (offset < 0) {
            offset += skyWidth;
        }

        // Draw the sky texture with wrapping
        for (int x = -offset; x < WIDTH; x += skyWidth) {
            g2d.drawImage(skyTexture, x, 0, skyWidth, halfHeight, null);
        }
    }
    
    /**
     * Draws the raycasted walls.
     * @param g2d Graphics2D.
     */
    public void drawWalls(Graphics2D g2d) {
        double distance;
        int textureX;

        // Draw floor.
        g2d.setColor(new Color(146, 136, 62));
        g2d.fillRect(0, HEIGHT / 2, WIDTH, HEIGHT);
        
        for (int x = 0; x < WIDTH; x++) {
            double[][] distanceTypes = player.castRay(x);

            if (distanceTypes[0][0] < distanceTypes[1][0]) {
                distance = distanceTypes[0][0];
                textureX = (int) distanceTypes[0][1] % wallTextureWidth;

                g2d.setColor(new Color(80, 80, 80));
            } else {
                distance = distanceTypes[1][0];
                textureX = (int) distanceTypes[1][2] % wallTextureWidth;
                g2d.setColor(new Color(100, 100, 100));
            }

            int projectedHeight = (int) (64 / distance * DISTANCE_PLAYER_TO_PLANE);
            int drawStart = (int) ((HEIGHT / 2) - (projectedHeight / 2));
            int drawEnd = (int) (drawStart + projectedHeight);
            
            g2d.drawImage(
                wallTexture,  // Image object for the wall texture
                x, drawStart, 
                x + 1, drawEnd,  // Position on the screen to draw
                textureX, 0, 
                textureX + 1, wallTexture.getHeight(),  // Texture slice to draw
                null  
            );
        }
    }

    /**
     * Draws the enemy on the screen in a 2.5D raycasted style.
     * @param g2d Graphics2D.
     */
    public void drawEnemy(Graphics2D g2d) {

        // Calculate distance from player to enemy
        double deltaX = enemy.getX() - player.getX();
        double deltaY = enemy.getY() - player.getY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        // Calculate angle between player's orientation and enemy
        double relativeAngle = Math.atan2(deltaY, deltaX) - player.getOrientation();
        
        relativeAngle = normalizeAngle(relativeAngle);

        // Project enemy onto screen
        int enemyScreenX = (int) 
            (WIDTH / 2 + Math.tan(relativeAngle) * DISTANCE_PLAYER_TO_PLANE);
        // Adjust size based on distance
        int enemySize = (int) 
            (enemy.getSize() / distance * DISTANCE_PLAYER_TO_PLANE); 

        updateAim(g2d, enemyScreenX, enemySize);

        // Clamp relative angle to field of view
        double halfFOV = Math.toRadians(Player.getFOV()) / 2;
        if (relativeAngle < -halfFOV || relativeAngle > halfFOV) {
            return; // Enemy is outside the field of view
        }

        // Draw the enemy as a rectangle
        int adjustedX = enemyScreenX - enemySize / 2;
        int adjustedY = HEIGHT / 2 - enemySize / 2;

        double[][] castResults = player.castRay(adjustedX + enemySize / 2);
        if (distance > Math.min(castResults[0][0], castResults[1][0])) {
            return;
        }

        // Draw enemy.
        g2d.drawImage(enemy.getTexture(), adjustedX, adjustedY, enemySize, enemySize, null);
        drawEnemyHealthBar(g2d, enemyScreenX, enemySize);
    }

    public void drawPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(FONT);
        g2d.drawString("Paused", WIDTH / 2 - 30, 100);
        g2d.drawString("© 2024 Victor Handzhiev & Miguel Lebrun", WIDTH / 2 - 145, HEIGHT - 100);
        
        resumeButton.setVisible(true);
        restartButton.setVisible(true);
        exitButton.setVisible(true);

        setCursor(Cursor.getDefaultCursor());
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
     * Draws mini map.
     * @param g2d Graphics2D.
     */
    public void drawMiniMap(Graphics2D g2d) {

        int size = Grid.getSize();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(24, 24, 151, 151);
        g2d.setColor(Color.BLACK);
        
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

        for (Powerup powerup : powerups) {
            int powerupX = scaleToMiniMap(powerup.getX());
            int powerupY = scaleToMiniMap(powerup.getY());
            g2d.setColor(Color.BLUE);
            g2d.fillRect(powerupX, powerupY, 3, 3);
        }
    }

    public int scaleToMiniMap(double x) {
        return (int) (x / (Grid.getCellSize() / (150 / (double) Grid.getSize()))) + 25;
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
     * Draws the enemy health bar.
     * @param g2d Graphics2D.
     * @param enemyScreenX Screen x-coordinate of the enemy.
     * @param enemySize Size of the enemy.
     */
    public void drawEnemyHealthBar(Graphics2D g2d, int enemyScreenX, int enemySize) {
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

    public void drawPowerUps(Graphics2D g2d) {
        for (Powerup powerup : powerups) {
            // Calculate distance from player to enemy
            double deltaX = powerup.getX() - player.getX();
            double deltaY = powerup.getY() - player.getY();
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            // Calculate angle between player's orientation and enemy
            double relativeAngle = Math.atan2(deltaY, deltaX) - player.getOrientation();
            
            relativeAngle = normalizeAngle(relativeAngle);

            // Project enemy onto screen
            int powerupScreenX = (int) 
                (WIDTH / 2 + Math.tan(relativeAngle) * DISTANCE_PLAYER_TO_PLANE);
            // Adjust size based on distance
            int powerupSize = (int) 
                (16 / distance * DISTANCE_PLAYER_TO_PLANE); 
            
            // Clamp relative angle to field of view
            double halfFOV = Math.toRadians(Player.getFOV()) / 2;
            if (relativeAngle < -halfFOV || relativeAngle > halfFOV) {
                continue; 
            }

            // Draw the enemy as a rectangle
            int adjustedX = powerupScreenX - powerupSize / 2;
            int adjustedY = HEIGHT / 2 - powerupSize / 2;

            double[][] castResults = player.castRay(adjustedX + powerupSize / 2);
            if (distance > Math.min(castResults[0][0], castResults[1][0])) {
                continue;
            }

            // Draw powerup.
            g2d.drawImage(powerupTexture, adjustedX, adjustedY, powerupSize, powerupSize, null);
        }
    }

    /**
     * Draws the cursor.
     * @param g2d Graphics2D.
     */
    public void drawCursor(Graphics2D g2d) {

        // Length of the crosshair.
        int size = 20;
    
        g2d.setColor(Color.WHITE);
        g2d.fillRect(WIDTH / 2 - size / 2, HEIGHT / 2 - 1, size, 2);
        g2d.fillRect(WIDTH / 2 - 1, HEIGHT / 2 - size / 2, 2, size);
    
        g2d.setColor(Color.BLACK);
        g2d.drawRect(WIDTH / 2 - size / 2, HEIGHT / 2 - 1, size, 2);
        g2d.drawRect(WIDTH / 2 - 1, HEIGHT / 2 - size / 2, 2, size);
    }
    
    /**
     * Normalizes an angle to be between -PI and PI.
     * @param angle Angle to normalize.
     * @return Normalized angle.
     */
    public static double normalizeAngle(double angle) {
        double angleNormalized = angle;
        if (angle < -Math.PI) {
            angleNormalized += 2 * Math.PI;
        } else if (angleNormalized > Math.PI) {
            angleNormalized -= 2 * Math.PI;
        }
        return angleNormalized;
    }

    public static double getAngleIncrement() {
        return ANGLE_INCREMENT;
    }

    public Map<String, Clip> getSoundMap() {
        return soundMap;
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