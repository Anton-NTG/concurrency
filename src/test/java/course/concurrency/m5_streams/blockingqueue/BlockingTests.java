package course.concurrency.m5_streams.blockingqueue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BlockingTests {

    @Test
    void enqueueToFull() throws InterruptedException {
        Queue<Integer> queue = new Queue<>(2);

        queue.enqueue(3);
        queue.enqueue(5);

        new Thread(() -> queue.enqueue(3)).start();
        Thread.sleep(1000);

        assertEquals(2, queue.size());
        assertEquals(3, queue.get(0));
        assertEquals(5, queue.get(1));
    }

    @Test
    void dequeueEmpty() throws InterruptedException {
        Queue<Integer> queue = new Queue<>(2);

        new Thread(queue::dequeue).start();
        Thread.sleep(1000);

        assertTrue(queue.isEmpty());
    }

    @Test
    void unblockWhenSpaceAvailable() {
        Queue<Integer> queue = new Queue<>(2);

        Thread enqueueThread = new Thread(() -> {
            try {
                queue.enqueue(1);
                assertFalse(queue.isEmpty());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread dequeueThread = new Thread(() -> {
            try {
                queue.dequeue();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        dequeueThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        enqueueThread.start();

        try {
            enqueueThread.join();
            dequeueThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(queue.isEmpty());
    }
}
