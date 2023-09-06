import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    int height;
    int width;
    int scale;
    Rectangle[][] pixels;
    public Display(int scale) {
        this.scale = scale;
        width = 64 * scale;
        height = 32 * scale;
        pixels = new Rectangle[64][32]; //height (rows), width (columns)
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
                //System.out.println(board[i][j]);
            }
        }
        System.out.println(pixels[0].length);

    }
    public void draw(CPU cpu, short i, short[] v, int x, int y, int n) {
        int xPos;
        int yPos;
        xPos = v[x] & 63;
        yPos = v[y] & 31;
        v[0xf] = 0;
        for(int m = 0; m < n; m++) {
           short rowData = cpu.memoryArr[i + m];
           for (int c = rowData; c< 8; c++) {
               if (pixels[m][c].)
           }

        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                Rectangle rect = pixels[i][j];
                g.setColor(Color.blue);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);

                //g.drawString(i + "," + j + " ", rect.x + 4, rect.y - 4);
                //g.setFont(new Font("TimesRoman", Font.PLAIN, 7));
                //System.out.println(rect);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return (new Dimension(width, height));
    }
}
