package course.concurrency.m3_shared.deadlock;

import java.util.concurrent.ConcurrentHashMap;

public class Deadlock {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map1 = new ConcurrentHashMap<>();
        map1.put("A", 1);

        ConcurrentHashMap<String, Integer> map2 = new ConcurrentHashMap<>();
        map2.put("B", 2);

        new Thread(() -> {
            map1.compute("A", (a, b) -> {
                map2.merge("B", 2, (c, d) -> d + 10);
                return b + 10;
            });
        }).start();

        new Thread(() -> {
            map2.compute("B", (a, b) -> {
                map1.merge("A", 1, (c, d) -> d + 10);
                return b + 10;
            });

        }).start();
    }
}
