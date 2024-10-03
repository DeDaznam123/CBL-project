import java.awt.*;
import javax.swing.*;

/**
 * Main JPanel.
 */
public class App {

    JFrame frame;
    // TODO: Custom implementation of JPanel.
    JPanel mainPanel;

    /**""
     * Constructor...
     */
    public App() {
        // Avoids race conditions or something.
        SwingUtilities.invokeLater(() -> {
            
            // Set Frame title.
            frame = new JFrame("DOOM-Like");
            mainPanel = new JPanel();

            mainPanel.setBackground(new Color(255, 255, 255));
            mainPanel.add(new JLabel("Hello world!!!!"));

            // Attach panel to frame and make visible and exitable.
            frame.add(mainPanel);
            frame.setSize(740, 400);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        });
    }
    

    public static void main(String[] args) throws Exception {
        // Start app.
        App app = new App();
    }
}