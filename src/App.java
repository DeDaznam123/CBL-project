import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 1920;
    public static final int HEIGHT  = 1080;
    
    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE =  (int) (WIDTH / 2 
        / Math.tan(Math.toRadians(Player.getFOV()) / 2));

    // How much to rotate after each ray cast.
    public static final double ANGLE_INCREMENT = Math.toRadians(Player.getFOV()) / (double) WIDTH;

    public Player player = new Player(100, 100);
    public Enemy enemy = new Enemy(player);
    public static final int FPS = 60;
    
    // Keybindings.
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

    public void run() {
        double timePerFrame = 1000000000 / FPS;
        double nextFrameTime = System.nanoTime() + timePerFrame;
        enemy.spawn();

        while (gameThread != null) {
            if (System.nanoTime() > nextFrameTime) {
                nextFrameTime = System.nanoTime() + timePerFrame;
                update();
                repaint();
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
        drawEnemy(g2d, enemy);
    }

    /**
     * Draws the 2D top down map.
     * @param g2d Graphics2D.
     */
    public void drawMap(Graphics2D g2d) {
        double distance;
        double projectedHeight;
        
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT / 2);
        g2d.setColor(Color.darkGray);
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
            // double x1 = player.getX() + distance/6.4 * 
            // Math.cos(player.getOrientation() + (i * ANGLE_INCREMENT));

            // double y1 = player.getY() + distance/6.4 * 
            //Math.sin(player.getOrientation() + (i * ANGLE_INCREMENT));

            // double rayAngle = player.getOrientation() + (i * App.ANGLE_INCREMENT);
            // rayAngle = (rayAngle + 2 * PI) % (2 * PI); 

            // double x1 = player.getX() + distance * Math.cos(rayAngle);
            // double y1 = player.getY() + distance * Math.sin(rayAngle);

            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(i, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
                        
            // g.drawLine((int)(player.getX()/6.4)+WIDTH-150, 
            // (int) (player.getY()/6.4)+50, (int)x1, (int)y1);

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
                } else {
                    g2d.setColor(Color.WHITE); 
                    g2d.fillRect(25 + i * 15,
                        25 + j * 15, 15, 15);
                }
            }
        }
        g2d.fillRect((int) (player.getX() / 6.4 * 1.5) + 25,
            (int) (player.getY() / 6.4 * 1.5) + 25, 3, 3);
    }

    /**
     * Draw one enemy.
     * @param g2d Graphics2D.
     * @param enemy Enemy to draw.
     */
    public void drawEnemy(Graphics2D g2d, Enemy enemy) {
        // double enemyDistance = Math.sqrt(Math.pow(player.getX()-enemy.getX(), 2) 
        // + Math.pow(player.getY()-enemy.getY(), 2));
        // double projectedEnemyHeight = 64 / enemyDistance * DISTANCE_PLAYER_TO_PLANE;
        // g2d.fillRect(,(int) (HEIGHT - projectedEnemyHeight) / 2,10, (int) projectedEnemyHeight);
        double angle = player.getOrientation() + (WIDTH * ANGLE_INCREMENT) / 2;
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double a = (enemy.getX() - player.getX()) * sin + (enemy.getY() - player.getY()) * cos;
        double b = (enemy.getX() - player.getX()) * cos - (enemy.getY() - player.getY()) * sin;
        double screenX = a;
        double screenY = b;
        screenX = (screenX * DISTANCE_PLAYER_TO_PLANE / screenY) + WIDTH / 2;
        screenY = (DISTANCE_PLAYER_TO_PLANE / screenY) + HEIGHT / 2;
        g2d.setColor(Color.RED);
        g2d.fillRect((int) screenX, (int) screenY, 40, 40);

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
        });
    }

}