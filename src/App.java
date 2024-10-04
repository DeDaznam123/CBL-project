import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel{
    // Dimensions of the Projection Plane (pixels).
    public static final int PLANE_WIDTH  = 320;
    public static final int PLANE_HEIGHT  = 200;

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 277;

    public static final double ANGLE_INCREMENT = Player.getFOV() / PLANE_WIDTH; 

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        performRayCastingAndDrawWalls(g2d);

        try {
            Thread.sleep(1000/60);
        } catch (Exception e) {
            repaint();
        }
    }
    
    public void performRayCastingAndDrawWalls(Graphics2D g2d) {

        //g2d.setBackground(Color.WHITE);
        //g2d.clearRect(0, 0, WIDTH, HEIGHT);

        double distance;
        double projectedHeight;

        for (int x = 0; x < PLANE_WIDTH; x++) {
            distance = Player.castRay(x);
            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(x, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight + 60);
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DOOM-Like");

            App app = new App();
            app.setBackground(Color.WHITE);
            
            // Attach panel to frame + frame settings.
            frame.add(app);
            frame.setSize(WIDTH, HEIGHT);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            app.requestFocus();
        });
    }

}