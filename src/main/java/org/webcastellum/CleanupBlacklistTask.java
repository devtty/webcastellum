package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;


public final class CleanupBlacklistTask extends TimerTask {
//    private final static boolean DEBUG = false;
    
    private final String name;
    private final Map/*<String,Long>*/ map;

    public CleanupBlacklistTask(final String name, final Map/*<String,Long>*/ map) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.name = name;
        this.map = map;
    }
    
    public void run() {
        int loosers = 0;
        synchronized (this.map) {
            final long now = System.currentTimeMillis();
            for (final Iterator entries = this.map.entrySet().iterator(); entries.hasNext();) {
                final Map.Entry entry = (Map.Entry) entries.next();
                final Long value = (Long) entry.getValue();
                if (value != null && value.longValue() < now) {
                    entries.remove();
                    loosers++;
                }
            }
        }
//        if (DEBUG) System.out.println("*** CLEANUP: "+loosers+" old blacklists removed (from "+this.name+") ***");
    }
    
}
