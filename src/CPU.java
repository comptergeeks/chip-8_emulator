import java.io.IOException;

public class CPU {
    short[] memoryArr; /*memory array, the chip - 8 has 4 kb of memory, for use the only important locations start at
    0x200 (512) - 0xFFF (4095) - this is the space relevant for me as the first 512 are where the actual interpreter
    was located
    */
    short programCounter; //moves up by two with each instruction
    short i; //points at locations in memory
    short[] v;
    Memory memory;
    Display display;
    short[] stack;
    // for the font I store then within an array and then set that to each location in memory 050 - 09F (80, 159)
    // Stack s = new Stack(); //stack used for 2 byte addresses, which calls subroutines (functions) and then returns from them - I am assuming I use this with the swtich instead of an array
    public CPU(Display dis) throws IOException {
        // add display into here
        this. display = dis;
        memoryArr = new short[4096];
        programCounter = 0x200;
        v = new short[16];
        stack = new short[16];
        memory = new Memory(memoryArr);
        memory.setFont();
        memory.loadROM();
        fetch();

    }

    public void fetch() {
        while (programCounter < 4096) {
            // combine each value into a short, and then pass to decode opcode
            //move variable assignment to concatenation
            short s1 = (short) (memoryArr[programCounter]);
            short s2 = (short) (memoryArr[programCounter + 1]);
            // left shift 8
            int s3 = (int) (s2 & 0xFF) | ((s1 & 0xFF) << 8); //concatenate

            programCounter += 2;

            decoder(s3);
        }
    }
    public void decoder(int instruction) {
        //according to guide steps are to extract nibbles first, and then decode based on that
        //nibble1 first four bits so mask off first four
        int nibble = instruction >> 12 & 0xFF;
        //x second 4 bits
        int x = instruction >> 8 & 0x0F;
        //y third 4 bits
        int y = instruction >> 4 & 0x00F;
        //n forth 4 bits
        int n = instruction& 0x000F;
        short nn = (short) (instruction & 0x00FF);
        short nnn = (short) (instruction & 0x0FFF);
        //printOpcodes(nibble, x, y, n, nn, nnn, false, false);
        //still not sure when to mask off when performing operations
        switch (nibble) {
            case 0x00: {
                //System.out.println("clear");
                //set all rects to black - 0

                break;
            }
            case 0x1: {
                programCounter = nnn;
                //System.out.println("jump");
                break;
            }
            case 0x6: {
                v[x] = nn;
                //System.out.println("set register vx");
                break;
            }
            case 0x7: {
                v[x] += nn;
                //apparently not supposed to overflow, can handle the issue later
                //System.out.println("add value to register vx");
            }
            case 0xA: {
                i = nnn;
                //System.out.println("set index register i");
            }
            case 0xD: {
                System.out.println("draw");
                display.draw(this, i , v, x, y, n);
                display.repaint();
            }
        }

    }
    public void printOpcodes(int nibble, int x, int y, int n, short nn, short nnn, boolean printNN, boolean printNNN) {
        //cleaner printing of opcodes to avoid clutter in the decoder, and since this will only be used for testing
        if (!printNN && !printNNN) {
            System.out.print(Integer.toHexString(nibble));
            System.out.print(Integer.toHexString(x));
            System.out.print(Integer.toHexString(y));
            System.out.print(Integer.toHexString(n));
        }
        if (printNN) {
            System.out.print(Integer.toHexString(nn));
        }
        if (printNNN) {
            System.out.print(Integer.toHexString(nnn));
        }
        System.out.println();
    }
}