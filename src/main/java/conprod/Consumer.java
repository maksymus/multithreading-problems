package conprod;

public class Consumer<T> {
    private Buffer<T> buffer;

    public Consumer(Buffer<T> buffer) {
        this.buffer = buffer;
    }

    public void consume() throws InterruptedException {
        while (true) {
            synchronized (buffer) {
                while (buffer.isEmpty()) {
                    buffer.wait();
                    System.out.println("waiting to consume");
                }

                T t = buffer.get();
                System.out.println(t);

                buffer.notifyAll();
            }
        }
    }
}
