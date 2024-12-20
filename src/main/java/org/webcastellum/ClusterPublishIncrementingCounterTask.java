package org.webcastellum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.naming.NamingException;


public final class ClusterPublishIncrementingCounterTask extends TimerTask {
    
    private static final Logger LOGGER = Logger.getLogger(ClusterPublishIncrementingCounterTask.class.getName());
    
    private final String type;
    private final String systemIdentifier;
    private final String clusterInitialContextFactory;
    private final String clusterJmsProviderUrl;
    private final String clusterJmsConnectionFactory;
    private final String clusterJmsTopic;
    private final Map<String,Counter> map;

    public ClusterPublishIncrementingCounterTask(final String type, final String systemIdentifier, final String clusterInitialContextFactory, final String clusterJmsProviderUrl, final String clusterJmsConnectionFactory, final String clusterJmsTopic, final Map<String,Counter> map) {
        if (type == null) throw new NullPointerException("type must not be null");
        if (systemIdentifier == null) throw new NullPointerException("systemIdentifier must not be null");
        if (map == null) throw new NullPointerException("map must not be null");
        this.type = type;
        this.systemIdentifier = systemIdentifier;
        this.clusterInitialContextFactory = clusterInitialContextFactory;
        this.clusterJmsProviderUrl = clusterJmsProviderUrl;
        this.clusterJmsConnectionFactory = clusterJmsConnectionFactory;
        this.clusterJmsTopic = clusterJmsTopic;
        this.map = map;
    }
    
    
    @Override
    public void run() {
        try {
            JmsUtils.init(this.clusterInitialContextFactory, this.clusterJmsProviderUrl, this.clusterJmsConnectionFactory, this.clusterJmsTopic); // in case connection was dropped due to some reason (unreliable network etc.)
        } catch (NamingException | JMSException | RuntimeException e) {
            JmsUtils.closeQuietly(false); // to be re-initialized on the next call
            LOGGER.log(Level.WARNING, "Unable to init: {0}", e.getMessage());
        }
        // shortcut
        if (this.map.isEmpty()) return;
        // create a copy of all relevant counters using the copy-constructor
        final Map<String,Counter> payload = new HashMap<>(this.map.size());
        synchronized (this.map) {
            try {
                for (final Iterator<Map.Entry<String,Counter>> iter = this.map.entrySet().iterator(); iter.hasNext();) {
                    final Map.Entry<String,Counter> entry = (Map.Entry) iter.next();
                    final IncrementingCounter counter = (IncrementingCounter) entry.getValue();
                    if (counter.getDelta() > 0 && !counter.isOveraged()) {
                        payload.put(entry.getKey(), (Counter) counter.clone());
                        // NOW we can reset the original's delta
                        counter.resetDelta();
                    }
                }
            } catch (CloneNotSupportedException e) {
                System.err.println("Unable to clone: "+e); // TODO: better logging
            }
        }
	LOGGER.log(Level.FINE, "map: {0} payload: {1}", new Object[]{this.map, payload});
        
        final Snapshot snapshot = new Snapshot(this.type, this.systemIdentifier, payload);
        // PUBLISH THE SNAPSHOT USING JMS
        JmsUtils.publishSnapshot(snapshot);
    }

    
}
