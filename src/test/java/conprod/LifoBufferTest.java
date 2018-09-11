package conprod;

import org.junit.Assert;
import org.junit.Test;

public class LifoBufferTest {

    @Test
    public void put() {
        LifoBuffer<Integer> buffer = new LifoBuffer<Integer>(5);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        Assert.assertEquals(3, buffer.position);
        Assert.assertArrayEquals(new Object[] { 1, 2, 3, null, null } , buffer.buffer);
    }

    @Test
    public void get() {
        LifoBuffer<Integer> buffer = new LifoBuffer<Integer>(5);
        buffer.put(1);
        buffer.put(2);

        Integer res = buffer.get();
        Assert.assertEquals(2, (int) res);

        Assert.assertArrayEquals(new Object[] { 1, null, null, null, null } , buffer.buffer);
    }
}