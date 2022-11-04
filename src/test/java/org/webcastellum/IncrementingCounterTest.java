package org.webcastellum;

import org.junit.Test;
import static org.junit.Assert.*;

public class IncrementingCounterTest {
    
    @Test
    public void testClone() throws Exception {
        IncrementingCounter a = new IncrementingCounter(1234567);
        a.increment();
        a.increment();
        a.increment();
        a.resetDelta();
        a.increment();
        a.increment();
        
        assertEquals(2, a.getDelta());
        assertEquals(6, a.getCounter());
        assertEquals(1234567, a.getResetPeriodMillis());
        
        IncrementingCounter b = (IncrementingCounter) a.clone();
        b.increment();
        b.resetDelta();
        b.increment();
        b.setResetPeriodMillis(9876543);
        
        assertEquals(1, b.getDelta());
        assertEquals(8, b.getCounter());
        assertEquals(9876543, b.getResetPeriodMillis());
        
    }

    @Test
    public void testGetDelta() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        assertEquals(1, a.getDelta());
        
        a.increment();
        a.increment();
        a.increment();
        assertEquals(4, a.getDelta());
        
        a.decrementQuietly();
        a.decrementQuietly();
        assertEquals(2, a.getDelta());
        
        a.resetDelta();
        assertEquals(0, a.getDelta());
    }

    @Test
    public void testResetAllOnForeignRemoval() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        
        assertEquals(1, a.getCounter());
        assertEquals(1, a.getDelta());
        assertEquals(1234567, a.getResetPeriodMillis());
        assertFalse(a.isOveraged());
       
        a.resetAllOnForeignRemoval(0L);
        assertEquals(0, a.getCounter());
        assertEquals(0, a.getDelta());
        assertEquals(1234567, a.getResetPeriodMillis());
        assertFalse(a.isOveraged());
    }

    @Test
    public void testMergeWith() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        a.increment();
        
        IncrementingCounter b = new IncrementingCounter(7654321);
        b.resetDelta();
        
        a.mergeWith(b);
        
        assertEquals(2, a.getCounter());
        assertEquals(2, a.getDelta());
        
        b.increment();
        b.increment();
        
        a.mergeWith(b);
        assertEquals(4, a.getCounter());
        assertEquals(2, a.getDelta());
        
        IncrementingCounter overaged = new IncrementingCounter(-1);
        
        a.mergeWith(overaged);
        assertEquals(5, a.getCounter());
        assertEquals(2, a.getDelta());
    }

    @Test
    public void testDecrementQuietly() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        a.increment();
        assertEquals(2, a.getCounter());
        assertEquals(2, a.getDelta());
        
        a.decrementQuietly();
        assertEquals(1, a.getCounter());
        assertEquals(1, a.getDelta());
        
        a.decrementQuietly();
        assertEquals(0, a.getCounter());
        assertEquals(0, a.getDelta());
        
        a.decrementQuietly();
        assertEquals(0, a.getCounter());
        assertEquals(0, a.getDelta());
    }

    @Test
    public void testIsOveraged() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        a.increment();
        assertEquals(2, a.getCounter());
        assertEquals(2, a.getDelta());
        
        IncrementingCounter overaged = new IncrementingCounter(-1);
        overaged.increment();
        assertEquals(1, overaged.getCounter());
        assertEquals(1, overaged.getDelta());
    }

    @Test
    public void testToString() {
        IncrementingCounter a = new IncrementingCounter(1234567);
        assertEquals("counter:1(1)1234567", a.toString());
    }
    
}
