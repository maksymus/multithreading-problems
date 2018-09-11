package conprod;

public class LifoBuffer<T> implements Buffer<T> {
    private final int capacity;

    int position;
    Object[] buffer;

    public LifoBuffer(int capacity) {
        this.position = 0;
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    @Override
    public void put(T item) {
        if (isFull())
            throw new RuntimeException("overflow");

        buffer[position++] = item;
    }

    @Override
    public T get() {
        if (isEmpty())
            throw new RuntimeException("underflow");

        T elem = (T) buffer[--position];
        buffer[position] = null;
        return elem;
    }

    @Override
    public boolean isFull() {
        return position >= capacity;
    }

    @Override
    public boolean isEmpty() {
        return position == 0;
    }
}
