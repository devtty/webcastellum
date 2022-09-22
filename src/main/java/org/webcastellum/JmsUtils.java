package org.webcastellum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class JmsUtils {

    private JmsUtils() {
    }

    private static final Logger LOGGER = Logger.getLogger(JmsUtils.class.getName());

    private static final int STANDARD_PRIORITY = Message.DEFAULT_PRIORITY;
    private static final int HIGHER_PRIORITY = STANDARD_PRIORITY + 3;
    private static final long TIME_TO_LIVE = 15L * 60L * 1000L; // unsent messages are allowed to expire here after 15 minutes

    private static boolean initialized;

    private static Context context;
    private static TopicConnectionFactory connectionFactory;
    private static TopicConnection connection;
    private static TopicSession session;
    private static Topic topic;
    private static TopicPublisher publisher;
    private static TopicSubscriber subscriber;
    private static MessageListener listener;

    /**
     * Map of type to listeners
     */
    private static Map<String, List<SnapshotBroadcastListener>> type2listeners = new HashMap();

    public static synchronized void init(final String clusterInitialContextFactory, final String clusterJmsProviderUrl, final String clusterJmsConnectionFactory, final String clusterJmsTopic) throws NamingException, JMSException {
        if (!initialized) {
            LOGGER.log(Level.FINE, "JMS init");
        }

        if (context == null) {
            Hashtable properties = new Hashtable();
            if (clusterInitialContextFactory != null) {
                properties.put(Context.INITIAL_CONTEXT_FACTORY, clusterInitialContextFactory);
            }
            if (clusterJmsProviderUrl != null) {
                properties.put(Context.PROVIDER_URL, clusterJmsProviderUrl);
            }
            context = properties.isEmpty() ? new InitialContext() : new InitialContext(properties);
        }
        if (connectionFactory == null) {
            connectionFactory = (TopicConnectionFactory) context.lookup(clusterJmsConnectionFactory);
        }
        if (connection == null) {
            connection = connectionFactory.createTopicConnection();
        }
        if (session == null) {
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        if (topic == null) {
            topic = (Topic) context.lookup(clusterJmsTopic);
        }
        if (publisher == null) {
            publisher = session.createPublisher(topic);
        }
        if (subscriber == null) {
            subscriber = session.createSubscriber(topic, null, true); // true = to ignore messages published by itself (i.e. the same connection) already at the JMS level
        }
        if (listener == null) {
            listener = new IncomingMessageDispatcher();
            subscriber.setMessageListener(listener);
            // start the connection to enable message delivery
            connection.start();
        }
        initialized = true;
        LOGGER.log(Level.FINE, "topic: {0}", topic);
    }

    public static void closeQuietly(final boolean destroy) {
        LOGGER.log(Level.FINE, "JMS closeQuietly");
        if (publisher != null) {
            try {
                publisher.close();
            } catch (JMSException ignored) {
            }
            publisher = null;
        }
        if (listener != null) {
            listener = null;
        }
        if (subscriber != null) {
            try {
                subscriber.close();
            } catch (JMSException ignored) {
            }
            subscriber = null;
        }
        if (topic != null) {
            topic = null;
        }
        if (session != null) {
            try {
                session.close();
            } catch (JMSException ignored) {
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ignored) {
            }
            connection = null;
        }
        if (connectionFactory != null) {
            connectionFactory = null;
        }
        if (context != null) {
            try {
                context.close();
            } catch (NamingException ignored) {
            }
            context = null;
        }
        // when complete destroyal is desired (on shutdown) we can also safely drop the listeners
        if (destroy) {
            type2listeners.clear();
        }
    }

    public static synchronized void publishSnapshot(final Snapshot snapshot) {
        LOGGER.log(Level.FINE, "JMS publishSnapshot");
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }
        try {
            final ObjectMessage message = session.createObjectMessage(snapshot);
            LOGGER.log(Level.FINE, "JMS publishMessage: {0}", message);
            final int priority = snapshot.hasRemovals() ? HIGHER_PRIORITY : STANDARD_PRIORITY;
            publisher.publish(message, DeliveryMode.NON_PERSISTENT, priority, TIME_TO_LIVE);
        } catch (JMSException | RuntimeException e) {
            closeQuietly(false); // to be re-initialized on the next call
            LOGGER.log(Level.SEVERE, "Unable to publish message: {0}", e); // TODO: add better logging (using AttackHandler to log into the rotated file)
        }
    }

    public static synchronized void addSnapshotBroadcastListener(final String type, final SnapshotBroadcastListener listener) {
        LOGGER.log(Level.FINE, "JMS addSnapshotBroadcastListener");
        if (type == null) {
            throw new NullPointerException("type must not be null");
        }
        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }
        if (initialized) {
            closeQuietly(false); // in order to have a fresh re-initialization upon next call, since a message-listener was added
        }        // add it
        List<SnapshotBroadcastListener> listeners = (List) type2listeners.get(type);
        if (listeners == null) {
            listeners = new ArrayList<>();
            type2listeners.put(type, listeners);
        }
        listeners.add(listener);
    }

    public static final class IncomingMessageDispatcher implements MessageListener {

        public void onMessage(final Message message) {
            try {
                LOGGER.log(Level.FINE, "JMS RECEIVED: {0}", message);
                final Snapshot snapshot = (Snapshot) ((ObjectMessage) message).getObject();
                final List<SnapshotBroadcastListener> listeners = (List) type2listeners.get(snapshot.getType());
                LOGGER.log(Level.FINE, "listeners ({0}): {1}", new Object[]{snapshot.getType(), listeners});
                if (listeners != null && !listeners.isEmpty()) {
                    for (final Iterator iter = listeners.iterator(); iter.hasNext();) {
                        final SnapshotBroadcastListener listener = (SnapshotBroadcastListener) iter.next();
                        listener.handleSnapshotBroadcast(snapshot);
                    }
                }
            } catch (JMSException | RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Unable to handle incoming message: {0}", e); // TODO: add better logging (using AttackHandler to log into the rotated file)
            }
        }
    }

}
