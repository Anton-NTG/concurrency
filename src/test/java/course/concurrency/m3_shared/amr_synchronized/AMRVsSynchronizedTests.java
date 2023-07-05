package course.concurrency.m3_shared.amr_synchronized;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AMRVsSynchronizedTests {

    ExecutorService executor;
    AMRVsSynchronized testObject;

    @BeforeEach
    public void setup() {
        executor = Executors.newCachedThreadPool();
        testObject = new AMRVsSynchronized();
    }

    @Test
    public void testAMRWrite() {
        for (int i = 0; i < 1000000; i++) {
            executor.submit(() ->
                    testObject.runAMRWrite(Thread.currentThread().getName(), true));
        }
    }

    @Test
    public void testSynchronizedWrite() {
        for (int i = 0; i < 1000000; i++) {
            executor.submit(() ->
                    testObject.runSynchronizedWrite(Thread.currentThread().getName(), true));
        }
    }

    @Test
    public void testAMRRead() {
        for (int i = 0; i < 1000000; i++) {
            executor.submit(testObject::runAMRRead);
        }
    }

    @Test
    public void testSynchronizedRead() {
        for (int i = 0; i < 1000000; i++) {
            executor.submit(testObject::runSynchronizedRead);
        }
    }
}
