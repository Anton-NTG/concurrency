package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        List<MountTableRefresher> refreshTasks = new Others.RouterStore().getRefreshTasks(states);
        for (int i = 0; i < refreshTasks.size(); i++) {
            String adminAddress = refreshTasks.get(i).getAdminAddress();
            refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithSuccess(
                    new Others.MountTableManager(adminAddress), adminAddress
            ));
        }
        when(routerStore.getRefreshTasks(states)).thenReturn(refreshTasks);

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
        List<MountTableRefresher> refreshTasks = new Others.RouterStore().getRefreshTasks(states);
        for (int i = 0; i < refreshTasks.size(); i++) {
            String adminAddress = refreshTasks.get(i).getAdminAddress();
            refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithFailure(
                    new Others.MountTableManager(adminAddress), adminAddress
            ));
        }
        when(routerStore.getRefreshTasks(states)).thenReturn(refreshTasks);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
        verify(routerClientsCache, times(4)).invalidate(anyString());
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
        List<MountTableRefresher> refreshTasks = new Others.RouterStore().getRefreshTasks(states);
        int randomNum1 = ThreadLocalRandom.current().nextInt(0, 4);
        int randomNum2;
        do {
            randomNum2 = ThreadLocalRandom.current().nextInt(0, 4);
        } while (randomNum2 == randomNum1);

        for (int i = 0; i < refreshTasks.size(); i++) {
            String adminAddress = refreshTasks.get(i).getAdminAddress();
            if (i == randomNum1 || i == randomNum2) {
                refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithSuccess(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            } else {
                refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithFailure(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            }
        }

        when(routerStore.getRefreshTasks(states)).thenReturn(refreshTasks);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=2,failureCount=2");
        verify(routerClientsCache, times(2)).invalidate(anyString());
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
        List<MountTableRefresher> refreshTasks = new Others.RouterStore().getRefreshTasks(states);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < refreshTasks.size(); i++) {
            if (i == randomNum) {
                String adminAddress = refreshTasks.get(i).getAdminAddress();
                refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithException(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
            }
        }
        when(routerStore.getRefreshTasks(states)).thenReturn(refreshTasks);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Exception occurred in mount table cache refresher");
        verify(routerClientsCache, atMost(4)).invalidate(anyString());
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
        List<MountTableRefresher> refreshTasks = new Others.RouterStore().getRefreshTasks(states);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < refreshTasks.size(); i++) {
            if (i == randomNum) {
                String adminAddress = refreshTasks.get(i).getAdminAddress();
                refreshTasks.set(i, new MountTableRefresher.MountTableRefresherWithTimeout(
                        new Others.MountTableManager(adminAddress), adminAddress
                ));
           }
        }
        when(routerStore.getRefreshTasks(states)).thenReturn(refreshTasks);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Not all router admins updated their cache");
        verify(routerClientsCache, atMost(3)).invalidate(anyString());
    }

}
