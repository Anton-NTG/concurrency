package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    public boolean propose(Bid bid) {
        if (latestBid.get() == null || bid.getPrice() > latestBid.get().getPrice()) {
            Bid expectedLatestBid;
            boolean updated;
            do {
                expectedLatestBid = latestBid.get();
                updated = latestBid.compareAndSet(expectedLatestBid, bid);
                if (updated) {
                    notifier.sendOutdatedMessage(expectedLatestBid);
                }
            } while (!updated);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
