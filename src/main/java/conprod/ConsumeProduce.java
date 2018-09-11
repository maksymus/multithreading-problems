package conprod;

import java.util.Random;

public class ConsumeProduce {
    public static void main(String[] args) throws InterruptedException {
        Buffer<Integer> buffer = new FifoBuffer<>(3);

        Random random = new Random();

        Producer producer = new Producer(buffer, () -> random.nextInt(1000));
        Consumer consumer = new Consumer(buffer);

        Thread producerThread = new Thread(() -> {
            try {
                producer.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                consumer.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producerThread.start();
        consumerThread.start();

        consumerThread.join();
        producerThread.join();
    }
}

