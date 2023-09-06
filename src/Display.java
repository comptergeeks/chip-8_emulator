import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    int height;
    int width;
    int scale;
    Rectangle[][] pixels;
    int[][] board;
    public Display(int scale) {
        this.scale = scale;
        width = 64 * scale;
        height = 32 * scale;
        pixels = new Rectangle[64][32];
        board = new int[64][32];//height (rows), width (columns)
        /*
        logic: turn off and on pixels depending on the current value provided by
        DXYN, make the pixels rectangles depending on the scale and then turn them
        off through that
         */
        setBackground(Color.BLACK);
        //setSize(new Dimension(width, height));
        getPreferredSize();
        System.out.println(getSize());
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pixels[i][j] = new Rectangle(i * scale, j * scale, scale, scale);
                board[i][j] = 0;

                //System.out.println(board[i][j]);
            }
        }
        System.out.println(pixels[0].length);

    }
    public void draw(CPU cpu, short i, short[] v, int x, int y, int n) {
        int xPos;
        int yPos;
        xPos = v[x] % 64;
        yPos = v[y] % 32;
        v[0xF] = 0;

    }

    public void cls() {

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (board[i][j] == 1) {
                    Rectangle rect = pixels[i][j];
                    g.setColor(Color.green);
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return (new Dimension(width, height));
    }
}
