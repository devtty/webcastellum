package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;


public final class CleanupTrackingCounterTask extends TimerTask {
    //private final static boolean DEBUG = false;
    
    private final String name;
    private final Map/*<DenialOfServiceLimitDefinition,Map<String,Counter>>*/ map;

    public CleanupTrackingCounterTask(final String name, final Map/*<DenialOfServiceLimitDefinition,Map<String,Counter>>*/ map) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.name = name;
        this.map = map;
    }
    
    public void run() {
        int loosers = 0;
        synchronized (this.map) { 
            for (final Iterator definitions = this.map.entrySet().iterator(); definitions.hasNext();) {
                final Map.Entry definition = (Map.Entry) definitions.next();
                final Map/*<String,Counter>*/ client2CounterMap = (Map) definition.getValue();

                // remove overaged counters
                if (!client2CounterMap.isEmpty()) for (final Iterator entries = client2CounterMap.entrySet().iterator(); entries.hasNext();) {
                    final Map.Entry entry = (Map.Entry) entries.next();
                    final TrackingCounter counter = (TrackingCounter) entry.getValue();
                    if (counter != null && counter.isOveraged()) {
                        entries.remove();
                        loosers++;
                    }
                }
                
                // remove this whole map if it is empty
                if (client2CounterMap.isEmpty()) definitions.remove();

            }
        }
//        if (DEBUG) System.out.println("*** CLEANUP: "+loosers+" overaged counters removed (from "+this.name+") ***");
    }
    
    
    
    
    

    // just for local performance testing: @@@@@@@@@@@@@@@@@@@@@@@@@
    /*
    public static final void main(String[] args) throws InterruptedException {
        final Map map = new HashMap(); // <DenialOfServiceLimitDefinition,Map<String,Counter>>
        for (int i=0; i<25; i++) { // = number simulated of DoS definitions
            final DenialOfServiceLimitDefinition definition = new DenialOfServiceLimitDefinition(true, "TEST-"+i, "Just testing", Pattern.compile("."), false);
            definition.setWatchPeriodSeconds(30);
            definition.setClientDenialOfServiceLimit(350);
            final Map client2Counters = new HashMap(); //<String,Counter>
            for (int j=0; j<500; j++) { // = number of simulated parallel clients (users)
                final Counter counter = new TrackingCounter( definition.getWatchPeriodMillis() );
                for (int c=0; c<400; c++) { // = number of simulated requests for each client
                    if (c % 50 == 0) Thread.sleep(2); // = simulate user think time
                    counter.increment();
                }
                client2Counters.put("IP-OF-CLIENT-"+j, counter);
            }
            map.put(definition, client2Counters);
        }
        
        // Now test the cleaning
        System.out.println("Adding some delay to let counters overage");
        final CleanupLimitTrackerTask cleaner = new CleanupLimitTrackerTask("Test", map);
        Thread.sleep(60000); // = add some long delay here to make the counters overage a lot
        System.out.println("Beginning to clean");
        final long start = System.currentTimeMillis();
        cleaner.run();
        System.out.println("Time for cleaning: "+(System.currentTimeMillis()-start)+" ms");
        System.out.println("Map size after cleaning: "+map.size());
        // sleep for management agents to have time to present results
        System.out.println("Waiting for agents to connect");
        Thread.sleep(60000);
    }*/
    
    
}
