package pro.rewindlabs.benchmark;

import org.openjdk.jmh.annotations.*;

import pro.rewindlabs.regex.BitNFA;
import pro.rewindlabs.regex.BitNFABuilder;
import pro.rewindlabs.regex.BytecodeBuilder;
import pro.rewindlabs.regex.NativeThompson;
import pro.rewindlabs.regex.Opcode;
import pro.rewindlabs.regex.ShiftOr;
import pro.rewindlabs.regex.ShiftOrBuilder;

import java.util.Arrays;

@State(Scope.Thread)
@Fork(value = 1)
@Warmup(iterations = 3, time = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BenchSearch {
    String haystack = null;

    ShiftOr shiftOr;

    BitNFA bitNFA;

    @Setup
    public void setup() {
        char[] array = new char[1024*1024];
        Arrays.fill(array, 'a');
        array[array.length-1] = 'b';
        haystack = new String(array);
        shiftOr = ShiftOrBuilder.fromString("aaaaaaaaaaaaaaaaaab");
        var b = new BytecodeBuilder();
        var lbl = b.label();
        int[] code = b
            .bind(lbl).code(Opcode.CHAR, 'a')
            .code(Opcode.FORK, lbl)
            .code(Opcode.CHAR, 'b')
            .code(Opcode.END, 1)
            .build();
        bitNFA = BitNFABuilder.create(code);
    }

    @Benchmark
    public int search() {
        return shiftOr.search(haystack);
    }

    @Benchmark
    public int bitnfa() {
        return bitNFA.search(haystack);
    }

    @Benchmark
    public boolean nativeThompson() {
        return NativeThompson.search(haystack);
    }
}
