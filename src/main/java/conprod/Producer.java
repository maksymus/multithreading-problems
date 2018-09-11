package conprod;

import java.util.function.Supplier;

public class Producer<T> {
    private final Buffer<T> buffer;
    private final Supplier<T> elementFactory;

    public Producer(Buffer<T> buffer, Supplier<T> elementFactory) {
        this.buffer = buffer;
        this.elementFactory = elementFactory;
    }

    public void produce() throws InterruptedException {
        while (true) {
            synchronized (buffer) {
                while (buffer.isFull()) {
                    buffer.wait();
                    System.out.println("waiting to produce");
                }

                buffer.put(elementFactory.get());
                buffer.notifyAll();
            }
        }
    }
}
