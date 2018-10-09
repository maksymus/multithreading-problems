package philosophers;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Imagine you have five philosopher's sitting on a roundtable. The philosopher's do only two kinds of activities.
 * One they contemplate, and two they eat. However, they only have five forks between themselves to eat their food with.
 * Each philosopher requires both the fork to his left and the fork to his right to eat his food.
 *
 * The arrangement of the philosophers and the forks are shown in the diagram.
 *
 * Design a solution where each philosopher gets a chance to eat his food without causing a deadlock
 */
public class DiningPhilosophers {

    private static class Philosopher {
        private final Random random = new Random();

        private final String name;
        private final Fork leftFork;
        private final Fork rightFork;
        private final Semaphore globalSemaphore;

        public Philosopher(String name, Fork leftFork, Fork rightFork, Semaphore globalSemaphore) {
            this.name = name;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.globalSemaphore = globalSemaphore;
        }

        public void start() {
            try {
                while (true) {
                    eat();
                    think();
                }
            } catch (Exception e) {
                System.out.println(String.format("%s is leaving the table: %s", name, e.getMessage()));
            }
        }

        private void think() throws InterruptedException {
            Thread.sleep(random.nextInt(100));
        }

        private void eat() throws InterruptedException {
            globalSemaphore.acquire();

            leftFork.take();
            Thread.sleep(random.nextInt(100));
            rightFork.take();
            System.out.println(String.format("%s is eating", name));
            Thread.sleep(random.nextInt(100));
            leftFork.put();
            rightFork.put();

            globalSemaphore.release();
        }
    }

    private static class Fork {
        private String name;
        private Semaphore semaphore = new Semaphore(1);

        public Fork(String name) {
            this.name = name;
        }

        public void take() throws InterruptedException {
            boolean acquired = semaphore.tryAcquire(1000, TimeUnit.MILLISECONDS);
            if (!acquired)
                throw new RuntimeException("failed to get fork " + name);
        }

        public void put() {
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        int numPhilosophers = 5;

        // only 4 allowed to eat at any point of time
        Semaphore globalSemaphore = new Semaphore(numPhilosophers - 1);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Fork[] forks = new Fork[numPhilosophers];
        Philosopher[] philosophers = new Philosopher[numPhilosophers];

        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Fork("fork" + i);
        }

        for (int i = 0; i < numPhilosophers; i++) {
            philosophers[i] = new Philosopher("phil" + i, forks[i], forks[(i + 1) % numPhilosophers], globalSemaphore);
        }

        Arrays.stream(philosophers).forEach(ph -> executorService.submit(() -> ph.start()));

        executorService.shutdown();
    }
}
