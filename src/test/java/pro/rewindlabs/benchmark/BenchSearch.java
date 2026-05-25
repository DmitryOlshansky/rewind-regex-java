package pro.rewindlabs.benchmark;

import org.openjdk.jmh.annotations.*;
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

    @Setup
    public void setup() {
        char[] array = new char[1024*1024];
        Arrays.fill(array, 'a');
        array[array.length-1] = 'b';
        haystack = new String(array);
        shiftOr = ShiftOrBuilder.fromString("aaaaaaaaaaaaaaaaaab");
    }

    @Benchmark
    public int search() {
        return shiftOr.search(haystack);
    }

}
