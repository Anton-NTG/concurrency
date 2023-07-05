package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid expectedLatestBid;
        do {
            expectedLatestBid = latestBid.get();
            if (bid.getPrice() <= expectedLatestBid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(expectedLatestBid, bid));
        notifier.sendOutdatedMessage(expectedLatestBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
