import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel implements Runnable {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 1024;
    public static final int HEIGHT  = 700;

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 887;

    // How much to rotate after each ray cast.
    public static final double ANGLE_INCREMENT = (double) Player.getFOV() / (double) WIDTH; 

    public Player player = new Player(100, 100);
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
        double distance;
        double projectedHeight;
        g2d.setColor(Color.RED);

        for (int i = 0; i < Grid.getWidth(); i++){
            for (int j = 0; j < Grid.getHeight(); j++) {
                if (Grid.getGrid()[i][j] == 1) {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(i * Grid.getCellSize(), j * Grid.getCellSize(), Grid.getCellSize()-1, Grid.getCellSize()-1);
                }else{
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(i * Grid.getCellSize(), j * Grid.getCellSize(), Grid.getCellSize(), Grid.getCellSize());
                }
            }
        }

        g2d.setColor(Color.RED);
        g2d.fillRect((int) player.getX(), (int) player.getY(), 10, 10);

        for (int i = 0; i < WIDTH; i++) {
            distance = player.castRay(i);

            double x1 = player.getX() + distance * Math.cos(player.getOrientation() + (i * ANGLE_INCREMENT));
            double y1 = player.getY() + distance * Math.sin(player.getOrientation() + i * (ANGLE_INCREMENT));
            g.drawLine((int)player.getX(), (int)player.getY(), (int)x1, (int)y1);

            //projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            //System.out.println("Projected Height: " + projectedHeight);
            //g2d.fillRect(i, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);

        }
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