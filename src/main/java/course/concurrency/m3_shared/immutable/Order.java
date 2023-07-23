package course.concurrency.m3_shared.immutable;

import java.util.Collections;
import java.util.List;

public final class Order {

    public enum Status { NEW, IN_PROGRESS, DELIVERED }

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = Collections.unmodifiableList(items);
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public static Order createOrder(Long id, List<Item> items) {
        return new Order(id, items, null, false, Status.NEW);
    }

    public boolean checkStatus() {
        if (items != null
                && !items.isEmpty()
                && paymentInfo != null
                && isPacked
                && this.status != Order.Status.DELIVERED
        ) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Status getStatus() {
        return status;
    }

    public Order updatedPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(this.id, this.items, paymentInfo, this.isPacked, Status.IN_PROGRESS);
    }

    public Order packed(boolean isPacked) {
        return new Order(this.id, this.items, this.paymentInfo, isPacked, Status.IN_PROGRESS);
    }

    public Order updatedStatus(Status status) {
        return new Order(this.id, this.items, this.paymentInfo, this.isPacked, status);
    }
}



//package course.concurrency.m3_shared.immutable;
//
//import java.util.List;
//
//import static course.concurrency.m3_shared.immutable.Order.Status.NEW;
//
//public class Order {
//
//    public enum Status { NEW, IN_PROGRESS, DELIVERED }
//
//    private Long id;
//    private List<Item> items;
//    private PaymentInfo paymentInfo;
//    private boolean isPacked;
//    private Status status;
//
//    public Order(List<Item> items) {
//        this.items = items;
//        this.status = NEW;
//    }
//
//    public synchronized boolean checkStatus() {
//        if (items != null && !items.isEmpty() && paymentInfo != null && isPacked) {
//            return true;
//        }
//        return false;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public List<Item> getItems() {
//        return items;
//    }
//
//    public PaymentInfo getPaymentInfo() {
//        return paymentInfo;
//    }
//
//    public void setPaymentInfo(PaymentInfo paymentInfo) {
//        this.paymentInfo = paymentInfo;
//        this.status = Status.IN_PROGRESS;
//    }
//
//    public boolean isPacked() {
//        return isPacked;
//    }
//
//    public void setPacked(boolean packed) {
//        isPacked = packed;
//        this.status = Status.IN_PROGRESS;
//    }
//
//    public Status getStatus() {
//        return status;
//    }
//
//    public void setStatus(Status status) {
//        this.status = status;
//    }
//}
