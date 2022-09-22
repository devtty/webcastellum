package org.webcastellum;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClusterSubscribeIncrementingCounterClient implements SnapshotBroadcastListener {

    private static final Logger LOGGER = Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName());
    
    private final String type;
    private final String systemIdentifier;
    private final Map<String, Counter> map;

    public ClusterSubscribeIncrementingCounterClient(final String type, final String systemIdentifier, final Map<String, Counter> map) {
        if (type == null) {
            throw new NullPointerException("type must not be null");
        }
        if (systemIdentifier == null) {
            throw new NullPointerException("systemIdentifier must not be null");
        }
        if (map == null) {
            throw new NullPointerException("map must not be null");
        }
        this.type = type;
        this.systemIdentifier = systemIdentifier;
        this.map = map;
    }

    public void handleSnapshotBroadcast(final Snapshot snapshot) {
        if (snapshot == null || snapshot.isEmpty() || !this.type.equals(snapshot.getType()) || this.systemIdentifier.equals(snapshot.getSystemIdentifier())) {
            return; // = nothing to do
        }
        synchronized (this.map) {
            // payload (incrementals)
            if (snapshot.hasPayload()) {
                for (final Iterator iter = snapshot.getPayload().entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry<String, Counter> entry = (Map.Entry) iter.next();
                    final String ip = (String) entry.getKey();
                    final IncrementingCounter foreignCounter = (IncrementingCounter) entry.getValue();
                    final IncrementingCounter localCounter = (IncrementingCounter) this.map.get(ip);
                    if (JmsUtils.DEBUG) {
                        LOGGER.log(Level.FINE, "foreignCounter: {0}", foreignCounter);
                    }
                    if (localCounter == null) {
                        final IncrementingCounter copy = new IncrementingCounter(foreignCounter); // using copy-constructor
                        copy.resetDelta(); // to avoid ping-pong back upon creation
                        this.map.put(ip, copy);
                    } else {
                        localCounter.mergeWith(foreignCounter);
                    }
                }
            }
            // removals
            if (snapshot.hasRemovals()) {
                for (final Iterator iter = snapshot.getRemovals().iterator(); iter.hasNext();) {
                    // removal-indicating broadcast (i.e. the limit was reached on the foreign site and therefore the counter has to be removed)
                    final String ip = (String) iter.next();
                    // but instead of removing, better reset to zero
                    final IncrementingCounter counter = (IncrementingCounter) this.map.get(ip);
                    if (counter != null) {
                        counter.resetAllOnForeignRemoval(snapshot.getRemovalTimestamp());
                        if (JmsUtils.DEBUG) {
                            LOGGER.log(Level.FINE, "foreignRemoval (resetted to 0): {0}", ip);
                        }
                    }
                }
            }
        }
    }

}
