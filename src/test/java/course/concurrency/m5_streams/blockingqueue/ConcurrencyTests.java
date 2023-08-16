package course.concurrency.m5_streams.blockingqueue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyTests {

    @Test
    void concurrentEnqueueDequeue() throws InterruptedException {
        int threadsCount = 100;
        int iterations = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        Queue<Integer> queue = new Queue<>(Integer.class, threadsCount);

        CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            executor.submit(new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    queue.enqueue(j);
                    Integer value = queue.dequeue();
                    assertEquals(j, value);
                }

            }));
            latch.countDown();
        }


        latch.await();

        executor.shutdown();

        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //assertTrue(queue.isEmpty());
    }
}
