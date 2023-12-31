import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Memory {
    short[] arr;
    byte[] data;

    public Memory(short[] memory) {
        this.arr = memory;
    }
    public void setFont() {
        short[] font = {
                0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
                0x20, 0x60, 0x20, 0x20, 0x70, // 1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
                0x90, 0x90, 0xF0, 0x10, 0x10, // 4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
                0xF0, 0x10, 0x20, 0x40, 0x40, // 7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
                0xF0, 0x90, 0xF0, 0x90, 0x90, // A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
                0xF0, 0x80, 0x80, 0x80, 0xF0, // C
                0xE0, 0x90, 0x90, 0x90, 0xE0, // D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
                0xF0, 0x80, 0xF0, 0x80, 0x80  // F
        };
        int count = 0;
        for(int i = 0x50; i <= 0x9F; i++) {
            arr[i] = font[count];
            count++;
        }

    }
    public void loadROM() throws IOException {
        //load rom at 0x200 - and onwards
//        Path path = Paths.get("testcases/IBM Logo.ch8");
//        Path path = Paths.get("testcases/test_opcode2.ch8");
        Path path = Paths.get("testcases/breakout.rom");
//        Path path = Paths.get("testcases/pong.rom");
//        Path path = Paths.get("testcases/maze.rom");
//         Path path = Paths.get("testcases/tetris.rom");
//        Path path = Paths.get("testcases/6-keypad.ch8");
//        Path path = Paths.get("testcases/4-flags.ch8");
//        Path path = Paths.get("testcases/oob_test.ch8");
        data = Files.readAllBytes(path);
        for (int i = 0; i < data.length; i++) {
            arr[0x200 + i] = (data[i]);
        }
        System.out.println("ROM loaded successfully! ");
    }
}
