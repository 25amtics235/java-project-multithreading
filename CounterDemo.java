import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterDemo {

    public static void main(String[] args) {

        int numberOfThreads = 4;
        int incrementsPerThread = 100000;

        // Unsafe counter: race condition
        int[] unsafeCounter = {0};

        // Thread-safe counter using AtomicInteger
        AtomicInteger safeCounter = new AtomicInteger(0);

        // Create a thread pool
        ExecutorService pool =
                Executors.newFixedThreadPool(numberOfThreads);

        // Submit multiple tasks
        for (int t = 0; t < numberOfThreads; t++) {

            pool.submit(() -> {

                for (int i = 0; i < incrementsPerThread; i++) {

                    // Unsafe increment
                    unsafeCounter[0]++;

                    // Thread-safe increment
                    safeCounter.incrementAndGet();
                }
            });
        }

        // Stop accepting new tasks
        pool.shutdown();

        try {
            // Wait for all tasks to finish
            if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                pool.shutdownNow();
            }

            // Display results
            int expected = numberOfThreads * incrementsPerThread;

            System.out.println("Expected Count: " + expected);
            System.out.println("Unsafe Count:   " + unsafeCounter[0]);
            System.out.println("Atomic Count:   " + safeCounter.get());

        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Execution interrupted.");
        }
    }
}