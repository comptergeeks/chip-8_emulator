import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            JFrame displayFrame = new JFrame();
            Display dis = new Display();
            displayFrame.setSize(640, 320);
            displayFrame.getContentPane().add(dis);
            displayFrame.setVisible(true);
            displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
        );
        CPU cpu = new CPU();
    }
}