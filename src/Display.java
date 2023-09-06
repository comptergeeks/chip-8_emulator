import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    int height;
    int width;
    int scale;
    Rectangle[][] board;
    public Display(int scale) {
        this.scale = scale;
        width = 64 * scale;
        height = 64 * scale;
        board = new Rectangle[32][64]; //height (rows), width (columns)
        /*
        logic: turn off and on pixels depending on the current value provided by
        DXYN, make the pixels rectangles depending on the scale and then turn them
        off through that
         */
        setBackground(Color.BLACK);
        setSize(new Dimension(width, height));
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Rectangle(i * scale, j * scale, scale, scale);
            }
        }

    }
    public void draw(CPU cpu, short i, short[] v, int x, int y, int n) {
        int xPos;
        int yPos;
        xPos = v[x] & 63;
        yPos = v[y] & 31;
        v[0xf] = 0;
        for(int m = 0; m < n; m++) {
           short rowData = cpu.memoryArr[i + m];
           //for (int c = rawData;  )

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Rectangle rect = board[i][j];
                g.setColor(Color.blue);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
        super.paintComponent(g);
    }
}
