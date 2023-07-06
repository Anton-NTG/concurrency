package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public synchronized boolean propose(Bid bid) {
        if (bid.getPrice() <= latestBid.getPrice()) {
            return false;
        }
        synchronized (this) {
            notifier.sendOutdatedMessage(latestBid);
            latestBid = bid;
            return true;
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
