package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;


public final class CleanupIncrementingCounterTask extends TimerTask {
//    private final static boolean DEBUG = false;
    
    private final String name;
    private final Map/*<String,Counter>*/ map;

    public CleanupIncrementingCounterTask(final String name, final Map/*<String,Counter>*/ map) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.name = name;
        this.map = map;
    }
    
    public void run() {
        if (this.map.isEmpty()) return;
        int loosers = 0;
        synchronized (this.map) {
            for (final Iterator entries = this.map.entrySet().iterator(); entries.hasNext();) {
                final Map.Entry entry = (Map.Entry) entries.next();
                final IncrementingCounter counter = (IncrementingCounter) entry.getValue();
                if (counter != null && counter.isOveraged()) {
                    entries.remove();
                    loosers++;
                }
            }
        }
//        if (DEBUG) System.out.println("*** CLEANUP: "+loosers+" overaged counters removed (from "+this.name+") ***");
    }
    
}
