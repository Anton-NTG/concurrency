package course.concurrency.m5_streams.blockingqueue;

public class Consumer <T> implements Runnable {
    final Queue<T> queue;

    public Consumer(Queue<T> queue) {
        this.queue = queue;
    }

    public void run() {
        while (true) {
            synchronized (this.queue) {
                if (this.queue.getLength() > 0) {
                    T value = this.queue.dequeue();
                    System.out.println(value);
                    this.queue.queueWait();
                }
            }
        }
    }
}
