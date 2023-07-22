package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final ConcurrentHashMap<String, LongAdder> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public synchronized void addToStat(String restaurantName) {
        // your code
//        synchronized (this) {
//        Integer val = stat.get(restaurantName);
//        Integer statValue = Optional.ofNullable(val).map(i -> i + 1).orElse(1);
//        stat.put(restaurantName, statValue);
//        }
        //stat.compute(restaurantName, (key, value) -> value == null ? 1 : value + 1);

        LongAdder adder = stat.computeIfAbsent(restaurantName, k -> new LongAdder());
        adder.increment();
//        stat.compute(restaurantName, (key, value) -> {
//            if (value == null) {
//                value = new LongAdder();
//            }
//            value.increment();
//            return value;
//        });
        //stat.merge(restaurantName, 1, (k, value) -> value + 1);
    }

    public Set<String> printStat() {
        // your code
        HashSet<String> set = new HashSet<>();
        stat.forEach((key, value) -> set.add(key + " - " + value.sum()));
        return set;
        //return new HashSet<>();
    }
}
