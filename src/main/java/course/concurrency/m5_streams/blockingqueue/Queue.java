package course.concurrency.m5_streams.blockingqueue;

import java.lang.reflect.Array;

public class Queue <T> {

    private volatile T[] queue;
    private volatile int index = -1;
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
        while (isFull()) blockQueue();
        index++;
        queue[index] = item;
        notify();
    }

    synchronized T dequeue() {
        while (isEmpty()) blockQueue();
        T value = queue[0];
        @SuppressWarnings("unchecked")
        T[] q = (T[]) Array.newInstance(cls, capacity);
        System.arraycopy(queue, 1, q, 0, size() - 1);
        queue = q;
        index--;
        notify();
        return value;
    }

    synchronized void blockQueue() {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized boolean isEmpty() {
        return index == -1;
    }

    synchronized boolean isFull() {
        return index == capacity - 1;
    }

    synchronized int size() {
        return index + 1;
    }

    synchronized T get(int index) {
        return queue[index];
    }

    public static void main(String[] args) throws InterruptedException {
        Queue<String> queue = new Queue<>(String.class, 10);
        Producer<String> producer = new Producer<>(queue);
        Consumer<String> consumer = new Consumer<>(queue);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.setDaemon(true);
        consumerThread.setDaemon(true);

        producerThread.start();
        consumerThread.start();

        producer.produce("aaa");

        Thread.sleep(1000);

        producer.produce("bbb");

        Thread.sleep(1000);

    }
}
