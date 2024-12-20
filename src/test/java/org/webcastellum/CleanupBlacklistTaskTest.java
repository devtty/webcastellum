package org.webcastellum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.junit.Test;
import static org.junit.Assert.*;

public class CleanupBlacklistTaskTest {

    public CleanupBlacklistTaskTest() {

    }

    @Test
    public void testConstructorWithoutName(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupBlacklistTask(null, new HashMap()));
        assertEquals("name must not be null", npe.getMessage());
    }
    
    @Test
    public void testConstructorWithoutMap(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new CleanupBlacklistTask("test", null));
        assertEquals("map must not be null", npe.getMessage());
    }
    
    @Test
    public void testTimer() throws InterruptedException{

        LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        
        Map<String, Long> blacklist = new HashMap();
        blacklist.put("168.192.178.1", System.currentTimeMillis());
        blacklist.put("168.192.178.2", System.currentTimeMillis());
        blacklist.put("168.192.178.3", System.currentTimeMillis() + 5000L);

        TimerTask task = new CleanupBlacklistTask("test", blacklist);        
        
        new Timer().schedule(task, twoSecondsLaterAsDate);
        
        while(LocalDateTime.now().isBefore(twoSecondsLater)){
            assertEquals(3,blacklist.size());
            Thread.sleep(500);
        }
     
        assertEquals(1,blacklist.size());
        assertTrue(blacklist.containsKey("168.192.178.3"));
    }

}
