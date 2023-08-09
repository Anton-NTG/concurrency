package course.concurrency.m5_streams.blockingqueue;

import java.lang.reflect.Array;

public class Queue <T> {

    private volatile T[] queue;
    private volatile int index = 0;
    private final int capacity;
    private final Class<T> cls;

    public Queue(Class<T> cls, int capacity) {
        this.cls = cls;
        @SuppressWarnings("unchecked")
        final T[] queue = (T[]) Array.newInstance(cls, capacity);
        this.queue = queue;
        this.capacity = capacity;
    }

    synchronized void enqueue(T item) {
        if (index == this.capacity) return;
        queue[index] = item;
        if (index < this.capacity) index++;
    }

    synchronized T dequeue() {
        T value = queue[0];
        @SuppressWarnings("unchecked")
        T[] q = (T[]) Array.newInstance(cls, capacity);
        System.arraycopy(queue, 1, q, 0, queue.length - 1);
        queue = q;
        index--;
        return value;
    }

    int getLength() {
        return index;
    }

    public static void main(String[] args) throws InterruptedException {
        Queue<String> queue = new Queue<>(String.class, 10);
        Producer<String> producer = new Producer<>(queue);
        Consumer<String> consumer = new Consumer<>(queue);

        new Thread(producer).start();
        new Thread(consumer).start();

        producer.produce("aaa");

        Thread.sleep(1000);

        producer.produce("bbb");

        Thread.sleep(1000);

        producer.halt();
        consumer.halt();

    }
}
