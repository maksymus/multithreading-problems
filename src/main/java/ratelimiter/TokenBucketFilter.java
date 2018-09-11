package ratelimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The token bucket algorithm is based on an analogy of a fixed capacity bucket into which tokens, normally
 * representing a unit of bytes or a single packet of predetermined size, are added at a fixed rate.
 * When a packet is to be checked for conformance to the defined limits, the bucket is inspected to see if it contains
 * sufficient tokens at that time. If so, the appropriate number of tokens, e.g. equivalent to the length of the packet
 * in bytes, are removed ("cashed in"), and the packet is passed, e.g., for transmission. The packet does not conform
 * if there are insufficient tokens in the bucket, and the contents of the bucket are not changed.
 */
public class TokenBucketFilter {
    private final int maxTokens;
    private long availableTokens;

    private long lastRefillTimeMillis;

    private final double refillTokensPerOneMillis;

    public TokenBucketFilter(int maxTokens, int initTokens, int refillPeriod, TimeUnit timeUnit) {
        this.maxTokens = maxTokens;
        this.availableTokens = initTokens;
        this.lastRefillTimeMillis = System.currentTimeMillis();
        this.refillTokensPerOneMillis = 1.0 / (double) timeUnit.toMillis(refillPeriod);
    }

    public synchronized void getTokens(int numTokens) throws InterruptedException {
        while (!tryGetTokens(numTokens)) {
            Thread.sleep(0, 1);
//            Thread.yield();
        }
    }

    public synchronized void getToken() throws InterruptedException {
        getTokens(1);
    }

    private boolean tryGetTokens(int numTokens) {
        refill();

        if (availableTokens >= numTokens) {
            availableTokens -= numTokens;
            return true;
        }

        return false;
    }

    private void refill() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > lastRefillTimeMillis) {
            long passedMillis = currentTimeMillis - lastRefillTimeMillis;
            long tokens = (long) (refillTokensPerOneMillis * passedMillis);

            long newAvailableTokens = Math.min(maxTokens, availableTokens + tokens);
            if (newAvailableTokens != availableTokens) {
                availableTokens = newAvailableTokens;
                long entropy = (passedMillis - (long) (tokens / refillTokensPerOneMillis));
                lastRefillTimeMillis = currentTimeMillis - entropy;
            }
        }
    }

    public static void main(String[] args) {
        TokenBucketFilter tokenBucketFilter = new TokenBucketFilter(2, 0, 1, TimeUnit.SECONDS);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    tokenBucketFilter.getToken();
                    System.out.println("got token " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
