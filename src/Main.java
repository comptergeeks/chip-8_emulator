import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        int scale = 8;
        Display dis = new Display(scale);
        SwingUtilities.invokeLater(() -> {
            JFrame displayFrame = new JFrame();
            //displayFrame.setSize(64 * scale, 32 * scale);
            displayFrame.getContentPane().add(dis);
            displayFrame.setVisible(true);
            displayFrame.setResizable(false);
            displayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            //displayFrame.setResizable(false);
            displayFrame.pack();
                }
        );
        CPU cpu = new CPU(dis);
    }
}