package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Double.NaN;

public class PriceAggregator {
    private final ExecutorService customExecutor = Executors.newCachedThreadPool();

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
            priceTasks.add(CompletableFuture.supplyAsync(() ->
                priceRetriever.getPrice(itemId, shopId), customExecutor).exceptionally(ex -> {
                    System.out.println("Exception : " + ex.getMessage());
                    return NaN;
            }));
        }

        CompletableFuture<Void> futures = CompletableFuture.allOf(priceTasks.toArray(new CompletableFuture[0]));
        futures.completeOnTimeout(null, 2900, TimeUnit.MILLISECONDS).join();

        return priceTasks.stream()
                .filter(CompletableFuture::isDone)
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite)
                .min()
                .orElse(NaN);

//        CompletableFuture<Double> resultFuture = futures.handle((result, ex) ->
//            priceTasks.stream()
//                .filter(future -> future.isDone() && !future.isCompletedExceptionally())
//                .mapToDouble(CompletableFuture::join)
//                .min()
//                .orElse(NaN)
//        );
//        customExecutor.shutdown();
//        return resultFuture.join();

//        try {
//            futures.get(2900, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {}
//        customExecutor.shutdown();
//        return priceTasks.stream()
//            .filter(future -> future.isDone() && !future.isCompletedExceptionally())
//            .mapToDouble(CompletableFuture::join)
//            .min()
//            .orElse(NaN);

    }
}
