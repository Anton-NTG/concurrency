package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Double.NaN;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        // place for your code
        List<CompletableFuture<Double>> priceTasks = new ArrayList<>();
        for (long shopId: shopIds) {
            priceTasks.add(CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(1, shopId)));
        }

        CompletableFuture<Void> futures = CompletableFuture.allOf(priceTasks.toArray(new CompletableFuture[0]));
        try {
            futures.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {}
        return priceTasks.stream()
            .filter(future -> future.isDone() && !future.isCompletedExceptionally())
            .mapToDouble(CompletableFuture::join)
            .min()
            .orElse(NaN);
    }
}
