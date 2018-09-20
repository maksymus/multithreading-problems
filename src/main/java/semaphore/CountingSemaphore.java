package semaphore;

public class CountingSemaphore {

    int usedPermits = 0; // permits given out
    int maxCount;  // max permits to give out

    public CountingSemaphore(int count) {
        this.maxCount = count;
    }

    public synchronized void acquire() throws InterruptedException {
        while (usedPermits >= maxCount)
            wait();

        usedPermits++;
        notifyAll();
    }

    public synchronized void release() throws InterruptedException {
        while (usedPermits == 0)
            wait();

        usedPermits--;
        notifyAll();
    }
}
