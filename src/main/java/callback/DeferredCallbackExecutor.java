package callback;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DeferredCallbackExecutor {

    PriorityQueue<CallBack> queue = new PriorityQueue<>((o1, o2) -> (int) (o1.executeAt - o2.executeAt));

    private ReentrantLock lock = new ReentrantLock();
    private Condition callbackArrived = lock.newCondition();

    // Run by the Executor Thread
    public void start() {
        FutureTask futureTask = new FutureTask<Void>(() -> {
            long sleepForMillis = 0;
            int lastSeenQueueSize = 0;

            while (true) {
                lock.lock();

                try {
                    // await if queue is empty
                    if (queue.isEmpty())
                        callbackArrived.await();

                    // await if queue size is not changed since last cycle
                    if (lastSeenQueueSize == queue.size())
                        callbackArrived.await(sleepForMillis, TimeUnit.MILLISECONDS);

                    // execute past callbacks
                    long currentTime = System.currentTimeMillis();
                    while (!queue.isEmpty() && currentTime > queue.peek().executeAt) {
                        CallBack callback = queue.poll();
                        System.out.println("Executing callback " + callback.message + " at " + new Date(System.currentTimeMillis()));
                    }

                    // recalculate last seen queue size and wait time
                    sleepForMillis = queue.size() > 0 ? queue.peek().executeAt - System.currentTimeMillis(): 0;
                    lastSeenQueueSize = queue.size();
                } finally {
                    lock.unlock();
                }
            }
        });

        new Thread(futureTask).start();
    }

    // Called by Consumer Threads to register callback
    public void registerCallback(CallBack callBack) {
        lock.lock();

        try {
            queue.add(callBack);
            callbackArrived.signalAll();
            System.out.println("callback added");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Represents the class which holds the callback. For simplicity instead of
     * executing a method, we print a message.
     */
    private static class CallBack {
        long executeAt;
        String message;

        public CallBack(long executeAfterSec, String message) {
            this.executeAt = System.currentTimeMillis() + executeAfterSec * 1000;
            this.message = message;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeferredCallbackExecutor callbackExecutor = new DeferredCallbackExecutor();
        callbackExecutor.start();

        callbackExecutor.registerCallback(new CallBack(4, "test 2"));
        callbackExecutor.registerCallback(new CallBack(3, "test 1"));
    }
}
