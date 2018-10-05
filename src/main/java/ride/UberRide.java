package ride;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Uber Ride Problem
 *
 * Imagine at the end of a political conference, republicans and democrats are trying to leave the venue and ordering
 * Uber rides at the same time. However, to make sure no fight breaks out in an Uber ride, the software developers at
 * Uber come up with an algorithm whereby either an Uber ride can have all democrats or republicans or two Democrats
 * and two Republicans. All other combinations can result in a fist-fight.
 *
 * Your task as the Uber developer is to model the ride requestors as threads that call the method seated once a right
 * combination is found and then any one of the threads tells the Uber driver to drive. This could be any of the four threads.
 */
public class UberRide {
    public enum Party { DEM, REP }

    private final int maxRiders;

    private int numRepublicans;
    private int numDemocrats;

    private Lock lock = new ReentrantLock();
    private CyclicBarrier cyclicBarrier;

    private Condition driveCondition = lock.newCondition();

    public UberRide() {
        this.maxRiders = 4;
        this.cyclicBarrier = new CyclicBarrier(this.maxRiders);
    }

    public void seat(Party party) throws BrokenBarrierException, InterruptedException {
        lock(lock, () -> {
            int myParty = party == Party.REP ? numRepublicans : numDemocrats;
            int otherParty = party == Party.REP ? numDemocrats : numRepublicans;

            while (!canBeSeated(myParty, otherParty)) {
                try {
                    driveCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (party == Party.REP) {
                numRepublicans++;
            } else {
                numDemocrats++;
            }
        });

        seated();
        cyclicBarrier.await();

        lock(lock, () -> {
            if (numRepublicans != 0 || numDemocrats != 0)
                drive();

            numRepublicans = 0;
            numDemocrats = 0;

            driveCondition.signalAll();
        });
    }

    private void seated() {
        System.out.println(String.format("rider seated %s", Thread.currentThread().getName()));
    }

    private void drive() {
        System.out.println(String.format("Woohoo ... riding to the party {dems: %d, reps: %d}",
                numDemocrats, numRepublicans));
    }

    private boolean canBeSeated(int myParty, int otherParty) {
        // if more then 2 riders from other party then don't sean in car
        // if other party in car and my party has 2 seats then not allowed to seat
        if (otherParty > 2 || (myParty == 2 && otherParty > 0)) {
            return false;
        }

        return true;
    }

    private void lock(Lock lock, Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // 2 democrats and 2 republicans allowed in same car
        // otherwise 4 democrats or 4 republicans
        UberRide uberRide = new UberRide();

        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        while (true) {
            executorService.submit(() -> {
                Party party = new Random().nextBoolean() ? Party.DEM : Party.REP;

                try {
                    uberRide.seat(party);
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            Thread.sleep(random.nextInt(1000));
        }
    }
}
