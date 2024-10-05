import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 1920;
    public static final int HEIGHT  = 1200;
    

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 887;

    // How much to rotate after each ray cast.
    public static final double ANGLE_INCREMENT = Math.toRadians(Player.getFOV()) / (double) WIDTH;

    public Player player = new Player(100, 100);
    public Enemy enemy = new Enemy(player);
    public int FPS = 60;   
    // Keybindings.
    
    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;

    public App() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        startGame();
    }

    public void startGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run(){
        double timePerFrame = 1000000000 / FPS;
        double nextFrameTime = System.nanoTime() + timePerFrame;
        enemy.spawn();
        while(gameThread != null){
            if(System.nanoTime() > nextFrameTime){
                nextFrameTime = System.nanoTime() + timePerFrame;
                update();
                repaint();
            }
        }
    }
    
    public void update(){
        if(keyHandler.wPressed){
            player.moveForward();
        }
        if(keyHandler.sPressed){
            player.moveBackward();
        }
        if(keyHandler.aPressed){
            player.rotateLeft();
        }
        if(keyHandler.dPressed){
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

    public void drawMap(Graphics2D g2d){
        double distance;
        double projectedHeight;
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        for (int i = 0; i < WIDTH; i++) {
            double[] distanceTypes = player.castRay(i);
            if(distanceTypes[0] < distanceTypes[1]){
                distance = distanceTypes[0];
                g2d.setColor(new Color(80, 80, 80));
            }else{
                distance = distanceTypes[1];
                g2d.setColor(new Color(100, 100, 100));
            }
            // double x1 = player.getX() + distance/6.4 * Math.cos(player.getOrientation() + (i * ANGLE_INCREMENT));
            // double y1 = player.getY() + distance/6.4 * Math.sin(player.getOrientation() + (i * ANGLE_INCREMENT));
            // double rayAngle = player.getOrientation() + (i * App.ANGLE_INCREMENT);
            // rayAngle = (rayAngle + 2 * PI) % (2 * PI); 

            // double x1 = player.getX() + distance * Math.cos(rayAngle);
            // double y1 = player.getY() + distance * Math.sin(rayAngle);
            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(i, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
            // g.drawLine((int)(player.getX()/6.4)+WIDTH-150, (int) (player.getY()/6.4)+50, (int)x1, (int)y1);

        }
    }

    public void drawMiniMap(Graphics2D g2d){
        g2d.setColor(Color.WHITE);
        g2d.fillRect(WIDTH-151, 49, 101, 101);
        for (int i = 0; i < Grid.getWidth(); i++){
            for (int j = 0; j < Grid.getHeight(); j++) {
                if (Grid.getGrid()[i][j] == 1) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect((int)(WIDTH-Grid.getWidth()*10-50)+i * 10, 50+j * 10, 9, 9);
                }else{
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect((int)(WIDTH-Grid.getWidth()*10-50)+i * 10, 50+j * 10, 10, 10);
                }
            }
        }
        g2d.fillRect((int) (player.getX()/6.4)+(int)(WIDTH-Grid.getWidth()*10-50), (int) (player.getY()/6.4)+50, 3, 3);
    }

    public void drawEnemy(Graphics2D g2d, Enemy enemy){
        // double enemyDistance = Math.sqrt(Math.pow(player.getX()-enemy.getX(), 2) + Math.pow(player.getY()-enemy.getY(), 2));
        // double projectedEnemyHeight = 64 / enemyDistance * DISTANCE_PLAYER_TO_PLANE;
        // g2d.fillRect(,(int) (HEIGHT - projectedEnemyHeight) / 2,10, (int) projectedEnemyHeight);
        double angle = player.getOrientation()+(WIDTH * ANGLE_INCREMENT)/2;
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);
        double cos = Math.cos(angle), sin = Math.sin(angle);
        double a = (enemy.getX()-player.getX())*sin+(enemy.getY()-player.getY())*cos;
        double b = (enemy.getX()-player.getX())*cos-(enemy.getY()-player.getY())*sin; 
        double screenX = a, screenY=b;
        screenX = (screenX*DISTANCE_PLAYER_TO_PLANE/screenY)+WIDTH/2;
        screenY = (DISTANCE_PLAYER_TO_PLANE/screenY)+HEIGHT/2;
        g2d.setColor(Color.RED);
        g2d.fillRect((int)screenX, (int)screenY, 40, 40);

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