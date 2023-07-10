package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);
    private volatile boolean active = true;

    private final Object lock = new Object();

    public boolean propose(Bid bid) {
        if (!active) return false;
        if (bid.getPrice() <= latestBid.getPrice()) return false;
        synchronized (lock) {
            if (bid.getPrice() <= latestBid.getPrice()) {
                return false;
            } else {
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        synchronized (lock) {
            active = false;
            return latestBid;
        }
    }
}
