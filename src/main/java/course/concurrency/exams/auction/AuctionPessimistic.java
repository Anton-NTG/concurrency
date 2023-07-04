package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            notifier.sendOutdatedMessage(latestBid);
            synchronized (this) {
                latestBid = bid;
            }
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
