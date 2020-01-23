import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10_000; i++) {

            DeadLocker deadLocker = new DeadLocker();

            CompletableFuture<Void> increment1 = CompletableFuture.runAsync(deadLocker::incrementOne, pool);
            CompletableFuture<Void> increment2 = CompletableFuture.runAsync(deadLocker::incrementTwo, pool);

            CompletableFuture<Void> all = CompletableFuture.<Integer>allOf(increment1, increment2);
            all.thenApply((v) -> {

                return null;
            });
        }

        waitForThreadpoolShutdown(pool);
    }

    private static void waitForThreadpoolShutdown(ExecutorService pool) throws InterruptedException {
        pool.shutdownNow();
        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            System.err.println("Pool did not complete within 10 seconds");
            pool.shutdownNow();
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.err.println("Pool did not terminate");
            }
        }
    }

    public static class Counter {
        private AtomicInteger val = new AtomicInteger(0);

        public void increment() {
            val.incrementAndGet();
        }

        public int get() {
            return val.get();
        }
    }
}
