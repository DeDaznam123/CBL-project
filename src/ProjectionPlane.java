import java.awt.*;
import javax.swing.*;

/**
 * JPanel that represents the plane that is the screen.
 */
public class ProjectionPlane extends JPanel {
    
    // Dimensions of the Projection Plane (pixels).
    public static final int PLANE_WIDTH  = 320;
    public static final int PLANE_HEIGHT  = 200;

    // Center coordinates of the Projection Plane.
    public static final int PLANE_CENTER_X = PLANE_WIDTH / 2;
    public static final int PLANE_CENTER_Y = PLANE_HEIGHT / 2;

    // planeCenter / tan(FOV / 2)
    public static final int DISTANCE_PLAYER_TO_PLANE = 277;

    // Angle increment
    public static final double ANGLE_INCREMENT =  Player.getFOV() / PLANE_WIDTH;

    public static double[] distances;

    public ProjectionPlane() {
        distances = Player.rayCast();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, PLANE_HEIGHT, PLANE_WIDTH, 0);

    }

    public int getWidth() {
        return PLANE_WIDTH;
    }

    public static double getAngleIncrement() {
        return ANGLE_INCREMENT;
    }

}