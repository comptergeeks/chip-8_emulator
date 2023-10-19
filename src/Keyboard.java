import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {
    short keyValue;
    boolean keyPressed = false;
    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        decodeKey(e);

    }
    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println(keyValue);
        keyPressed = true;
        keyValue = 0;
    }
    public void decodeKey(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_1) {
                keyValue = 0x1;
            }
            if (e.getKeyCode() == KeyEvent.VK_2) {
                keyValue = 0x2;
            }
            if (e.getKeyCode() == KeyEvent.VK_3) {
                keyValue = 0x3;
            }
            if (e.getKeyCode() == KeyEvent.VK_4) {
                keyValue = 0xC;
            }
            if (e.getKeyCode() == KeyEvent.VK_Q) {
                keyValue = 0x4;
            }
            if (e.getKeyCode() == KeyEvent.VK_W) {
                keyValue = 0x5;
            }
            if (e.getKeyCode() == KeyEvent.VK_E) {
                keyValue = 0x6;
            }
            if (e.getKeyCode() == KeyEvent.VK_R) {
                keyValue = 0xD;
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                keyValue = 0x7;
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                keyValue = 0x8;
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                keyValue = 0x9;
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
                keyValue = 0xE;
            }
            if (e.getKeyCode() == KeyEvent.VK_Z) {
                keyValue = 0xA;
            }
            if (e.getKeyCode() == KeyEvent.VK_X) {
                keyValue = 0x0;
            }
            if (e.getKeyCode() == KeyEvent.VK_C) {
                keyValue = 0xB;
            }
            if (e.getKeyCode() == KeyEvent.VK_V) {
                keyValue = 0xF;
            }
    }
}
