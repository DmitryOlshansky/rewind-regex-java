package pro.rewindlabs.regex;

import org.junit.jupiter.api.Test;
import pro.rewindlabs.regex.ShiftOrBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShiftOrTest {

    @Test
    public void basicStrings() {
        var abc = ShiftOrBuilder.fromString("abc");
        assertEquals(0, abc.search("abc"));
        assertEquals(1, abc.search("aabc"));
        assertEquals(1, abc.search("aabcd"));
        assertEquals(-1, abc.search("abd"));
        assertEquals(-1, abc.search("abdc"));
    }

    @Test
    public void charClasses() {
        var builder = new ShiftOrBuilder();
        var cc = builder
            .add(0, 'a', 'z')
            .add(0, 'A', 'Z')
            .add(1, '0', '9')
            .end(2).build();
        assertEquals(0, cc.search("b1"));
        assertEquals(0, cc.search("z1"));
        assertEquals(0, cc.search("Z9"));
        assertEquals(-1, cc.search("19"));
        var second = builder.add(0, '_', '_').end(1).build();
        assertEquals(0, second.search("_"));
        assertEquals(-1, second.search("A"));

    }
}
