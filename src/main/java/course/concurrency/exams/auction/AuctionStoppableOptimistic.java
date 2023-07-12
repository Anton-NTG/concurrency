package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid =
            new AtomicMarkableReference<>(new Bid(0L, 0L, 0L), true);

    public boolean propose(Bid bid) {
        Bid expectedLatestBid;
        do {
            if (!latestBid.isMarked()) return false;
            expectedLatestBid = latestBid.getReference();
            if (bid.getPrice() <= expectedLatestBid.getPrice()) return false;
        } while (!latestBid.compareAndSet(expectedLatestBid, bid, true, true));
        notifier.sendOutdatedMessage(expectedLatestBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        if (!latestBid.isMarked()) return getLatestBid();
        Bid expectedLatestBid;
        do {
            expectedLatestBid = latestBid.getReference();
        } while (!latestBid.attemptMark(expectedLatestBid, false));
        return getLatestBid();
    }
}
