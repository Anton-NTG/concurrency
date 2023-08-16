package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

public class MountTableRefresherServiceTests {

    private MountTableRefresherService service;

    private Others.RouterStore routerStore;
    private Others.MountTableManager manager;
    private Others.LoadingCache routerClientsCache;

    @BeforeEach
    public void setUpStreams() {
        service = new MountTableRefresherService();
        service.setCacheUpdateTimeout(1000);
        routerStore = mock(Others.RouterStore.class);
        manager = mock(Others.MountTableManager.class);
        service.setRouterStore(routerStore);
        routerClientsCache = mock(Others.LoadingCache.class);
        service.setRouterClientsCache(routerClientsCache);
        // service.serviceInit(); // needed for complex class testing, not for now
    }

    @AfterEach
    public void restoreStreams() {
        // service.serviceStop();
    }

    @Test
    @DisplayName("All tasks are completed successfully")
    public void allDone() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);

        // smth more
        List<MountTableRefresherThread> refreshThreads = new Others.RouterStore().getRefreshThreads(states);
        for (int i = 0; i < refreshThreads.size(); i++) {
            String adminAddress = refreshThreads.get(i).getAdminAddress();
            refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithSuccess(
                    new Others.MountTableManager(adminAddress), adminAddress
            ));
        }
        when(routerStore.getRefreshThreads(states)).thenReturn(refreshThreads);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=4,failureCount=0");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("All tasks failed")
    public void noSuccessfulTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);

        // smth more
        List<MountTableRefresherThread> refreshThreads = new Others.RouterStore().getRefreshThreads(states);
        for (int i = 0; i < refreshThreads.size(); i++) {
            String adminAddress = refreshThreads.get(i).getAdminAddress();
            refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithFailure(
                    new Others.MountTableManager(adminAddress), adminAddress
            ));
        }
        when(routerStore.getRefreshThreads(states)).thenReturn(refreshThreads);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
    }

    @Test
    @DisplayName("Some tasks failed")
    public void halfSuccessedTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(manager.refresh()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);

        // smth more
        List<MountTableRefresherThread> refreshThreads = new Others.RouterStore().getRefreshThreads(states);
        int randomNum1 = ThreadLocalRandom.current().nextInt(0, 4);
        int randomNum2 = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < refreshThreads.size(); i++) {
            String adminAddress = refreshThreads.get(i).getAdminAddress();
            if (i == randomNum1 || i == randomNum2) {
                refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithSuccess(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            } else {
                refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithFailure(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            }
        }
        when(routerStore.getRefreshThreads(states)).thenReturn(refreshThreads);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=2,failureCount=2");
    }

    @Test
    @DisplayName("One task completed with exception")
    public void exceptionInOneTask() {
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);

        // smth more
        List<MountTableRefresherThread> refreshThreads = new Others.RouterStore().getRefreshThreads(states);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < refreshThreads.size(); i++) {
            if (i == randomNum) {
                String adminAddress = refreshThreads.get(i).getAdminAddress();
                refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithException(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            }
        }
        when(routerStore.getRefreshThreads(states)).thenReturn(refreshThreads);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Exception occurred in mount table cache refresher");
    }

    @Test
    @DisplayName("One task exceeds timeout")
    public void oneTaskExceedTimeout() {
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        List<Others.RouterState> states = addresses.stream()
                .map(a -> new Others.RouterState(a)).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);

        // smth more
        List<MountTableRefresherThread> refreshThreads = new Others.RouterStore().getRefreshThreads(states);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < refreshThreads.size(); i++) {
            if (i == randomNum) {
                String adminAddress = refreshThreads.get(i).getAdminAddress();
                refreshThreads.set(i, new MountTableRefresherThread.MountTableRefresherThreadWithTimeout(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            }
        }
        when(routerStore.getRefreshThreads(states)).thenReturn(refreshThreads);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Not all router admins updated their cache");
    }

}
