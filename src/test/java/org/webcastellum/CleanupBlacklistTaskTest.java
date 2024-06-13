package org.webcastellum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class CleanupBlacklistTaskTest {

    public CleanupBlacklistTaskTest() {

    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithoutName(){
        new CleanupBlacklistTask(null, new HashMap());
    }
    
    @Test(expected = NullPointerException.class)
    public void testConstructorWithoutMap(){
        new CleanupBlacklistTask("test", null);
    }
    
    @Test
    public void testTimer() throws InterruptedException {

        LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        
        Map<String, Long> blacklist = new HashMap();
        blacklist.put("168.192.178.1", System.currentTimeMillis());
        blacklist.put("168.192.178.2", System.currentTimeMillis());
        blacklist.put("168.192.178.3", System.currentTimeMillis() + 5000L);

        TimerTask task = new CleanupBlacklistTask("test", blacklist);        
        
        new Timer().schedule(task, twoSecondsLaterAsDate);
        
        while(LocalDateTime.now().isBefore(twoSecondsLater)){
            assertTrue(blacklist.size()==3);
        }
        
        System.out.println("sadf" + blacklist.size());
        assertTrue(blacklist.size()==1);
        assertTrue(blacklist.containsKey("168.192.178.3"));
    }

}
