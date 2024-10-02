package org.webcastellum;

import org.junit.Test;
import static org.junit.Assert.*;

public class ClientBlacklistTest {
    
    @Test
    public void testIsBlacklisted() throws InterruptedException {
        String ip = "127.0.0.1";
        ClientBlacklist instance = new ClientBlacklist(200L, 500L);
        assertFalse(instance.isBlacklisted(ip));
        instance.blacklistClient(ip);
        assertTrue(instance.isBlacklisted(ip));
        instance.blacklistClient(ip);
        assertTrue(instance.isBlacklisted(ip));
        Thread.sleep(700L);
        assertFalse(instance.isBlacklisted(ip));
    }
    
}
