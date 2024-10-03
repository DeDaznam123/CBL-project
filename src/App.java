import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App {

    /**
     * Constructor...
     */
    public App() {
        // Avoids race conditions or something.
        SwingUtilities.invokeLater(() -> {
            
            // Set Frame title.
            JFrame frame = new JFrame("DOOM-Like");

            // Create Projection Plane
            ProjectionPlane mainPanel = new ProjectionPlane();

            // Attach panel to frame + frame settings.
            frame.add(mainPanel);
            frame.setSize(740, 400);
            frame.setResizable(false);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        });
    }
    

    public static void main(String[] args) throws Exception {
        // Start app.
        App app = new App();
    }
}