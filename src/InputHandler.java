import java.awt.event.*;

/**
 * Handles the keyboard input from the player.
 */
public class InputHandler implements KeyListener, MouseListener {
    public boolean wPressed;
    public boolean sPressed;
    public boolean aPressed;
    public boolean dPressed;
    public boolean pPressed;

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
            case KeyEvent.VK_P:
                pPressed = true;
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
            case KeyEvent.VK_P:
                pPressed = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) { }

    // Mouse related events

    public boolean mouseClicked = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseClicked = true;
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
