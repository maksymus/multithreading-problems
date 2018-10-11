package barber;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Barber Shop
 *
 * A barbershop consists of a waiting room with n chairs, and a barber chair for giving haircuts.
 * If there are no customers to be served, the barber goes to sleep. If a customer enters the barbershop and all chairs
 * are occupied, then the customer leaves the shop. If the barber is busy, but chairs are available, then the customer
 * sits in one of the free chairs. If the barber is asleep, the customer wakes up the barber. Write a program to
 * coordinate the interaction between the barber and the customers.
 */
public class BarberShop {
    private int freeChairs;
    private boolean isAsleep = true;

    private Object lock = new Object();

    public BarberShop(int totalChairs) {
        this.freeChairs = totalChairs;

        new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    while (isAsleep) {
                        System.out.println("no work - barber sleeping");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                makeHaircut();

                synchronized (lock) {
                    if (freeChairs == totalChairs) {
                        isAsleep = true;
                    } else {
                        freeChairs++;
                    }
                }
            }
        }).start();
    }

    public void enter() {
        synchronized (lock) {
            if (freeChairs == 0) {
                System.out.println("no free chairs - customer leaving");
                return;
            }

            System.out.println(String.format("free seats %d - taking free seat", freeChairs));

            freeChairs--;

            if (isAsleep) {
                isAsleep = false;
                lock.notify();
            }
        }
    }

    private void makeHaircut() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        BarberShop barberShop = new BarberShop(3);

        while (true) {
            executorService.submit(() -> barberShop.enter());
            Thread.sleep(random.nextInt(300));
        }
    }
}
