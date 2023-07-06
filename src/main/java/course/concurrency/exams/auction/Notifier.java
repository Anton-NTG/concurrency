package course.concurrency.exams.auction;

import java.util.concurrent.*;

public class Notifier {

    private final ExecutorService executor = new ThreadPoolExecutor(
        4000,
        4000,
        3,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>()
    );

    public void sendOutdatedMessage(Bid bid) {
        executor.submit(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ignore) {}
    }
}
