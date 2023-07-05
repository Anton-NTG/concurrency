package course.concurrency.m3_shared.amr_synchronized;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AMRVsSynchronized {

    private volatile String value = "value";
    private volatile boolean flag = false;

    private static final AtomicMarkableReference<String> reference = new AtomicMarkableReference<>(
            "Initial Value", false);

    public void runARMWrite(String newValue, boolean newMark) {
        String currentValue;
        boolean currentMark;
        do {
            boolean[] markHolder = new boolean[1];
            currentValue = reference.get(markHolder);
            currentMark = markHolder[0];
        } while (!reference.compareAndSet(currentValue, newValue, currentMark, newMark));
    }

    public synchronized void runSynchronizedWrite(String newValue, boolean newMark) {
        value = newValue;
        flag = newMark;
    }

    public void runARMRead() {
        String value = reference.getReference();
        boolean mark = reference.isMarked();
    }

    public synchronized void runSynchronizedRead() {
        String val = value;
        boolean mark = flag;
    }
}
