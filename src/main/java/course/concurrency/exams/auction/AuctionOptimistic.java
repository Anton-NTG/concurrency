package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.get().getPrice()) {
            Bid expectedLatestBid;
            do {
                expectedLatestBid = latestBid.get();
            } while (!latestBid.compareAndSet(expectedLatestBid, bid));
            notifier.sendOutdatedMessage(expectedLatestBid);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
