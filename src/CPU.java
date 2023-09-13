import java.io.IOException;
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
    public CPU(Display dis) throws IOException {
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

    public void fetch() {

        while ( programCounter < 4096 ) {
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
            switch (nibble) {
                case 0x00: {
                    switch (nn) { // (short) (nn & 0x00FF) - in case this breaks later down the line
                        case 0xE0: {
                            display.cls();
                            System.out.print("clear");
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
                } case 0x2: {
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
                            short operation = (short) ((v[x] | v[y]) & 0xFF); ;
                            v[x] = operation;
                            break;
                        }
                        case 0x2: {
                            //and
                            short operation = (short) ((v[x] & v[y]) & 0xFF); ;
                            v[x] = operation;
                            break;
                        }
                        case 0x3: {
                            //xor
                            short operation = (short) ((v[x] ^ v[y]) & 0xFF); ;
                            v[x] = operation;
                            break;
                        }
                        case 0x4: {
                            //add 0 with overflow, if the result is > 0xFF v[0xF] = 1; else v[0xF] = 0;
                            short operation = (short) ((v[x] + v[y]) & 0xFF);
                            v[x] = operation;
                            if (operation > 0xFF) {
                                v[0xF] = 1;
                            } else {
                                v[0xF] = 0;
                            }
                            break;
                        }
                        case 0x5: {
                            //grouped with 0x7 for subtraction 8XY5 =  v[x] = v[x] - v[y];
                            //if the first (minuend) > than second ) subtrahend - v[0xF] = 1;
                            //else v[0xF] = 0;
                            //make a function to affect carry flag
                            short operation = (short) ((v[x] - v[y]) & 0xFF);
                            v[x] = operation;
                            v[0xF] = subtractionFlag(v[x], v[y], v[0xF], true);
                            break;
                        }
                        case 0x6: {
                            //shift left (E) or right one (6)
                            //v[x] = (short) (v[y] & 0xFF);
                            //get the first bit value so maybe mask off with 0b1 - get the value of the first digit and then right shift one
                            //get x & 1 - if 1 then vf = 1 else vf = 0;
                            //divide v[x]/2
                            /*
                            short temp = (short) (v[x] & 0xFF);
                            short operation = (short) ((v[y] >> 1)& 0xFF);
                            v[x] = operation;
                            if ((temp & 1) == 1) {
                                v[0xF] = 1;
                            } else {
                                v[0xF] = 0;
                            }
                            break;
                             */
                            short temp = (short) (v[x] & 0xFF);
                            short operation = (short) ((v[x] >> 1)& 0xFF);
                            v[x] = operation;
                            if ((temp & 1) == 1) {
                                v[0xF] = 1;
                            } else {
                                v[0xF] = 0;
                            }
                            break;
                        }
                        case 0x7: {
                            //not sure if the subtraction flag method works for 0x7 due to corax test not implemented, could cause an issue later
                            short operation = (short) ((v[y] - v[x]) & 0xFF);
                            v[x] = operation;
                            v[0xF] = subtractionFlag(v[x], v[y], v[0xF], false);
                            break;
                        }
                        case 0xE: {
                            short temp = (short) (v[x] & 0xFF);
                            short operation = (short) ((v[x] << 1)& 0xFF);
                            v[x] = operation;
                            if ((temp & 1) == 1) {
                                v[0xF] = 1;
                            } else {
                                v[0xF] = 0;
                            }
                            break;
                        }
                    }
                    break;
                }
                //arithmetic operations are determined by n value - so it's a nested which first checks the nibble and then the N value
                //using nested switch case, just like the clear / subroutine instruction
                default: {
                    System.out.println("no opcode found " + Integer.toHexString(instruction) );
                }
            }
        //System.out.println();
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
    public short subtractionFlag(short vx, short vy, short vf, boolean sub) {
        //set vF equal to the value of the final flag
        if (vx > vy && sub) {
            vf = 1;
        } else if (sub){
            vf = 0;
        }
        if (vy > vx && !sub) {
            vf = 1;
        } else if (!sub){
            vf = 0;
        }
        return vf;
    }
}