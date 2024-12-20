package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CleanupBlacklistTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(CleanupBlacklistTask.class.getName());
            
    private final String name;
    private final Map<String,Long> map;

    public CleanupBlacklistTask(final String name, final Map<String,Long> map) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.name = name;
        this.map = map;
    }
    
    @Override
    public void run() {
        int loosers = 0;
        synchronized (this.map) {
            final long now = System.currentTimeMillis();
            for (final Iterator<Map.Entry<String, Long>> entries = this.map.entrySet().iterator(); entries.hasNext();){
                final Map.Entry<String, Long> entry = entries.next();
                final Long value = entry.getValue();
                if (value != null && value < now) {
                    entries.remove();
                    loosers++;
                }
            }
        }
        LOGGER.log(Level.FINE, "*** CLEANUP: {0} old blacklists removed (from {1}) ***", new Object[]{loosers, name});
    }
    
}
