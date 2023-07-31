package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(
                2,
                4,
                10L,
                TimeUnit.SECONDS,
                new LifoBlockingDeque<>()
        );
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(
                8,
                8,
                10L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                (runnable, executor) -> {
                    System.out.println("Rejected");
                }
        );
    }
}
