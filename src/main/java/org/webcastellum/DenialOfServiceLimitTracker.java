package org.webcastellum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.http.HttpServletRequest;

public final class DenialOfServiceLimitTracker {

//    private static final boolean DEBUG = false;

    
    private final Map/*<DenialOfServiceLimitDefinition,Map<String,Counter>>*/ denialOfServiceCounter = Collections.synchronizedMap(new HashMap());
    private final AttackHandler attackHandler;

// NOTE: The inner content of DenialOfServiceLimitDefinitionContainer object "limitContainer" might change when it reloads the rules, so always use the object and don't cache any content of that object here
//    private final DenialOfServiceLimitDefinitionContainer limitContainer;
//    private final Map/*<DenialOfServiceLimitDefinition,Map<String,Counter>>*/ denialOfServiceCounter = new HashMap();
    
    private Timer cleanupTimer;
    private TimerTask task;
    
    
    public DenialOfServiceLimitTracker(final AttackHandler attackHandler/*, final DenialOfServiceLimitDefinitionContainer limitContainer*/, final long cleanupIntervalMillis) {
        if (attackHandler == null) throw new NullPointerException("attackHandler must not be null");
        this.attackHandler = attackHandler;
        initTimers(cleanupIntervalMillis);
    }
    
    
    private void initTimers(final long cleanupIntervalMillis) {
        this.cleanupTimer = new Timer(/*"DenialOfServiceLimitTracker-cleanup", */true); // TODO Java5: name parameter in Time constructor is only available in Java5
        this.task = new CleanupTrackingCounterTask("DenialOfServiceLimitTracker", this.denialOfServiceCounter);
        this.cleanupTimer.scheduleAtFixedRate(task, CryptoUtils.generateRandomNumber(false,60000,300000), cleanupIntervalMillis);
    }
    
    
    public void destroy() {
        this.denialOfServiceCounter.clear();
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
            this.cleanupTimer = null;
            this.denialOfServiceCounter.clear();
        }
    }
    
    
    public void trackDenialOfServiceRequest(final String ip, final DenialOfServiceLimitDefinition definition, final HttpServletRequest request) {
        if (/*!this.denialOfServiceCounter.isEmpty() &&*/ this.cleanupTimer != null && definition != null) {
            synchronized (this.denialOfServiceCounter) {
                
                // first level of mapping: DoS-definition --> client2Counter-map
                Map/*<String,Counter>*/ client2CounterMap = (Map) this.denialOfServiceCounter.get(definition);
                if (client2CounterMap == null) {
                    client2CounterMap = new HashMap/*<String,Counter>*/(); // not using "Collections.synchronizedMap" here, since we're already locking on the master object "this.denialOfServiceCounter" which is enough
                    this.denialOfServiceCounter.put(definition, client2CounterMap);
                }
                assert client2CounterMap != null;
                
                // second level of mapping: Client --> Counter
                Counter counter = (Counter) client2CounterMap.get(ip);
                if (counter == null) {
                    counter = new TrackingCounter( definition.getWatchPeriodMillis() );
                    client2CounterMap.put(ip, counter);
                } else {
                    counter.setResetPeriodMillis( definition.getWatchPeriodMillis() ); // = let changes to a rule's watch-period take effect immediately
                    counter.increment(); // = overaged will automatically be reset and reused (i.e. starting again at 1)
                }
                assert counter != null;
                
                // check if limit exceeded
//                if (DEBUG) System.out.println("Current DoS limit map: "+this.denialOfServiceCounter);
                if (counter.getCounter() > definition.getClientDenialOfServiceLimit()) {
                    client2CounterMap.remove(ip);
                    this.attackHandler.handleAttack(request, ip, "Denial-of-Service limit exceeded: "+definition);
                }
                
            }
        }
    }
            
}



