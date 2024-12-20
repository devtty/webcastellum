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
import org.junit.Ignore;
import org.junit.Test;

public class CleanupTrackingCounterTaskTest {

    @Test
    public void testConstructorWithoutName(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupTrackingCounterTask(null, new HashMap()));
        assertEquals("name must not be null", npe.getMessage());
    }
    
    @Test
    public void testConstructorWithoutMap(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupTrackingCounterTask("test", null));
        assertEquals("map must not be null", npe.getMessage());
    }
    
    @Test
    @Ignore("wrong implementation")
    public void testTimer() throws InterruptedException{
        LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(12);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        
        Map counters = new HashMap();
        TrackingCounter t = new TrackingCounter(1000L);
        t.increment();
        counters.put("test-1", t);
        counters.put("test-2", new TrackingCounter(1000L));
        counters.put("test-3", new TrackingCounter(5000L));
        TimerTask task = new CleanupTrackingCounterTask("test", counters);
        
        new Timer().schedule(task, twoSecondsLaterAsDate);
     
        while(LocalDateTime.now().isBefore(twoSecondsLater)){
            assertEquals(3, counters.size());
            Thread.sleep(500L);
        }
        
        assertEquals(1, counters.size());
        assertTrue(counters.containsKey("test-3"));
    }
}
