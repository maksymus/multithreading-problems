package conprod;

public class FifoBuffer<T> implements Buffer<T> {
    private final int capacity;

    Object[] buffer;

    int size;
    int head;
    int tail;

    public FifoBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];

        this.head = 0;
        this.tail = 0;
    }

    @Override
    public void put(T item) {
        if (isFull())
            throw new RuntimeException("overflow");

        buffer[head] = item;
        head = nextPosition(head);
        size++;
    }

    @Override
    public T get() {
        if (isEmpty())
            throw new RuntimeException("underflow");

        Object elem = buffer[tail];
        buffer[tail] = null;
        tail = nextPosition(tail);
        size--;

        return (T) elem;
    }

    @Override
    public boolean isFull() {
        return size == capacity;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private int nextPosition(int pos) {
        if (pos == capacity - 1)
            return 0;

        return pos + 1;
    }

//    private int prevPosition(int pos) {
//        if (pos == 0)
//            return capacity - 1;
//
//        return pos - 1;
//    }
}
