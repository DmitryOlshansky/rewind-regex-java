package pro.rewindlabs.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeThompsonTest {
    @Test
    void predefined() {
        assertEquals(true, NativeThompson.search("aaaaaaaaaaaaaaab"));
        //assertEquals(true, NativeThompson.search("ab"));
        //assertEquals(false, NativeThompson.search("aa"));
    }    
}
