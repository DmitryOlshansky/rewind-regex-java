package pro.rewindlabs.regex;

import pro.rewindlabs.regex.SIHT;

public class BitNFA {
    private long[] table;
    private SIHT jumps;
    private long jumpMask;
    private long finishMask;

    BitNFA(long[] table, SIHT jumps, long jumpMask, long finishMask) {
        this.table = table;
        this.jumps = jumps;
        this.jumpMask = jumpMask;
        this.finishMask = finishMask;
    }

    public int search(CharSequence seq) {
        long state = -1;
        int len = seq.length();
        for (int idx = 0; idx < len; idx++) {
            state <<= 1;
            long m = state | jumpMask;
            state &= jumps.get(m);
            state |= table[seq.charAt(idx)];
            if ((finishMask & state) == 0) {
                return idx;
            }
        }
        state <<= 1;
        long m = state | jumpMask;
        state &= jumps.get(m);
        if ((finishMask & state) == 0) {
            return len;
        }
        return -1;
    }
}
