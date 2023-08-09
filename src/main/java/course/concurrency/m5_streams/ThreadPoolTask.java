package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        LifoBlockingDeque<Runnable> blockingDeque = new LifoBlockingDeque<>();
        return new ThreadPoolExecutor(8, 8, 10L, TimeUnit.SECONDS, blockingDeque);
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        return new ThreadPoolExecutor(
                8,
                8,
                10L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy()
        );
    }
}
