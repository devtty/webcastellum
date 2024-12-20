package org.webcastellum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientBlacklist {
    
    private static final Logger LOGGER = Logger.getLogger(ClientBlacklist.class.getName());
    
    /*
      * specifies the duration of a client blockade
      */
//    private static final long BLOCKED_PERIOD_MILLIS = 30*60*1000;
    
//    private static final long CLEANUP_INTERVAL_MILLIS = 8*60*1000;

        
    private final Map<String,Long> blockedClients;

    private final long blockPeriodMillis;
    
    private Timer cleanupTimer;
    private TimerTask task;
    
    
    public ClientBlacklist(final long cleanupIntervalMillis, final long blockPeriodMillis) {
        this.blockedClients = Collections.synchronizedMap(new HashMap<>());
        this.blockPeriodMillis = blockPeriodMillis;
        initTimers(cleanupIntervalMillis);
    }
    
    private void initTimers(final long cleanupIntervalMillis) {
        this.cleanupTimer = new Timer("ClientBlacklist-cleanup", true);
        this.task = new CleanupBlacklistTask("ClientBlacklist",this.blockedClients);
        this.cleanupTimer.scheduleAtFixedRate(task, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
    }
    
    public void destroy() {
        this.blockedClients.clear();
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
            this.cleanupTimer = null;
            this.blockedClients.clear();
        }
    }
    
    public boolean isBlacklisted(final String ip) {
        if (!this.blockedClients.containsKey(ip)) return false;
        synchronized (this.blockedClients) {
            final Long blockedUntilMillis = this.blockedClients.get(ip);
            if (blockedUntilMillis == null) return false;
            if (System.currentTimeMillis() < blockedUntilMillis) {
                LOGGER.log(Level.FINE, "Client is still blacklisted: {0}", ip);
                return true;
            }
            // OK, blocking is over, so remove the blockade
            this.blockedClients.remove(ip);
            LOGGER.log(Level.FINE, "Client is now un-blacklisted: {0}", ip);
            return false;
        }
    }

    public void blacklistClient(final String ip) {
        if (this.cleanupTimer != null) {
            final Long blockedUntilMillis = System.currentTimeMillis() + this.blockPeriodMillis;
            this.blockedClients.put(ip, blockedUntilMillis);
            LOGGER.log(Level.FINE, "Client is now blacklisted: {0}", ip);
        }
    }
    
}
