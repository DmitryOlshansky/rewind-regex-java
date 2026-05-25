package pro.rewindlabs.regex;

import org.junit.jupiter.api.Test;

import pro.rewindlabs.regex.SIHT;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SIHTTest {
    @Test
    void hashTable() {
        var siht = new SIHT(4);
        for (int i = 1; i <= 255; i += 2) {
            siht.put(i, i+1);
        }
        for (int i = 0; i <= 256; i++) {
            if ((i & 1) != 0) {
                assertEquals(i+1, siht.get(i));
            } else {
                assertEquals(-1, siht.get(i));
            }
        }
    }
}
