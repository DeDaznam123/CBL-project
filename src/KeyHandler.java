import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles the keyboard input from the player.
 */
public class KeyHandler implements KeyListener {
    public boolean wPressed;
    public boolean sPressed;
    public boolean aPressed;
    public boolean dPressed;
    public boolean escPressed;

    @Override
    public void keyPressed(KeyEvent arg0) {
        int key = arg0.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
                wPressed = true;
                break;
            case KeyEvent.VK_S:
                sPressed = true;
                break;
            case KeyEvent.VK_A:
                aPressed = true;
                break;
            case KeyEvent.VK_D:
                dPressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                escPressed = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        int key = arg0.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
                wPressed = false;
                break;
            case KeyEvent.VK_S:
                sPressed = false;
                break;
            case KeyEvent.VK_A:
                aPressed = false;
                break;
            case KeyEvent.VK_D:
                dPressed = false;
                break;
            case KeyEvent.VK_ESCAPE:
                escPressed = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) { }
}
