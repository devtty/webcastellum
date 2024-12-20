package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class CleanupIncrementingCounterTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(CleanupIncrementingCounterTask.class.getName());
    
    private final String name;
    private final Map<String,Counter> map;

    public CleanupIncrementingCounterTask(final String name, final Map<String,Counter> map) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.name = name;
        this.map = map;
    }
    
    @Override
    public void run() {
        if (this.map.isEmpty()) return;
        int loosers = 0;
        synchronized (this.map) {
            for (final Iterator<Map.Entry<String, Counter>> entries = this.map.entrySet().iterator(); entries.hasNext();) {
                final Map.Entry<String, Counter> entry = (Map.Entry) entries.next();
                final IncrementingCounter counter = (IncrementingCounter) entry.getValue();
                if (counter != null && counter.isOveraged()) {
                    entries.remove();
                    loosers++;
                }
            }
        }
        LOGGER.log(Level.FINE, "*** CLEANUP: {0} overaged counters removed (from {1}]) ***", new Object[]{loosers, name});
    }
    
}
