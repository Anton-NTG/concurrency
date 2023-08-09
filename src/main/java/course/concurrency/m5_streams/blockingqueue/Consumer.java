package course.concurrency.m5_streams.blockingqueue;

public class Consumer <T> implements Runnable {

    final Queue<T> queue;

    volatile boolean active = true;

    public Consumer(Queue<T> queue) {
        this.queue = queue;
    }

    public void halt() {
        this.active = false;
    }

    public void run() {
        while (active) {
            synchronized (this.queue) {
                if (this.queue.getLength() > 0) {
                    T value = this.queue.dequeue();
                    System.out.println(value);
                    if (this.queue.getLength() == 0) {
                        try {
                            this.queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
