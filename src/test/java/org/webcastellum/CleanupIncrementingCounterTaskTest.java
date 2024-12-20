package org.webcastellum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CleanupIncrementingCounterTaskTest {
    
    @Test
    public void testConstructorWithoutName(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupIncrementingCounterTask(null, new HashMap()));
        assertEquals("name must not be null", npe.getMessage());
    }
    
    @Test
    public void testConstructorWithoutMap(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupIncrementingCounterTask("test", null));
        assertEquals("map must not be null", npe.getMessage());
    }
    
    @Test
    public void testTimer() throws InterruptedException{
        LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        
        Map counters = new HashMap();
        counters.put("test-1", new IncrementingCounter(1000L));
        counters.put("test-2", new IncrementingCounter(1000L));
        counters.put("test-3", new IncrementingCounter(5000L));
        TimerTask task = new CleanupIncrementingCounterTask("test", counters);
        
        new Timer().schedule(task, twoSecondsLaterAsDate);
         
        while(LocalDateTime.now().isBefore(twoSecondsLater)){
            assertEquals(3, counters.size());
            Thread.sleep(500L);
        }
        
        assertEquals(1, counters.size());
        assertTrue(counters.containsKey("test-3"));
    }
}
