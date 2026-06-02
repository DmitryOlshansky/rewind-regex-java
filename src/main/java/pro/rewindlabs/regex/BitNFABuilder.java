package pro.rewindlabs.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pro.rewindlabs.regex.BitNFA;
import pro.rewindlabs.regex.SIHT;

public class BitNFABuilder {
    //outputs & state
    long[] table;
    SIHT jumps;
    long jumpMask;
    long finishMask;
    // internal state
    ArrayList<ArrayList<Integer>> jumpTargets;

    private BitNFABuilder() {
        jumpTargets = new ArrayList<>();
        for (int i = 0; i < 64; i++)
            jumpTargets.add(null);
        finishMask = 0;
        jumpMask = -1;
        table = new long[1<<16];
        Arrays.fill(table, -1);
        jumps = new SIHT(16);
    }

    private void add(int index, char start, char end) {
        for (int i = start; i <= end; i++) {
            table[i] &= ~(1L<<index);
        }
    }

    private void jumpTarget(int from, int to) {
        if (jumpTargets.get(from) == null) {
            jumpTargets.set(from, new ArrayList<>());
        }
        jumpTargets.get(from).add(to);
    }

    private void end(int index) {
        finishMask = 1L << index;
    }

    private static class E {
        long jumpMask;
        long jumpTargetMask;
        public E(long jumpMask, long jumpTargetMask) {
            this.jumpMask = jumpMask;
            this.jumpTargetMask = jumpTargetMask;
        }
    }

    private static void collectJumpTargets(int[] code, int i, HashMap<Integer, Boolean> targets) {
        var op = Opcode.instr(code[i]);
        var arg = Opcode.operand(code[i]);
        if (op == Opcode.JMP) {
            targets.put((i + arg) & 0xFF_FFFF, true);
            collectJumpTargets(code, (i + arg) & 0xFF_FFFF, targets);
        } else if (op == Opcode.FORK) {
            targets.put(i + 1, true);
            targets.put((i + arg) & 0xFF_FFFF, true);
            collectJumpTargets(code, i + 1, targets);
            collectJumpTargets(code, (i + arg) & 0xFF_FFFF, targets);
        }
    }

    public static BitNFA create(int[] code) {
        return new BitNFABuilder().build(code);
    }

    private BitNFA build(int[] code) {
        for (int i = 0; i < code.length; i++) {
            int op = Opcode.instr(code[i]);
            int arg = Opcode.operand(code[i]);
            switch(op) {
                case Opcode.JMP:
                case Opcode.FORK:
                    var jumps = new HashMap<Integer, Boolean>();
                    collectJumpTargets(code, i, jumps);
                    for (var kv : jumps.entrySet()) {
                        jumpTarget(i, kv.getKey());
                    }
                    break;
                case Opcode.END:
                    add(i, (char)0, (char)0xFFFF);
                    end(i);
                    break;
                case Opcode.MARK:
                    throw new IllegalArgumentException("must trim zero-width arguments");
                case Opcode.ANY:
                    add(i, (char)0, (char)0xFFFF);
                    break;
                case Opcode.CHAR:
                    add(i, (char)arg, (char)arg);
                    break;
                case Opcode.NOTCHAR:
                case Opcode.ONE_OF:
                case Opcode.NOT_ONE_OF:
                case Opcode.INTERVALS:
                case Opcode.BIT:
                case Opcode.TRIE:
                    throw new IllegalStateException("this is not yet supported bytecode for bitnfa");
                default:
                    throw new IllegalStateException("unsupported bytecode detected");
            }
        }
        
        ArrayList<E> entries = new ArrayList<>();
        for(int i = 0; i < jumpTargets.size(); i++) {
            var jts = jumpTargets.get(i);
            if (jts != null) {
                jumpMask &= ~(1L<<i); // cumulative mask
                long jumpTargetMask = -1; // this offset target mask
                for (var j : jts) {
                    jumpTargetMask &= ~(1L<<j);
                }
                entries.add(new E(~(1L<<i), jumpTargetMask));
            }
        }
        
        // populate SIHT table with all permutations, 0 is -1 which is default for not found
        for (long i = 1; i < (1L<<entries.size()); i++) {
            long j = i;
            long mask = -1;
            long targetsMask = -1;
            while (j != 0) {
                int bit = Long.numberOfTrailingZeros(j);
                mask &= entries.get(bit).jumpMask;
                targetsMask &= entries.get(bit).jumpTargetMask;
                j = j & (j-1);
            }
            jumps.put(mask, targetsMask);
        }
        
        return new BitNFA(table, jumps, jumpMask, finishMask);
    }
}
