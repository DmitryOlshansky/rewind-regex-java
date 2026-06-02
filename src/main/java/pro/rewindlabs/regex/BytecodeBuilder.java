package pro.rewindlabs.regex;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

import pro.rewindlabs.regex.Opcode;

public class BytecodeBuilder {
    private int[] data;
    private int ofs;
    private HashMap<Integer, Label> fixups;

    public static class Label {
        private int offset;

        Label(int ofs) {
            offset = ofs;
        }
    }

    private void check(boolean cond, String message) {
        if (!cond) throw new IllegalArgumentException(message);
    }

    public BytecodeBuilder() {
        data = new int[8];
        fixups = new HashMap<>();
    }

    private void emit(int code) {
        if (ofs == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        data[ofs++] = code;
    }

    public int offset() { return ofs; }

    public BytecodeBuilder code(int opcode, int operand) {
        check(operand < (1<<24), "operand is greater that 24 bits");
        emit((opcode << 24) | operand);
        return this;
    }

    public BytecodeBuilder raw(int data) {
        emit(data);
        return this;
    }

    public BytecodeBuilder code(int opcode, Label lbl) {
        check(opcode == Opcode.JMP || opcode == Opcode.FORK, "cannot use labels with opcode=" + Integer.toString(opcode));
        fixups.put(ofs, lbl);
        emit((opcode << 24) | 0xFFFFFF);
        return this;
    }

    public BytecodeBuilder bind(Label lbl) {
        lbl.offset = ofs;
        return this;
    }

    public Label label() {
        return new Label(-1);
    }

    public int[] build() {
        for (var kv  : fixups.entrySet()) {
            int op = Opcode.instr(data[kv.getKey()]);
            check(op == Opcode.JMP || op == Opcode.FORK, "fixup must point to jmp or fork instr");
            check(kv.getValue().offset != -1, "unbound label at offset=" + Integer.toString(kv.getKey()));
            data[kv.getKey()] = (op << 24) | (kv.getValue().offset - kv.getKey()) & 0xFFFFFF;
        }
        return Arrays.copyOf(data, ofs);
    }
}
