package course.concurrency.exams.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Others {

    public static class LoadingCache<K, V> {
        Map<K,V> map = new ConcurrentHashMap<>();

        public void add(K key, V value) {
            map.put(key, value);
        }

        public void invalidate(String address) {
            map.remove(address);
        }

        public void cleanUp() { map.clear(); }

    }

    public static class RouterClient {

    }

    public static class RouterState {
        private static AtomicInteger counter = new AtomicInteger(0);
        private String adminAddress;

        public RouterState(String address) {
            this.adminAddress = address + counter.incrementAndGet();
        }

        public String getAdminAddress() {
            return adminAddress;
        }
    }

    public static class RouterStore {
        List<RouterState> states = new ArrayList<>();
        List<MountTableRefresherThread> refreshThreads = new ArrayList<>();

        public List<RouterState> getCachedRecords() {
            return states;
        }
        public List<MountTableRefresherThread> getRefreshThreads(List<Others.RouterState> cachedRecords) {
            for (Others.RouterState routerState : cachedRecords) {
                String adminAddress = routerState.getAdminAddress();
                if (adminAddress == null || adminAddress.length() == 0) {
                    // this router has not enabled router admin.
                    continue;
                }
                MountTableRefresherThread thread = new MountTableRefresherThread(
                        new Others.MountTableManager(adminAddress), adminAddress);
                if (isLocalAdmin(adminAddress)) {
                    /*
                     * Local router's cache update does not require RPC call, so no need for
                     * RouterClient
                     */
                    thread = getLocalRefresher(adminAddress);
                }
                refreshThreads.add(thread);
            }

            return refreshThreads;
        }

        protected MountTableRefresherThread getLocalRefresher(String adminAddress) {
            return new MountTableRefresherThread(new Others.MountTableManager("local"), adminAddress);
        }

        private boolean isLocalAdmin(String adminAddress) {
            return adminAddress.contains("local");
        }
    }

    public static class MountTableManager {

        private String address;
        public MountTableManager(String address) {
            this.address = address;
        }

        public boolean refresh() {
            return ThreadLocalRandom.current().nextBoolean();
        }
    }
}
