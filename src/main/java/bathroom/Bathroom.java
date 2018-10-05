package bathroom;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <b>Unisex Bathroom Problem</b>
 * <p>
 * A synchronization practice problem requiring us to synchronize the usage of a single bathroom by both the genders.
 * </p>
 * A bathroom is being designed for the use of both males and females in an office but requires the following constraints to be maintained:
 * <ul>
 *      <li>There cannot be men and women in the bathroom at the same time.</li>
 *      <li>There should never be more than three employees in the bathroom simultaneously.</li>
 * </ul>
 */
public class Bathroom {

    private Person.Gender usedBy;

    private final int maxPersons;
    private int numPersons;

    private Object lock = new Object();

    public Bathroom(int maxPersons) {
        this.maxPersons = maxPersons;
    }

    public void enter(Person person) throws InterruptedException {
        synchronized (lock) {
            Person.Gender gender = person.getGender();
            while (usedBy == gender.getOpposite() || numPersons >= maxPersons) {
                lock.wait();
            }

            usedBy = gender;
            numPersons++;
        }

        use(person);

        synchronized (lock) {
            numPersons--;

            if (numPersons == 0)
                usedBy = null;

            lock.notifyAll();
        }
    }

    private void use(Person person) {
        System.out.println(String.format("%s using bathroom", person));

        Random random = new Random();
        int i = random.nextInt(1000);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("%s used bath for %dms", person, i));
    }

    public static void main(String[] args) throws InterruptedException {
        Bathroom bathroom = new Bathroom(3);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; ; i++) {
            final int p = i;

            executorService.submit(() -> {
                Person.Gender gender = new Random().nextBoolean() ? Person.Gender.M : Person.Gender.F;
                Person person = new Person(String.valueOf(p), gender);

                try {
                    bathroom.enter(person);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            Random random = new Random();
            Thread.sleep(random.nextInt(1000));
        }
    }
}

