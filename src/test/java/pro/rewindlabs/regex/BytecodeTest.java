package pro.rewindlabs.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BytecodeTest {
    @Test
    void building() {
        var b = new BytecodeBuilder();
        var lbl = b.label();
        int[] code = b
            .code(Opcode.ANY, 0)
            .code(Opcode.JMP, lbl)
            .bind(lbl).code(Opcode.END, 42).build();
        assertEquals(Opcode.ANY, Opcode.instr(code[0]));
        assertEquals(0, Opcode.operand(code[0]));
        assertEquals(Opcode.JMP, Opcode.instr(code[1]));
        assertEquals(1, Opcode.operand(code[1]));
        assertEquals(Opcode.END, Opcode.instr(code[2]));
        assertEquals(42, Opcode.operand(code[2]));
    }

    @Test
    void decode() {
        var b = new BytecodeBuilder();
        var lbl = b.label();
        var lbl2 = b.label();
        int[] code = b
            .bind(lbl).code(Opcode.CHAR, 'a')
            .bind(lbl2).code(Opcode.CHAR, 'b')
            .code(Opcode.JMP, lbl2)
            .code(Opcode.FORK, lbl)
            .code(Opcode.END, 1).build();
        String output = """
            0\tCHAR 'a'
            1\tCHAR 'b'
            2\tJMP => 1
            3\tFORK => 0
            4\tEND 1

        """.replaceAll("\\s", "");
        assertEquals(output, Bytecode.decode(code).replaceAll("\\s", ""));
    }

    @Test
    void exception() {
        var b = new BytecodeBuilder();
        var lbl = b.label();
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> {
                b
                    .code(Opcode.CHAR, 'A')
                    .code(Opcode.FORK, lbl).build();
            }
        );
        assertEquals("unbound label at offset="+Integer.toString(1), ex.getMessage());
    }
}
