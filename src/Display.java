import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Display extends JPanel{
    int height;
    int width;
    int arrHeight, arrWidth;
    int scale;
    Rectangle[][] pixels;
    int[][] board;
    Keyboard k = new Keyboard();

    public Display(int scale) {
        arrHeight = 32;
        arrWidth = 64;
        this.scale = scale;
        width = 64 * scale;
        height = 32 * scale;
        pixels = new Rectangle[arrHeight][arrWidth];
        board = new int[arrHeight][arrWidth];//height (rows), width (columns)
        /*
        logic: turn off and on pixels depending on the current value provided by
        DXYN, make the pixels rectangles depending on the scale and then turn them
        off through that
         */
        //add seperate class that calls check for input and call that in cpu
        addKeyListener(k);;
        setFocusable(true);
        setBackground(Color.BLACK);
        //add keyboard class here

        getPreferredSize();
        System.out.println(getSize());
        //create pixels and set all values to false
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < arrWidth; j++) {
                pixels[i][j] = new Rectangle(j * scale, i * scale, scale, scale);
                board[i][j] = 0;
            }
        }
    }

    public void draw(CPU cpu, short i, short[] v, int x, int y, int n) {
        v[0xF] = 0;
        int xPos = v[x] % 64;
        int yPos = v[y] % 32;

        //add comments for understanding
        for (int row = 0; row < n; row++) {
            short rowData = (short) (cpu.memoryArr[i + row] & 0xFF);
            for (int bits = 0; bits < 8; bits++) {
                if (xPos + bits < arrWidth && yPos + row < arrHeight) {
                    int currentPixel = board[yPos + row][xPos + bits];
                    if (currentPixel == 1 && ((rowData >> 7 - bits) & 1) == 1) {
                        v[0xF] = 1;
                        board[yPos + row][xPos + bits] = 0;
                    }
                    if (currentPixel == 0 && ((rowData >> 7 - bits) & 1) == 1) {
                        board[yPos + row][xPos + bits] = 1;
                    }
                }
            }
        }
        repaint();
    }
    public void cls() {
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < arrWidth; j++) {
                board[i][j] = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < arrWidth; j++) {
                if (board[i][j] == 1) {
                    Rectangle rect = pixels[i][j];
                    g.setColor(Color.green);
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
                if (board[i][j] == 0) {
                    Rectangle rect = pixels[i][j];
                    g.setColor(Color.black);
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return (new Dimension(width, height));
    }

}
