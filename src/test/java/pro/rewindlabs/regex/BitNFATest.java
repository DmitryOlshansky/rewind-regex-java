package pro.rewindlabs.regex;

import org.junit.jupiter.api.Test;

import pro.rewindlabs.regex.BitNFA;
import pro.rewindlabs.regex.BitNFABuilder;
import pro.rewindlabs.regex.Bytecode;
import pro.rewindlabs.regex.BytecodeBuilder;
import pro.rewindlabs.regex.Opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitNFATest {
    @Test
    void basic() {
        var b = new BytecodeBuilder();
        var label = b.label();
        int[] code = b
            .bind(label).code(Opcode.CHAR, 'a')
            .code(Opcode.FORK, label)
            .code(Opcode.CHAR, 'b')
            .code(Opcode.END, 1)
            .build();
        var bit = BitNFABuilder.create(code);
        assertEquals(3, bit.search("aab"));
        assertEquals(-1, bit.search("aaa"));
        assertEquals(-1, bit.search("b"));
        var labelA = b.label();
        var labelB = b.label();
        var labelC = b.label();
        code = new BytecodeBuilder()
            .bind(labelA).code(Opcode.CHAR, 'a')
            .code(Opcode.FORK, labelA)
            .bind(labelB).code(Opcode.CHAR, 'b')
            .code(Opcode.FORK, labelB)
            .bind(labelC).code(Opcode.CHAR, 'c')
            .code(Opcode.FORK, labelC)
            .code(Opcode.END, 1)
            .build();
        var bit2 = BitNFABuilder.create(code);
        assertEquals(3, bit2.search("abc"));
    }
}
