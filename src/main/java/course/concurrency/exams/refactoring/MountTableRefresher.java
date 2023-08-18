package course.concurrency.exams.refactoring;

import static course.concurrency.exams.refactoring.Others.*;

import java.util.concurrent.CountDownLatch;

public class MountTableRefresher {

    private boolean success;
    /** Admin server on which refreshed to be invoked. */
    private String adminAddress;
    private MountTableManager manager;

    public MountTableRefresher(MountTableManager manager,
                               String adminAddress) {
        this.manager = manager;
        this.adminAddress = adminAddress;
    }

    /**
     * Refresh mount table cache of local and remote routers. Local and remote
     * routers will be refreshed differently. Lets understand what are the
     * local and remote routers and refresh will be done differently on these
     * routers. Suppose there are three routers R1, R2 and R3. User want to add
     * new mount table entry. He will connect to only one router, not all the
     * routers. Suppose He connects to R1 and calls add mount table entry through
     * API or CLI. Now in this context R1 is local router, R2 and R3 are remote
     * routers. Because add mount table entry is invoked on R1, R1 will update the
     * cache locally it need not to make RPC call. But R1 will make RPC calls to
     * update cache on R2 and R3.
     */
    public void refresh() {
        success = manager.refresh();
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return true if cache was refreshed successfully.
     */
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "MountTableRefreshThread [success=" + success + ", adminAddress="
                + adminAddress + "]";
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public static class MountTableRefresherWithException extends MountTableRefresher {
        public MountTableRefresherWithException(MountTableManager manager, String adminAddress) {
            super(manager, adminAddress);
        }

        public void refresh() {
            throw new RuntimeException();
        }
    }

    public static class MountTableRefresherWithTimeout extends MountTableRefresher {
        public MountTableRefresherWithTimeout(MountTableManager manager, String adminAddress) {
            super(manager, adminAddress);
        }

        @Override
        public void refresh() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class MountTableRefresherWithSuccess extends MountTableRefresher {
        public MountTableRefresherWithSuccess(MountTableManager manager, String adminAddress) {
            super(manager, adminAddress);
        }

        @Override
        public void refresh() {
            setSuccess(true);
        }
    }

    public static class MountTableRefresherWithFailure extends MountTableRefresher {
        public MountTableRefresherWithFailure(MountTableManager manager, String adminAddress) {
            super(manager, adminAddress);
        }

        @Override
        public void refresh() {
            setSuccess(false);
        }
    }
}

