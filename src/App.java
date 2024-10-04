import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel {

    // Dimensions of the Projection Plane (pixels).
    public static final int WIDTH  = 320;
    public static final int HEIGHT  = 200;

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 277;

    // How much to rotate after each ray cast.
    public static final double ANGLE_INCREMENT = Player.getFOV() / WIDTH; 

    // Keybindings.
    private Action w = new AbstractAction("w") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player.moveForward();
            System.out.println("w");
        }
    };
    private Action s = new AbstractAction("s") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player.moveBackward();
            System.out.println("s");
        }
    };
    private Action a = new AbstractAction("a") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player.rotateLeft();
            System.out.println("a");
        }
    };
    private Action d = new AbstractAction("d") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player.rotateRight();
            System.out.println("d");
        }
    };


    public App() {
        this.setSize(WIDTH, HEIGHT);

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "w");
        this.getActionMap().put("w", w);

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "s");
        this.getActionMap().put("s", s);

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "a");
        this.getActionMap().put("a", a);

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "d");
        this.getActionMap().put("d", d);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        performRayCastingAndDrawWalls(g2d);
    }
    
    public void performRayCastingAndDrawWalls(Graphics2D g2d) {
        double distance;
        double projectedHeight;
        g2d.setColor(Color.RED);

        for (int i = 0; i < WIDTH; i++) {
            System.out.println(i);
            distance = Player.castRay(i);
            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            System.out.println("Projected Height: " + projectedHeight);
            g2d.fillRect(i, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
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