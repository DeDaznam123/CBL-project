import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App extends JPanel {

    // Dimensions of the Projection Plane (pixels).
    public static final int PLANE_WIDTH  = 320;
    public static final int PLANE_HEIGHT  = 200;

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 277;

    public static final double ANGLE_INCREMENT = Player.getFOV() / PLANE_WIDTH; 

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

        //g2d.setBackground(Color.WHITE);
        //g2d.clearRect(0, 0, WIDTH, HEIGHT);

        double distance;
        double projectedHeight;

        for (int x = 0; x < PLANE_WIDTH; x++) {
            distance = Player.castRay(x);
            projectedHeight = 64 / distance * DISTANCE_PLAYER_TO_PLANE;
            g2d.fillRect(x, (int) (HEIGHT - projectedHeight) / 2, 1, (int) projectedHeight);
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            App app = new App();

            JFrame frame = new JFrame("DOOM-Like");
            frame.setLayout(new FlowLayout());
            frame.setSize(WIDTH, HEIGHT);

            frame.add(app);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

}