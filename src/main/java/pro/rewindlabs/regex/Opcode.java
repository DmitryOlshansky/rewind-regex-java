package pro.rewindlabs.regex;

public class Opcode {
    public static final int 
    ANY = 0,
    CHAR = 1,
    NOTCHAR = 2,
    ONE_OF = 3,
    NOT_ONE_OF = 4,
    INTERVALS = 5,
    BIT = 6,
    TRIE = 7,
    MARK = 8,

    JMP = 9,
    FORK = 10,

    END = 11,

    MERGE_POINT = 0x80;

    public static int operand(int code) { return code & 0xFFFFFF; }
    public static int instr(int code) { return ((code >>> 24) & 0x7F); }
    public static boolean isMergePoint(int code) { return (code & 0x80000000) != 0; }
}

