package pro.rewindlabs.regex;

public class ShiftOr {
    private long[] table;
    private long finishMask;
    private int length;

    ShiftOr(long[] table, long finishMask, int length) {
        this.table = table;
        this.finishMask = finishMask;
        this.length = length;
    }

    public int search(CharSequence seq) {
        long state = -1;
        for (int i = 0; i <seq.length(); i++) {
            state <<= 1;
            state |= table[seq.charAt(i)];
            if ((finishMask & state) == 0) {
                return i - length + 1;
            }
        }
        return -1;
    }
}