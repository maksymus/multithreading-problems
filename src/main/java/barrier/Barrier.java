package barrier;

import java.util.concurrent.FutureTask;

/**
 * Implementing a Barrier.
 * A barrier can be thought of as a point in the program code, which all or some of the threads need to reach at before
 * any one of them is allowed to proceed further.
 */
public class Barrier {
    /** Number of thread to wait for */
    private final int totalNumWait;

    /** Current number of threads waiting on barrier*/
    private int count;

    public Barrier(int totalNumWait) {
        this.totalNumWait = totalNumWait;
    }

    public synchronized void await() throws InterruptedException {
        count++;

        if (count >= totalNumWait) {
            count -= totalNumWait;
            notifyAll();
        } else {
            wait();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Barrier barrier = new Barrier(3);

        FutureTask<Void> futureTask1 = new FutureTask<>(() -> {
            System.out.println("thread1");
            barrier.await();
            System.out.println("thread1");
            barrier.await();
            System.out.println("thread1");
            barrier.await();
            return null;
        });


        FutureTask<Void> futureTask2 = new FutureTask<>(() -> {
            System.out.println("thread2");
            barrier.await();
            System.out.println("thread2");
            barrier.await();
            System.out.println("thread2");
            barrier.await();
            return null;
        });

        FutureTask<Void> futureTask3 = new FutureTask<>(() -> {
            System.out.println("thread3");
            barrier.await();
            System.out.println("thread3");
            barrier.await();
            System.out.println("thread3");
            barrier.await();
            return null;
        });

        Thread thread1 = new Thread(futureTask1);
        Thread thread2 = new Thread(futureTask2);
        Thread thread3 = new Thread(futureTask3);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }
}
