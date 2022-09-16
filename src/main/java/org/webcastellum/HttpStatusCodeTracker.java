package org.webcastellum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public final class HttpStatusCodeTracker {

    private static final boolean DEBUG = false;

    
    /**
     * Used to identify myself in order to ignore broadcasts sent from me (for the JMS-based clustring support)
     */
    private static final String SYSTEM_IDENTIFIER_OF_THIS_BOX = IdGeneratorUtils.createId();
    private static final String TYPE = "HttpStatusCodeTracker";

    
    
    private final String clusterInitialContextFactory;
    private final String clusterJmsProviderUrl;
    private final String clusterJmsConnectionFactory;
    private final String clusterJmsTopic;
    private final Map<String,IncrementingCounter> httpInvalidRequestOrNotFoundCounter = Collections.synchronizedMap(new HashMap<>());
    private final AttackHandler attackHandler;
    private final int httpInvalidRequestOrNotFoundAttackThreshold;
    
    private final long resetPeriodMillis;
    
    private Timer cleanupTimer;
    private Timer clusterPublishTimer;
    private TimerTask cleanupTask;
    private TimerTask clusterPublishTask;
    private SnapshotBroadcastListener broadcastListener;
    
    
    public HttpStatusCodeTracker(final AttackHandler attackHandler, final int httpInvalidRequestOrNotFoundAttackThreshold, final long cleanupIntervalMillis, final long resetPeriodMillis, final long clusterPublishPeriodMillis,
            final String clusterInitialContextFactory, final String clusterJmsProviderUrl, final String clusterJmsConnectionFactory, final String clusterJmsTopic) {
        if (attackHandler == null) throw new NullPointerException("attackHandler must not be null");
        if (httpInvalidRequestOrNotFoundAttackThreshold < 0) throw new IllegalArgumentException("httpInvalidRequestOrNotFoundAttackThreshold must not be negative");
        this.attackHandler = attackHandler;
        this.httpInvalidRequestOrNotFoundAttackThreshold = httpInvalidRequestOrNotFoundAttackThreshold;
        this.resetPeriodMillis = resetPeriodMillis;

        this.clusterInitialContextFactory = clusterInitialContextFactory;
        this.clusterJmsProviderUrl = clusterJmsProviderUrl;
        this.clusterJmsConnectionFactory = clusterJmsConnectionFactory;
        this.clusterJmsTopic = clusterJmsTopic;

        initTimers(cleanupIntervalMillis, clusterPublishPeriodMillis);
    }
    
    
    private void initTimers(final long cleanupIntervalMillis, final long clusterPublishPeriodMillis) {
        if (this.httpInvalidRequestOrNotFoundAttackThreshold > 0) { // 0 means disabled
            // cleanup timer
            this.cleanupTimer = new Timer("HttpStatusCodeTracker-cleanup", true);
            this.cleanupTask = new CleanupIncrementingCounterTask(TYPE,this.httpInvalidRequestOrNotFoundCounter);
            this.cleanupTimer.scheduleAtFixedRate(this.cleanupTask, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
            // cluster publish timer (but only if enabled: 0 means disabled)
            if (clusterPublishPeriodMillis > 0) {
                // subscribing stuff
                this.broadcastListener = new ClusterSubscribeIncrementingCounterClient(TYPE, SYSTEM_IDENTIFIER_OF_THIS_BOX, this.httpInvalidRequestOrNotFoundCounter);
                JmsUtils.addSnapshotBroadcastListener(TYPE, broadcastListener);
                // publishing stuff
                this.clusterPublishTimer = new Timer("HttpStatusCodeTracker-clusterPublish", true);
                this.clusterPublishTask = new ClusterPublishIncrementingCounterTask(TYPE, SYSTEM_IDENTIFIER_OF_THIS_BOX, this.clusterInitialContextFactory, this.clusterJmsProviderUrl, this.clusterJmsConnectionFactory, this.clusterJmsTopic, this.httpInvalidRequestOrNotFoundCounter);
                this.clusterPublishTimer.scheduleAtFixedRate(this.clusterPublishTask, CryptoUtils.generateRandomNumber(false,30000,120000), clusterPublishPeriodMillis);
            }
        }
    }
    
    
    public void destroy() {
        this.httpInvalidRequestOrNotFoundCounter.clear();
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
            this.cleanupTask = null;
        }
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
            this.cleanupTimer = null;
            this.httpInvalidRequestOrNotFoundCounter.clear();
        }
        if (this.clusterPublishTask != null) {
            this.clusterPublishTask.cancel();
            this.clusterPublishTask = null;
        }
        if (this.clusterPublishTimer != null) {
            this.clusterPublishTimer.cancel();
            this.clusterPublishTimer = null;
            this.httpInvalidRequestOrNotFoundCounter.clear();
        }
        if (this.broadcastListener != null) this.broadcastListener = null;
    }
    
    
    public void trackStatusCode(final String ip, final int statusCode, final HttpServletRequest request) {
        if (this.httpInvalidRequestOrNotFoundAttackThreshold > 0 && this.cleanupTimer != null) {
            if (statusCode == HttpServletResponse.SC_BAD_REQUEST || statusCode == HttpServletResponse.SC_NOT_FOUND) {
                boolean broadcastRemoval = false;
                try {
                    synchronized (this.httpInvalidRequestOrNotFoundCounter) {
                        IncrementingCounter counter = (IncrementingCounter) this.httpInvalidRequestOrNotFoundCounter.get(ip);
                        if (counter == null) {
                            counter = new IncrementingCounter(this.resetPeriodMillis);
                            this.httpInvalidRequestOrNotFoundCounter.put(ip, counter);
                        } else counter.increment(); // = overaged will automatically be reset and reused (i.e. starting again at 1)
                        Logger.getLogger(HttpStatusCodeTracker.class.getName()).log(Level.FINE, "Current HTTP 400/404 status map: {0}", this.httpInvalidRequestOrNotFoundCounter);
                        if (counter.getCounter() > this.httpInvalidRequestOrNotFoundAttackThreshold) {
                            this.httpInvalidRequestOrNotFoundCounter.remove(ip);
                            this.attackHandler.handleAttack(request, ip, "HTTP 400/404 per-client threshold exceeded ("+this.httpInvalidRequestOrNotFoundAttackThreshold+")");
                            // broadcast removal-indicator
                            broadcastRemoval = this.clusterPublishTask != null;// but only if this thing is configured cluster-aware here (not null = we are in cluster-aware mode here)
                        }
                    }
                } finally {
                    if (broadcastRemoval) {
                        final List<String> removals = new ArrayList<>(1);
                        removals.add(ip);
                        JmsUtils.publishSnapshot( new Snapshot(TYPE, SYSTEM_IDENTIFIER_OF_THIS_BOX, removals) );
                    }
                }
            }
        }
    }
            
}
