package course.concurrency.m5_streams.blockingqueue;

public class Producer <T> implements Runnable {
    final Queue<T> queue;
    volatile T value = null;

    public Producer(Queue<T> queue) {
        this.queue = queue;
    }

    public void produce(T value) {
        synchronized (this.queue) {
            this.value = value;
        }
    }

    public void run() {
        while (true) {
            synchronized (this.queue) {
                if (value != null) {
                    this.queue.enqueue(value);
                    value = null;
                }
            }
        }
    }
}
