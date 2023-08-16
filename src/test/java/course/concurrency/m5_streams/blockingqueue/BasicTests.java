package course.concurrency.m5_streams.blockingqueue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasicTests {

    @Test
    void enqueue() {
        Queue<String> queue = new Queue<>(5);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertEquals("a", queue.get(0));
        assertEquals("b", queue.get(1));
        assertEquals("c", queue.get(2));
    }

    @Test
    void dequeue() {
        Queue<String> queue = new Queue<>(5);
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");

        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());
        assertEquals("c", queue.dequeue());
    }

    @Test
    void isEmpty() {
        Queue<String> queue = new Queue<>(5);
        assertTrue(queue.isEmpty());

        queue.enqueue("a");
        assertFalse(queue.isEmpty());

        queue.dequeue();
        assertTrue(queue.isEmpty());
    }

    @Test
    void isFull() {
        Queue<String> queue = new Queue<>(2);
        assertFalse(queue.isFull());

        queue.enqueue("a");
        queue.enqueue("b");
        assertTrue(queue.isFull());

        queue.dequeue();
        assertFalse(queue.isFull());

        queue.dequeue();
        assertFalse(queue.isFull());
    }

    @Test
    void size() {
        Queue<String> queue = new Queue<>(5);
        assertEquals(0, queue.size());

        queue.enqueue("a");
        assertEquals(1, queue.size());

        queue.enqueue("b");
        assertEquals(2, queue.size());

        queue.dequeue();
        assertEquals(1, queue.size());

        queue.dequeue();
        assertEquals(0, queue.size());
    }
}
