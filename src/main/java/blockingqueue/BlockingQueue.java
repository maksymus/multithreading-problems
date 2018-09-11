package blockingqueue;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class BlockingQueue<T> {
    private final int capacity;

    Object[] buffer;

    int size;
    int putPosition;
    int takePosition;

    private ReentrantLock lock = new ReentrantLock();
    private Condition isFull = lock.newCondition();
    private Condition isEmpty = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];

        this.putPosition = 0;
        this.takePosition = 0;
    }

    public void enqueue(T elem) throws InterruptedException {
        lock.lock();

        try {
            while (size == capacity) {
                System.out.println("enqueue waiting: queue is full");
                isFull.await();
            }

            buffer[putPosition] = elem;
            putPosition = nextPosition(putPosition);
            size++;

            isEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T dequeue() throws InterruptedException {
        lock.lock();

        try {
            while (size == 0) {
                System.out.println("dequeue waiting: queue is empty");
                isEmpty.await();
            }

            T elem = (T) buffer[takePosition];
            takePosition = nextPosition(takePosition);
            size--;

            isFull.signalAll();

            return elem;
        } finally {
            lock.unlock();
        }
    }

    private int nextPosition(int pos) {
        if (pos == capacity - 1)
            return 0;

        return pos + 1;
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> bq = new BlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            int nextInt = 0;

            while (true) {
                try {
                    bq.enqueue(nextInt++);
                    System.out.println("enqueued " + nextInt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    Integer res = bq.dequeue();
                    System.out.println("dequeue " + res);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }
}
