package pro.rewindlabs.regex;

import pro.rewindlabs.regex.Opcode;

public class Bytecode {
    public static String decode(int[] code) {
        var sb = new StringBuilder();
        for (int pc = 0; pc < code.length; ) {
            int opcode = Opcode.instr(code[pc]);
            int operand = Opcode.operand(code[pc]);
            switch (opcode) {
                case Opcode.ANY:
                    sb.append(String.format("%d\tANY\n", pc));
                    pc++;
                    break;
                case Opcode.CHAR:
                    sb.append(String.format("%d\tCHAR '%s'\n", pc, Character.valueOf((char)operand)));
                    pc++;
                    break;
                case Opcode.NOTCHAR:
                    sb.append(String.format("%d\tNOTCHAR '%s'\n", pc, Character.valueOf((char)operand)));
                    pc++;
                    break;
                case Opcode.ONE_OF:
                    sb.append(String.format("%d\tONE_OF", pc));
                    for (int i = 0; i < operand; i++) {
                        sb.append(String.format(" '%s'", Character.valueOf((char)code[pc + 1 + i])));
                    }
                    sb.append("\n");
                    pc += 1 + operand;
                    break;
                case Opcode.NOT_ONE_OF:
                    sb.append(String.format("%d\tNOT_ONE_OF", pc));
                    for (int i = 0; i < operand; i++) {
                        sb.append(String.format(" '%s'", Character.valueOf((char)code[pc + 1 + i])));
                    }
                    sb.append("\n");
                    pc += 1 + operand;
                    break;
                case Opcode.INTERVALS:
                    sb.append(String.format("%d\tINTERVALS ", pc));
                    for (int i = 0; i < operand; i++) {
                        char first = Character.valueOf((char)code[pc + 1 + 2*i]);
                        char last = Character.valueOf((char)code[pc + 2 + 2*i]);
                        sb.append(String.format(" '%s'..'%s'", first, last));
                    }
                    sb.append("\n");
                    pc += 1 + 2*operand;
                    break;
                case Opcode.BIT:
                    sb.append(String.format("%d\tBIT ", pc));
                    for (int i = 0; i < 4; i++) {
                        int range = code[pc + 1 + i];
                        sb.append(String.format("%2x", range));
                    }
                    sb.append("\n");
                    pc += 5;
                    break;
                case Opcode.TRIE:
                    sb.append(String.format("%d\tTRIE %d\n", pc, operand));
                    pc++;
                    break;
                case Opcode.MARK:
                    sb.append(String.format("%d\tMARK %d\n", pc, operand));
                    pc++;
                    break;
                case Opcode.JMP:
                    sb.append(String.format("%d\tJMP => %d\n", pc, (pc + operand) & 0xFFFFFF));
                    pc++;
                    break;
                case Opcode.FORK:
                    sb.append(String.format("%d\tFORK => %d\n", pc, (pc + operand) & 0xFFFFFF));
                    pc++;
                    break;
                case Opcode.END:
                    sb.append(String.format("%d\tEND %d", pc, operand));
                    pc++;
                    break;
            }
        }
        return sb.toString();
    }
}
