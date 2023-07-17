package course.concurrency.m3_shared.immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private long nextId = 0L;

    private long nextId() {
        return nextId++;
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = Order.createOrder(id, items);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order order = currentOrders.computeIfPresent(orderId, (key, value) -> value.updatedPaymentInfo(paymentInfo));
        if (order != null && order.checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    public void setPacked(long orderId) {
        Order order = currentOrders.computeIfPresent(orderId, (key, value) -> value.packed(true));
        if (order != null && order.checkStatus()) {
            deliver(currentOrders.get(orderId));
        }
    }

    private void deliver(Order order) {
        /* ... */
        currentOrders.computeIfPresent(order.getId(), (key, value) -> value.updatedStatus(Order.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        return currentOrders
                .getOrDefault(orderId, Order.createOrder(0L, new ArrayList<>()))
                .getStatus()
                .equals(Order.Status.DELIVERED);
    }
}





//package course.concurrency.m3_shared.immutable;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class OrderService {
//
//    private Map<Long, Order> currentOrders = new HashMap<>();
//    private long nextId = 0L;
//
//    private synchronized long nextId() {
//        return nextId++;
//    }
//
//    public synchronized long createOrder(List<Item> items) {
//        long id = nextId();
//        Order order = new Order(items);
//        order.setId(id);
//        currentOrders.put(id, order);
//        return id;
//    }
//
//    public synchronized void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
//        currentOrders.get(orderId).setPaymentInfo(paymentInfo);
//        if (currentOrders.get(orderId).checkStatus()) {
//            deliver(currentOrders.get(orderId));
//        }
//    }
//
//    public synchronized void setPacked(long orderId) {
//        currentOrders.get(orderId).setPacked(true);
//        if (currentOrders.get(orderId).checkStatus()) {
//            deliver(currentOrders.get(orderId));
//        }
//    }
//
//    private synchronized void deliver(Order order) {
//        /* ... */
//        currentOrders.get(order.getId()).setStatus(Order.Status.DELIVERED);
//    }
//
//    public synchronized boolean isDelivered(long orderId) {
//        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
//    }
//}
