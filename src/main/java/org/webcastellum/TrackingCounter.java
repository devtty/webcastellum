package org.webcastellum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This counter tracks each increment with a timestamp to have overaged trackings timeout.
 * But to conserve time and memory it aggregates over a certain period (a few seconds) trackings
 * and counts them against a single timestamp.
 */
public final class TrackingCounter extends AbstractCounter {
    private static final long serialVersionUID = 1L;

    // TODO: make this value configurable
    private static final int DEFAULT_AGGREGATION_PERIOD_SECONDS = 10;
            
    private final List<AggregatedTrackedValue> counter = new ArrayList<>();
    private final long aggregationPeriodMillis;
    private AggregatedTrackedValue current;
    
    
    public TrackingCounter(final long resetPeriodMillis) {
        super(resetPeriodMillis);
        this.aggregationPeriodMillis = Math.min(resetPeriodMillis, DEFAULT_AGGREGATION_PERIOD_SECONDS*1000L); 
        // initial increment
        increment();
    }
    
    // NOTE: This method might be called *very* frequently so it should be fast and low-memory consuming !!
    @Override
    public final synchronized void increment() {
        if (this.current == null) { // = we're fresh and completely empty
            createAndAddNewAggregation();
        } else { // = check if the current AggregatedTrackedValue is still in time to add on top of aggregation or if we should create a new AggregatedTrackedValue
            if (this.current.timestamp >= System.currentTimeMillis()) { // = we're still in time (aggregation time window) and can safely increment the aggregation value
                this.current.size++;
            } else { // = add a new one (which automatically sets the cache pointer on the current)
                createAndAddNewAggregation();
            }
        }
    }
    
    private void createAndAddNewAggregation() {
        // we use aggregation in order to track the count within a few seconds and treat it as one aggregated count value (to preserve memory and performance)
        final AggregatedTrackedValue newAggregation = new AggregatedTrackedValue(this.aggregationPeriodMillis);
        this.counter.add(newAggregation);
        // set cache pointer on the current
        this.current = newAggregation;
    }
    
    
    @Override
    public final synchronized boolean isOveraged() {
        cutoffOldTrackings();
        return this.counter.isEmpty();
    }
    
    
    @Override
    public final synchronized int getCounter() {
        cutoffOldTrackings();
        int result = 0;
        result = counter.stream().map(trackedValue -> trackedValue.size).reduce(result, Integer::sum);
        return result;
    }
    

    private void cutoffOldTrackings() {
        if (this.counter.isEmpty()) return;
        final long cutoffTimestamp = System.currentTimeMillis() - this.getResetPeriodMillis();
        for (final Iterator<AggregatedTrackedValue> iter = this.counter.iterator(); iter.hasNext();) {
            final AggregatedTrackedValue trackedValue = iter.next();
            if (trackedValue.timestamp < cutoffTimestamp) 
                iter.remove();
            else 
                break; // since the elements are in chronological order
        }
    }
    
    @Override
    public final String toString() {
        return "counter with reset period: "+getResetPeriodMillis();
    }
    
    
    // An aggregated tracked value (we aggregate a little bit to save time and memory)
    private static final class AggregatedTrackedValue implements Serializable {
        final long timestamp;
        int size = 1;
        public AggregatedTrackedValue(final long aggregationPeriodMillis) {
            this.timestamp = System.currentTimeMillis() + aggregationPeriodMillis;
        }
    }
    
}
