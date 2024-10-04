import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean wPressed, sPressed, aPressed, dPressed;
    @Override
    public void keyPressed(KeyEvent arg0) {
        int key = arg0.getKeyCode();
        if(key == KeyEvent.VK_W){
            wPressed = true;
        }
        if(key == KeyEvent.VK_S){
            sPressed = true;
        }
        if(key == KeyEvent.VK_A){
            aPressed = true;
        }
        if(key == KeyEvent.VK_D){
            dPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        int key = arg0.getKeyCode();
        if(key == KeyEvent.VK_W){
            wPressed = false;
        }
        if(key == KeyEvent.VK_S){
            sPressed = false;
        }
        if(key == KeyEvent.VK_A){
            aPressed = false;
        }
        if(key == KeyEvent.VK_D){
            dPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent arg0) {}
}
