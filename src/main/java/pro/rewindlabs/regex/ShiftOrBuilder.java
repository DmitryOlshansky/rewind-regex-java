package pro.rewindlabs.regex;

import java.util.Arrays;

public class ShiftOrBuilder {
    long[] table;
    long finishMask;
    int length;
    boolean started;

    public ShiftOrBuilder() {
        started = false;
    }

    public static ShiftOr fromString(String pattern) {
        var builder = new ShiftOrBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            builder.add(i, pattern.charAt(i), pattern.charAt(i));
        }
        builder.end(pattern.length());
        return builder.build();
    }

    void reset() {
        table = new long[1<<16];
        Arrays.fill(table, -1);
        finishMask = -1;
        length = 0;
        started = true;
    }

    public ShiftOrBuilder add(int pos, char start, char end) {
        if (!started) reset();
        for (char i = start; i <= end; i++) {
            table[i] &= ~(1L << pos);
        }
        return this;
    }

    public ShiftOrBuilder end(int length) {
        if (!started) reset();
        finishMask = 1L<<(length-1);
        this.length = length;
        return this;
    }

    public ShiftOr build() {
        var ret = new ShiftOr(table, finishMask, length);
        started = false;
        return ret;
    }
}
