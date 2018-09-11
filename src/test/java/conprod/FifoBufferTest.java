package conprod;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class FifoBufferTest {

    @Test
    public void put() {
        FifoBuffer<Integer> buffer = new FifoBuffer<Integer>(5);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        Assert.assertEquals(0, buffer.tail);
        Assert.assertEquals(3, buffer.head);
        Assert.assertArrayEquals(new Object[] { 1, 2, 3, null, null } , buffer.buffer);
    }

    @Test
    public void get() {
        FifoBuffer<Integer> buffer = new FifoBuffer<Integer>(5);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        int res = buffer.get();

        Assert.assertEquals(1, buffer.tail);
        Assert.assertEquals(3, buffer.head);
        Assert.assertArrayEquals(new Object[] { null, 2, 3, null, null } , buffer.buffer);
    }

    @Test
    public void complex() {
        FifoBuffer<Integer> buffer = new FifoBuffer<Integer>(3);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        int one = buffer.get();
        Assert.assertArrayEquals(new Object[] { null, 2, 3 } , buffer.buffer);

        int two = buffer.get();
        Assert.assertArrayEquals(new Object[] { null, null, 3 } , buffer.buffer);

        buffer.put(4);
        Assert.assertArrayEquals(new Object[] { 4, null, 3 } , buffer.buffer);

        int three = buffer.get();
        Assert.assertArrayEquals(new Object[] { 4, null, null } , buffer.buffer);

        int four = buffer.get();
        Assert.assertArrayEquals(new Object[] { null, null, null } , buffer.buffer);

        Assert.assertArrayEquals(new int[] { 1, 2, 3, 4 }, new int[] { one, two, three, four });
    }

    @Test
    public void isFull() {
        FifoBuffer<Integer> buffer = new FifoBuffer<Integer>(1);
        boolean full = buffer.isFull();

        Assert.assertFalse(full);
    }
}