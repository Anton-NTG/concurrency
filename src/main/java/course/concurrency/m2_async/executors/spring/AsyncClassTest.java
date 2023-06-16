package course.concurrency.m2_async.executors.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class AsyncClassTest {

    @Autowired
    public ApplicationContext context;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private ThreadPoolTaskExecutor executor;


    public void runAsyncTask() {
        executor.submit(() -> {
            System.out.println("runAsyncTask: " + Thread.currentThread().getName());
            internalTask();
        });
    }
    public void internalTask() {
        executor.submit(() -> System.out.println("internalTask: " + Thread.currentThread().getName()));
    }
}

