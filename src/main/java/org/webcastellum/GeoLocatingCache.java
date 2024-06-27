package org.webcastellum;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Caches the looked-up locations on class-c /24 subnets
 */
public final class GeoLocatingCache {

    private static final Logger LOGGER = Logger.getLogger(GeoLocatingCache.class.getName());
    
    private static final boolean DEBUG = false;
    private static final boolean PRINT_COUNTRY = false;

    // TODO: make this configurable
    private static final int MAXIMUM_CACHE_SIZE = 1000;
    private static final int MAXIMUM_CACHE_SIZE_CUTOFF_TOLERANCE = 200; // used to avoid frequent reductions of the cache... better a few bigger than many smaller reductions...
    private static final long MAXIMUM_CACHE_TTL_MILLIS = 14400000L; // = 4 hours

    private final GeoLocator locator;
    private final long cleanupIntervalMillis;
    private final Map<String, GeoLocation> geoLocations = Collections.synchronizedMap(new HashMap<>());

    private Timer cleanupTimer;
    private TimerTask task;

    public GeoLocatingCache(final GeoLocator locator, final long cleanupIntervalMillis) {
        this.locator = locator;
        this.cleanupIntervalMillis = cleanupIntervalMillis;
    }

    private void initTimers() {
        if (this.cleanupTimer == null) {
            this.cleanupTimer = new Timer(/*"GeoLocatingCache-cleanup", */true); // TODO Java5: name parameter in Time constructor is only available in Java5
            this.task = new CleanupGeoLocatingCacheTask("GeoLocatingCache", this.geoLocations);
            this.cleanupTimer.scheduleAtFixedRate(task, DEBUG ? 10 : CryptoUtils.generateRandomNumber(false, 60000, 300000), this.cleanupIntervalMillis);
        }
    }

    public void destroy() {
        this.geoLocations.clear();
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        if (this.cleanupTimer != null) {
            this.cleanupTimer.cancel();
            this.cleanupTimer = null;
            this.geoLocations.clear();
        }
    }

    public String getCountryCode(final String ip) {
        if (ip == null) {
            return null;
        }
        if (this.locator == null || !this.locator.isEnabled()) {
            return null;
        }
        // lazily init cleanup timer
        if (this.cleanupTimer == null) {
            synchronized (this) {
                if (this.cleanupTimer == null) {
                    initTimers();
                }
                assert this.cleanupTimer != null;
            }
        }
        // extract subnet and lookup geo location
        final String subnet = extractSubnet(ip);
        GeoLocation location = (GeoLocation) this.geoLocations.get(subnet);
        if (location == null) {
            try {
                final String country = this.locator.getCountryCode(ip);
                if (PRINT_COUNTRY) {
                    LOGGER.log(Level.INFO, "Looked up country: {0}", country);
                }
                if (country != null || this.locator.isCachingOfNegativeRepliesAllowed()) {
                    location = new GeoLocation(subnet, country);
                }
            } catch (GeoLocatingException | RuntimeException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
                if (this.locator.isCachingOfNegativeRepliesAllowed()) {
                    location = new GeoLocation(subnet, null);
                }
            }
            if (location != null) {
                this.geoLocations.put(subnet, location);
                if (this.geoLocations.size() > MAXIMUM_CACHE_SIZE) {
                    cutoffOldestElements();
                }
            }
        }
        return location == null ? null : location.getCountry();
    }

    private void cutoffOldestElements() {
        if (this.geoLocations.size() <= MAXIMUM_CACHE_SIZE) {
            return;
        }
        synchronized (this.geoLocations) {
            if (this.geoLocations.size() <= MAXIMUM_CACHE_SIZE) {
                return; // double checked since my threads could wait on the monitor to access this block and the first has already reduced the cache
            }            // here we reduce the cache size (since it is too big) by removing the oldest ones
            final int looserCount = this.geoLocations.size() - (MAXIMUM_CACHE_SIZE - MAXIMUM_CACHE_SIZE_CUTOFF_TOLERANCE);
            if (looserCount <= 0) {
                return; // = nothing to remove
            }
            final SortedSet<GeoLocation> geoLocationsSortedByAge = new TreeSet<>(LAST_ACCESS_COMPARATOR);
            geoLocationsSortedByAge.addAll(this.geoLocations.values());
            int i = 0;
            for (final Iterator iter = geoLocationsSortedByAge.iterator(); iter.hasNext();) {
                final GeoLocation looser = (GeoLocation) iter.next();
                this.geoLocations.remove(looser.getSubnet());
                if (++i >= looserCount) {
                    break;
                }
            }
            LOGGER.log(Level.FINE, "*** CLEANUP: Geo-Location cache reduced to {0} (cache limit reached, removed {1} loosers) ***", new Object[]{this.geoLocations.size(), i});
        }
    }

    private String extractSubnet(String ip) {
        final int pos = ip.lastIndexOf(".");
        if (pos == -1) {
            return ip;
        }
        return ip.substring(0, pos);
    }

    private static final Comparator LAST_ACCESS_COMPARATOR = new LastAccessComparator();

    public static final class LastAccessComparator implements Comparator {

        public int compare(final Object obj1, final Object obj2) {
            final GeoLocation left = (GeoLocation) obj1;
            final GeoLocation right = (GeoLocation) obj2;
            if (left.getLastAccess() < right.getLastAccess()) {
                return -1;
            }
            if (left.getLastAccess() > right.getLastAccess()) {
                return 1;
            }
            return 0;
        }
    }

    public static final class GeoLocation implements Serializable {

        private static final long serialVersionUID = 1L;
        private final String subnet;
        private final String country;
        private final long creation = System.currentTimeMillis();

        private volatile long lastAccess;

        public GeoLocation(final String subnet, final String country) {
            this.subnet = subnet;
            this.country = country;
            this.lastAccess = System.currentTimeMillis();
        }

        public long getCreation() {
            return creation;
        }

        public long getLastAccess() {
            return lastAccess;
        }

        public String getSubnet() {
            return subnet;
        }

        public String getCountry() {
            this.lastAccess = System.currentTimeMillis();
            return country;
        }

        //1.5@Override
        public String toString() {
            return this.subnet + ": " + this.country;
        }
    }

    public static final class CleanupGeoLocatingCacheTask extends TimerTask {

        private final String name;
        private final Map<String,GeoLocation> map;

        public CleanupGeoLocatingCacheTask(final String name, final Map<String,GeoLocation> map) {
            LOGGER.log(Level.FINE, "Created cleanup timer");
            if (name == null) {
                throw new NullPointerException("name must not be null");
            }
            if (map == null) {
                throw new NullPointerException("map must not be null");
            }
            this.name = name;
            this.map = map;
        }

        public void run() {
            int loosers = 0;
            LOGGER.log(Level.FINE, "*** CLEANUP: waiting to get monitor ***");
            synchronized (this.map) {
                // here we remove all outdated cache entries
                final long cutoff = System.currentTimeMillis() - (DEBUG ? 100 : MAXIMUM_CACHE_TTL_MILLIS);
                for (final Iterator entries = this.map.entrySet().iterator(); entries.hasNext();) {
                    final Map.Entry entry = (Map.Entry) entries.next();
                    final GeoLocation location = (GeoLocation) entry.getValue();
                    if (location != null && location.getCreation() < cutoff) {
                        entries.remove();
                        loosers++;
                    }
                }
            }
            LOGGER.log(Level.FINE, "*** CLEANUP: {0} old geo-locations removed (from {1}) ***", new Object[]{loosers, this.name});
        }

    }

    /* * /
    // just for local testing
    public static final void main(String[] args) throws InterruptedException {
        final GeoLocatingCache cache = new GeoLocatingCache(new GeoLocator(){
            public String getCountryCode(String ip) throws GeoLocatingException {
                return ip.substring(ip.length()-4);
            }
            public boolean isEnabled() {
                return true;
            }
            public boolean isCachingOfNegativeRepliesAllowed() {
                return true;
            }
            public void setFilterConfig(javax.servlet.FilterConfig filterConfig) {
            }
        }, 5000);


        final Runnable runnable = new Runnable(){
            public void run() {
                final Random random = new Random();
                try {
                    for (int i=0; i<20000; i++) {
                        cache.getCountryCode("dummy" + random.nextInt(10000));
                        java.lang.Thread.sleep(2);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger("global").log(Level.SEVERE, null, ex);
                }
            }
        };
        final Thread t0 = new Thread(runnable);
        final Thread t1 = new Thread(runnable);
        final Thread t2 = new Thread(runnable);
        final Thread t3 = new Thread(runnable);
        final Thread t4 = new Thread(runnable);
        final Thread t5 = new Thread(runnable);
        final Thread t6 = new Thread(runnable);
        final Thread t7 = new Thread(runnable);
        final Thread t8 = new Thread(runnable);
        final Thread t9 = new Thread(runnable);

        t0.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();

        t0.join();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();
        t7.join();
        t8.join();
        t9.join();

        System.out.println( cache.getCountryCode("dummy"+1800) );
        Thread.sleep(10000);
        cache.destroy();
    }
    /* */
}
