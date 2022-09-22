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



public final class SessionCreationTracker {
    
    private static final Logger LOGGER = Logger.getLogger(SessionCreationTracker.class.getName());

    /**
     * Used to identify myself in order to ignore broadcasts sent from me (for the JMS-based clustring support)
     */
    private static final String SYSTEM_IDENTIFIER_OF_THIS_BOX = IdGeneratorUtils.createId();
    private static final String TYPE = "SessionCreationTracker";
    
    private final String clusterInitialContextFactory;
    private final String clusterJmsProviderUrl;
    private final String clusterJmsConnectionFactory;
    private final String clusterJmsTopic;
    private final Map<String,Counter> sessionCreationCounter = Collections.synchronizedMap(new HashMap<>());
    private final AttackHandler attackHandler;
    private final int sessionCreationAttackThreshold;
    
    private final long resetPeriodMillis;
    
    private Timer cleanupTimer;
    private Timer clusterPublishTimer;
    private TimerTask cleanupTask;
    private TimerTask clusterPublishTask;
    private SnapshotBroadcastListener broadcastListener;
    
    
    public SessionCreationTracker(final AttackHandler attackHandler, final int sessionCreationAttackThreshold, final long cleanupIntervalMillis, final long resetPeriodMillis, final long clusterPublishPeriodMillis,
            final String clusterInitialContextFactory, final String clusterJmsProviderUrl, final String clusterJmsConnectionFactory, final String clusterJmsTopic) {
        if (attackHandler == null) throw new NullPointerException("attackHandler must not be null");
        if (sessionCreationAttackThreshold < 0) throw new IllegalArgumentException("sessionCreationAttackThreshold must not be negative");
        this.attackHandler = attackHandler;
        this.sessionCreationAttackThreshold = sessionCreationAttackThreshold;
        this.resetPeriodMillis = resetPeriodMillis;

        this.clusterInitialContextFactory = clusterInitialContextFactory;
        this.clusterJmsProviderUrl = clusterJmsProviderUrl;
        this.clusterJmsConnectionFactory = clusterJmsConnectionFactory;
        this.clusterJmsTopic = clusterJmsTopic;

        initTimers(cleanupIntervalMillis, clusterPublishPeriodMillis);
    }
    
    
    private void initTimers(final long cleanupIntervalMillis, final long clusterPublishPeriodMillis) {
        if (this.sessionCreationAttackThreshold > 0) { // 0 = disabled
            this.cleanupTimer = new Timer("SessionCreationTracker-cleanup", true);
            this.cleanupTask = new CleanupIncrementingCounterTask("SessionCreationTracker",this.sessionCreationCounter);
            this.cleanupTimer.scheduleAtFixedRate(cleanupTask, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
            // cluster publish timer (but only if enabled: 0 means disabled)
            if (clusterPublishPeriodMillis > 0) {
                // subscribing stuff
                this.broadcastListener = new ClusterSubscribeIncrementingCounterClient(TYPE, SYSTEM_IDENTIFIER_OF_THIS_BOX, this.sessionCreationCounter);
                JmsUtils.addSnapshotBroadcastListener(TYPE, broadcastListener);
                // publishing stuff
                this.clusterPublishTimer = new Timer("HttpStatusCodeTracker-clusterPublish", true);
                this.clusterPublishTask = new ClusterPublishIncrementingCounterTask(TYPE, SYSTEM_IDENTIFIER_OF_THIS_BOX, this.clusterInitialContextFactory, this.clusterJmsProviderUrl, this.clusterJmsConnectionFactory, this.clusterJmsTopic, this.sessionCreationCounter);
                this.clusterPublishTimer.scheduleAtFixedRate(this.clusterPublishTask, CryptoUtils.generateRandomNumber(false,30000,120000), clusterPublishPeriodMillis);
            }
        }
    }
    
    
    public void destroy() {
        this.sessionCreationCounter.clear();
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
            this.cleanupTask = null;
        }
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
            this.cleanupTimer = null;
            this.sessionCreationCounter.clear();
        }
        if (this.clusterPublishTask != null) {
            this.clusterPublishTask.cancel();
            this.clusterPublishTask = null;
        }
        if (this.clusterPublishTimer != null) {
            this.clusterPublishTimer.cancel();
            this.clusterPublishTimer = null;
            this.sessionCreationCounter.clear();
        }
        if (this.broadcastListener != null) this.broadcastListener = null;
    }
    
    
    public void trackSessionCreation(final String ip, final HttpServletRequest request) {
        LOGGER.log(Level.FINE, "Session creation: {0}", ip);
        if (this.sessionCreationAttackThreshold > 0 && this.cleanupTimer != null) {
            boolean broadcastRemoval = false;
            try {
                synchronized (this.sessionCreationCounter) {
                    IncrementingCounter counter = (IncrementingCounter) this.sessionCreationCounter.get(ip);
                    if (counter == null) {
                        counter = new IncrementingCounter(this.resetPeriodMillis);
                        this.sessionCreationCounter.put(ip, counter);
                    } else counter.increment(); // = overaged will automatically be reset and reused (i.e. starting again at 1)
                    if (counter.getCounter() > this.sessionCreationAttackThreshold) {
                        this.sessionCreationCounter.remove(ip);
                        final String message = "Session creation per-client threshold exceeded ("+this.sessionCreationAttackThreshold+")";
                        this.attackHandler.handleAttack(request, ip, message);
                        // broadcast removal-indicator
                        broadcastRemoval = this.clusterPublishTask != null;// but only if this thing is configured cluster-aware here (not null = we are in cluster-aware mode here)
                        throw new ServerAttackException(message);
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

    
    public void trackSessionInvalidation(final String ip) {
        LOGGER.log(Level.FINE, "Session invalidation: {0}", ip);
        if (this.sessionCreationAttackThreshold > 0 && this.cleanupTimer != null) {
            synchronized (this.sessionCreationCounter) {
                final IncrementingCounter counter = (IncrementingCounter) this.sessionCreationCounter.get(ip);
                if (counter != null) {
                    counter.decrementQuietly();
                }
            }
        }
    }
    
}
