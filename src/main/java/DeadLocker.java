public class DeadLocker {
    private final Counter counterA = new Counter();
    private final Counter counterB = new Counter();

    public void incrementOne() {
        synchronized (counterA) {
            synchronized (counterB) {
                counterA.increment();
                counterB.increment();
            }
        }
    }

    public void incrementTwo() {
        synchronized (counterB) {
            synchronized (counterA) {
                counterB.increment();
                counterA.increment();
            }
        }
    }

    public Counter getCounterA() {
        return counterA;
    }

    public Counter getCounterB() {
        return counterB;
    }

    public static class Counter {
        private int val = 0;

        public void increment() {
            val += 1;
        }

        public int get() {
            return val;
        }
    }
}
