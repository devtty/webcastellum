package org.webcastellum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ClientBlacklist {
    
//    private static final boolean DEBUG = false;
    
    /*
      * specifies the duration of a client blockade
      */
//    private static final long BLOCKED_PERIOD_MILLIS = 30*60*1000;
    
//    private static final long CLEANUP_INTERVAL_MILLIS = 8*60*1000;

        
    private final Map/*<String,Long>*/ blockedClients = Collections.synchronizedMap(new HashMap());

    private final long blockPeriodMillis;
    
    private Timer cleanupTimer;
    private TimerTask task;
    
    
    public ClientBlacklist(final long cleanupIntervalMillis, final long blockPeriodMillis) {
        this.blockPeriodMillis = blockPeriodMillis;
        initTimers(cleanupIntervalMillis);
    }
    
    private void initTimers(final long cleanupIntervalMillis) {
        this.cleanupTimer = new Timer(/*"ClientBlacklist-cleanup", */true); // TODO Java5: name parameter in Time constructor is only available in Java5
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
            final Long blockedUntilMillis = (Long) this.blockedClients.get(ip);
            if (blockedUntilMillis == null) return false;
            if (System.currentTimeMillis() < blockedUntilMillis.longValue()) {
//                if (DEBUG) System.out.println("Client is still blacklisted: "+ip);
                return true;
            }
            // OK, blocking is over, so remove the blockade
            this.blockedClients.remove(ip);
//            if (DEBUG) System.out.println("Client is now un-blacklisted: "+ip);
            return false;
        }
    }

    public void blacklistClient(final String ip) {
        if (this.cleanupTimer != null) {
            final Long blockedUntilMillis = new Long( System.currentTimeMillis() + this.blockPeriodMillis );
            this.blockedClients.put(ip, blockedUntilMillis);
//            if (DEBUG) System.out.println("Client is now blacklisted: "+ip);
        }
    }
    
}
