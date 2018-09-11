package conprod;

public interface Buffer<T> {
    void put(T item);

    T get();

    boolean isFull();

    boolean isEmpty();
}
