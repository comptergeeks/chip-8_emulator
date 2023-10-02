import java.io.IOException;
import java.util.Random;
import java.util.Stack;

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

    Stack stack;

    // for the font I store then within an array and then set that to each location in memory 050 - 09F (80, 159)
    // Stack s = new Stack(); //stack used for 2 byte addresses, which calls subroutines (functions) and then returns from them - I am assuming I use this with the swtich instead of an array
    public CPU(Display dis) throws IOException, InterruptedException {
        // add display into here
        this.display = dis;
        memoryArr = new short[4096];
        programCounter = 0x200;
        v = new short[16];
        stack = new Stack<Short>();
        memory = new Memory(memoryArr);
        memory.setFont();
        memory.loadROM();
        fetch();

    }

    public void fetch() throws InterruptedException {

        while (programCounter < 4096) {
            Thread.sleep(1);
            // timing would go here - need to increment a timer based on the amount instructions ran
            // combine each value into a short, and then pass to decode opcode
            //move variable assignment to concatenation
            short s1 = (short) (memoryArr[programCounter++]);
            short s2 = (short) (memoryArr[programCounter++]);
            // left shift 8
            int s3 = (int) (s2 & 0xFF) | ((s1 & 0xFF) << 8); //concatenate
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
        int n = instruction & 0x000F;
        short nn = (short) (instruction & 0x00FF);
        short nnn = (short) (instruction & 0x0FFF);
        //printOpcodes(nibble, x, y, n, nn, nnn, false, false);
        switch (nibble) {
            case 0x00: {
                switch (nn) { // (short) (nn & 0x00FF) - in case this breaks later down the line
                    case 0xE0: {
                        display.cls();
                        break;
                    }
                    case 0xEE: {
                        programCounter = (short) stack.pop();
                        //call subroutine, make function
                        break;
                    }
                }
                break;
            }
            case 0x1: {
                programCounter = nnn; //(short) (nnn & 0x0FFF)
                //System.out.print("jump");
                break;
            }
            case 0x6: {
                v[x] = nn; //(short) (nn & 0x00FF)
                //System.out.print("set register vx");
                break;
            }
            case 0x7: {
                short value = (short) ((v[x] + nn) & 0x00FF);
                v[x] = value;
                //System.out.print("add value to register vx");
                break;
            }
            case 0xA: {
                i = nnn; //(short) (nnn & 0xFFF)
                //System.out.print("set index register i");
                break;
            }
            case 0xD: {
                display.draw(this, i, v, x, y, n);
                // System.out.print("draw");
                display.repaint();
                break;
            }
            case 0x2: {
                //call subroutine/function at memory location NNN - set program counter to NNN
                //steps 1: push current pc to stack, so subroutine can return to it later
                //step 2: 00EE returns from subroutine - pop the last address from stack and set program counter to it.
                stack.push(programCounter);
                programCounter = nnn;
                break;
            }
            case 0x3: {
                //grouped with 0x4XNN, 0x5XY0, and 0x9XY0 "Skip"
                //skip one instruction if v[x] == nn
                if (v[x] == nn) {
                    programCounter += 2;
                }
                break;
            }
            case 0x4: {
                //skip one instruction if v[x] != nn
                if (v[x] != nn) {
                    programCounter += 2;
                }
                break;
            }
            case 0x5: {
                if (v[x] == v[y]) {
                    programCounter += 2;
                }
                break;
            }
            case 0x9: {
                if (v[x] != v[y]) {
                    programCounter += 2;
                }
                break;
            }
            case 0x8: {
                //maybe create method to pass in variables and then perform operation based on that -- later after completed
                switch (n) {
                    case 0x0: {
                        v[x] = v[y];
                        break;
                    }
                    case 0x1: {
                        //or
                        short operation = (short) ((v[x] | v[y]) & 0xFF);
                        ;
                        v[x] = operation;
                        break;
                    }
                    case 0x2: {
                        //and
                        short operation = (short) ((v[x] & v[y]) & 0xFF);
                        v[x] = operation;
                        break;
                    }
                    case 0x3: {
                        //xor
                        short operation = (short) ((v[x] ^ v[y]) & 0xFF);
                        ;
                        v[x] = operation;
                        break;
                    }
                    case 0x4: {
                        //add 0 with overflow, if the result is > 0xFF v[0xF] = 1; else v[0xF] = 0;
                        short flagV;
                        if (v[x] + v[y] > 0xFF) {
                            flagV = 1;
                        } else {
                            flagV = 0;
                        }
                        short operation = (short) ((v[x] + v[y]) & 0xFF);
                        v[x] = operation;
                        v[0xF] = flagV;
                        break;
                    }
                    case 0x5: {
                        //grouped with 0x7 for subtraction 8XY5 =  v[x] = v[x] - v[y];
                        //if the first (minuend) > than second ) subtrahend - v[0xF] = 1;
                        //else v[0xF] = 0;
                        //make a function to affect carry flag
//                            short flagV = subtractionFlag(v[x], v[y], v[0xF], true);
                        short flagV = 0;
                        if (v[x] >= v[y]) flagV = 1;
                        short operation = (short) ((v[x] - v[y]) & 0xFF);
                        v[x] = operation;
                        v[0xF] = flagV;
                        break;
                    }
                    case 0x6: {
                        short temp = (short) (v[x] & 0xFF);
                        short operation = (short) ((v[x] >> 1) & 0xFF);
                        v[x] = operation;
                        if ((temp & 1) == 1) {
                            v[0xF] = 1;
                        } else {
                            v[0xF] = 0;
                        }
                        break;
                    }
                    case 0x7: {
                        //short flagV = subtractionFlag(v[x], v[y], v[0xF], false);
                        short flagV = 0;
                        if (v[y] >= v[x]) flagV = 1;
                        short operation = (short) ((v[y] - v[x]) & 0xFF);
                        v[x] = operation;
                        v[0xF] = flagV;
                        break;
                    }
                    case 0xE: {
                        short temp = (short) (v[x] & 0xFF);
                        short flagV;
                        if ((temp >> 7) == 1) {
                            flagV = 1;
                        } else {
                            flagV = 0;
                        }
                        short operation = (short) ((v[x] << 1) & 0xFF);
                        v[x] = operation;
                        v[0xF] = flagV;

                        break;
                    }
                }
                break;
            }
            //arithmetic operations are determined by n value - so it's a nested which first checks the nibble and then the N value
            //using nested switch case, just like the clear / subroutine instruction
            case 0xB: {
                programCounter = (short) ((nn + v[0]) & 0xFF);
                break;
            }
            case 0xC: {
                Random rand = new Random();
                short random = (short) (rand.nextInt(255));
                short operation = (short) ((random & nn) & 0xFF);
                v[x] = operation;
            }
            case 0xF: {
                switch (nn) {
                    case 0x1E: {
                        i = (short) (i + v[x]);
                        break;
                    }
                    case 0x29: {
                        i = (short) ((v[x]*5) & 0x00FF);
                        break;
                    }
                    case 0x33: {
                        //can shorten with for loop
                        short num = v[x];
                        short ones = (short) (num % 10);
                        num /= 10;
                        short tens = (short) (num % 10);
                        num /= 10;
                        short hundreds = num;
                        memoryArr[i] = hundreds;
                        memoryArr[i + 1] = tens;
                        memoryArr[i + 2] = ones;
                        break;
                    }
                    case 0x55: {
                        for (int z = 0; z <= x; z++) {
                            memoryArr[i + z] = (short) (v[z] & 0xFF);
                        }
                        break;
                    }
                    case 0x65: {
                        for (int z = 0; z <= x; z++) {
                            v[z] = (short) (memoryArr[i + z] & 0xFF);
                        }
                        break;
                    }
                }
                break;
            }
            case 0xE: {
                break;
            }
            default: {
                System.out.println("no opcode found " + Integer.toHexString(instruction));
            }
        }
        //System.out.println();
    }

    public void printOpcodes(int nibble, int x, int y, int n, short nn, short nnn, boolean printNN, boolean printNNN) {
        //cleaner printing of opcodes to avoid clutter in the decoder, and since this will only be used for testing
        //code maybe be refactored later into GUI element and send values over to display later.
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
    public void receiveInput() {

    }
}