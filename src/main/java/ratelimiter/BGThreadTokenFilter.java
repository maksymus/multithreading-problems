package ratelimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Use background thread to populate bucket.
 * Drawbacks:
 * <ul>
 *     <li>Requires at least one background thread.</li>
 *      <li>Background thread should be shut down.</li>
 * </ul>
 */
public class BGThreadTokenFilter {
    private final int maxTokens;

    private long possibleTokens = 0;

    private ReentrantLock lock = new ReentrantLock();
    private Condition noTokensCondition = lock.newCondition();
    private Condition bucketFullCondition = lock.newCondition();

    private ScheduledExecutorService periodicService = Executors.newSingleThreadScheduledExecutor();

    public BGThreadTokenFilter(int maxTokens, int refillPeriod, TimeUnit timeUnit) {
        this.maxTokens = maxTokens;
//        this.possibleTokens = maxTokens;

        periodicService.scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                while (possibleTokens >= maxTokens) {
                    try {
                        bucketFullCondition.await();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                possibleTokens++;
                noTokensCondition.signalAll();
            } finally {
                lock.unlock();
            }
        }, 0, refillPeriod, timeUnit);
    }

    public void getToken() throws InterruptedException {
        lock.lock();
        try {
            while (possibleTokens == 0) {
                noTokensCondition.await();
            }

            possibleTokens--;

            bucketFullCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        periodicService.shutdown();
    }

    public static void main(String[] args) {
        BGThreadTokenFilter tokenBucketFilter = new BGThreadTokenFilter(2, 1, TimeUnit.SECONDS);

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

        tokenBucketFilter.shutdown();
    }
}
