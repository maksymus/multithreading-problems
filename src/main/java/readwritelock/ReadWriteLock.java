package readwritelock;

/**
 * Reader lock - no writer locks. Multiple readers are fine (no data is modified by reader).
 * Writer lock - no reader/writer lock.
 */
public class ReadWriteLock {
    private boolean writerLocked = false;
    private int readerCounter = 0;

    public synchronized void acquireReadLock() throws InterruptedException {
        while (writerLocked) {
            wait();
        }

        readerCounter++;
    }

    public synchronized void releaseReadLock() {
        if (readerCounter > 0) {
            readerCounter--;
        }

        notifyAll();
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (readerCounter > 0 || writerLocked) {
            wait();
        }

        writerLocked = true;
    }

    public synchronized void releaseWriteLock() {
        writerLocked = false;
        notifyAll();
    }
}
