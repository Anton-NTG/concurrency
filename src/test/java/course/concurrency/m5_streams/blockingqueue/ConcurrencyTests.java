package course.concurrency.m5_streams.blockingqueue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyTests {

    @Test
    void concurrentEnqueueDequeue() throws InterruptedException {
        int threadsCount = 3;
        int iterations = 10;
        Queue<Integer> queue = new Queue<>(threadsCount);

        //CountDownLatch latch = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    queue.enqueue(j);
                    Integer value = queue.dequeue();
                    assertEquals(j, value);
                }
                //latch.countDown();
            }).start();
        }

        //latch.await();

        assertTrue(queue.isEmpty());
    }
}
