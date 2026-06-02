package pro.rewindlabs.regex;

public class NativeThompson {

    static interface Trace {
        void exec(State state, ThompsonThread current);
    }

    static Trace pc0 = new Trace() {
        @Override
        public void exec(State state, ThompsonThread current) {
            if (state.ch == 'a') {
                var t = state.fork(pc0);
                state.nlistPush(t);
                current.trace = pc2;
                state.nlistPush(current);
                current = state.clistPop();
                if (current != null) {
                    current.trace.exec(state, current);
                }
            } else {
                state.terminate(current);
                current = state.clistPop();
                if (current != null) {
                    current.trace.exec(state, current);
                }
            }
        }
    };

    static Trace pc2 = new Trace() {
        @Override
        public void exec(State state, ThompsonThread current) {
            if (state.ch == 'b') {
                current.trace = pc3;
                state.nlistPush(current);
                current = state.clistPop();
                if (current != null) {
                    current.trace.exec(state, current);
                }
            } else {
                state.terminate(current);
                current = state.clistPop();
                if (current != null) {
                    current.trace.exec(state, current);
                }
            }
        }
    };

    static Trace pc3 = new Trace() {
        @Override
        public void exec(State state, ThompsonThread current) {
            state.matched = current;
        }
    };

    static class ThompsonThread {
        ThompsonThread next;
        Trace trace;
    }

    static class State {
        ThompsonThread freelist;
        ThompsonThread clistHead, clistTail;
        ThompsonThread nlistHead, nlistTail;
        int ch;
        ThompsonThread matched;

        ThompsonThread allocate() {
            if (freelist != null) {
                var t = freelist;
                freelist = freelist.next;
                t.next = null;
                return t;
            }
            return new ThompsonThread();
        }

        ThompsonThread fork(Trace trace) {
            var t = allocate();
            t.trace = trace;
            return t;
        }

        void terminate(ThompsonThread t) {
            t.next = freelist;
            freelist = t;
        }
        
        void clistPush(ThompsonThread t) {
            if (clistTail == null) {
                clistHead = clistTail = t;
            } else {
                clistTail.next = t;
                clistTail = t;
            }
        }

        boolean clistEmpty(){ return clistHead == null; }

        ThompsonThread clistPop() {
            ThompsonThread t = clistHead;
            if (clistHead == clistTail) {
                clistHead = clistTail = null;
                return t;
            }
            clistHead = clistHead.next;
            return t;
        }

        void nlistPush(ThompsonThread t) {
            if (nlistTail == null) {
                nlistHead = nlistTail = t;
            } else {
                nlistTail.next = t;
                nlistTail = t;
            }
        }

        boolean nlistEmpty(){ return nlistHead == null; }

        ThompsonThread nlistPop() {
            ThompsonThread t = nlistHead;
            if (nlistHead == nlistTail) {
                nlistHead = nlistTail = null;
                return t;
            }
            nlistHead = nlistHead.next;
            return t;
        }

        public boolean search(CharSequence seq) {
            clistPush(fork(pc0));
            for (int i = 0; i < seq.length(); i++) {
                ch = seq.charAt(i);
                var head = clistPop();
                if (head == null) {
                    return false;
                }
                head.trace.exec(this, head);
                if (matched != null) {
                    return true;
                }
                clistHead = nlistHead;
                clistTail = nlistTail;
                nlistHead = nlistTail = null;
            }
            ch = Integer.MIN_VALUE;
            var head = clistPop();
            if (head == null) {
                return false;
            }
            head.trace.exec(this, head);
            return matched != null;
        }
    }

    public static boolean search(CharSequence seq) {
        return new State().search(seq);
    }
}
