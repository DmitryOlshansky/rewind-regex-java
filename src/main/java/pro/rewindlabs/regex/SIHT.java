package pro.rewindlabs.regex;

import java.util.Arrays;

public class SIHT {
    private long[] keys;
    private long[] values;
    private int mask, items, pow2;
    
    private static final long mul = 0x9E3779B97F4A7C15L;

    private static boolean isPow2(long size) {
        return (size & (size-1)) == 0;
    }

    private static long hash(long x, int n) {
        return (x * mul) >>> (64 - n);
    }

    public SIHT(int size) {
        assert isPow2(size);
        keys = new long[size];
        values = new long[size];
        Arrays.fill(keys, -1);
        Arrays.fill(values, -1);
        items = 0;
        mask = size - 1;
        pow2 = Integer.numberOfTrailingZeros(size);
    }

    public void put(long key, long value) {
        if (2 * items >= keys.length) rehash();
        int h = (int)hash(key, pow2);
        for (;;) {
            if (keys[h] == -1) {
                keys[h] = key;
                values[h] = value;
                items++;
                return;
            }
            h = (h + 1) & mask;
        }
    }

    public long get(long key) {
        int h = (int)hash(key, pow2);
        for (;;) {
            if (keys[h] == key) {
                return values[h];
            }
            if (keys[h] == -1) {
                return -1;
            }
            h = (h + 1) & mask;
        }
    }

    private void rehash() {
        var siht = new SIHT(keys.length*2);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != -1) {
                siht.put(keys[i], values[i]);
            }
        }
        this.keys = siht.keys;
        this.values = siht.values;
        this.mask = siht.mask;
        this.items = siht.items;
        this.pow2 = siht.pow2;
    }
}
