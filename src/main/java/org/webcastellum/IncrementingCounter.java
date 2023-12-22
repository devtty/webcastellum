package org.webcastellum;

public final class IncrementingCounter extends AbstractCounter implements Cloneable {
    private static final long serialVersionUID = 1L;
    
    private long lastEventMillis = 0;
    private long minimumTimestampForForeignActions=0;
    private int totalCounter;
    private int deltaCounter; // delta-counter is resetted when remote cluster service sends the counter over the wire. the recipient decides if it chooses the delta upon merge or creates a fresh one

    
    public IncrementingCounter(final long resetPeriodMillis) {
        super(resetPeriodMillis);
        increment();
    }
    
    

    // copy-constructor
    public IncrementingCounter(final IncrementingCounter objectToCopy) {
        super(objectToCopy);
        this.lastEventMillis = objectToCopy.lastEventMillis;
        this.totalCounter = objectToCopy.totalCounter;
        this.deltaCounter = objectToCopy.deltaCounter;
    }
    
    //TODO DR221104 (remove) usage in ClusterPublishIncrementingCounterTask, copy could be used but no tests available
    public Object clone() throws CloneNotSupportedException {
    //1.5public IncrementingCounter clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public int getDelta() {
        return this.deltaCounter;
    }
    
    public void resetAllOnForeignRemoval(final long removalTimestamp) {
        this.totalCounter = 0;
        this.deltaCounter = 0;
        this.lastEventMillis = System.currentTimeMillis();
        this.minimumTimestampForForeignActions = removalTimestamp;
    }
    
    public void resetDelta() {
        this.deltaCounter = 0;
    }
    
    public void mergeWith(final Counter objectToMergeValuesFrom) {
        if (objectToMergeValuesFrom instanceof IncrementingCounter) {
            final IncrementingCounter counterToMergeValuesFrom = (IncrementingCounter) objectToMergeValuesFrom;
            if (counterToMergeValuesFrom.deltaCounter <= 0) return;
            if (counterToMergeValuesFrom.lastEventMillis <= this.minimumTimestampForForeignActions) return;
            if (isOveraged()) {
                this.totalCounter = counterToMergeValuesFrom.deltaCounter;
            } else {
                this.totalCounter += counterToMergeValuesFrom.deltaCounter;
            }
            this.lastEventMillis = Math.max(this.lastEventMillis, counterToMergeValuesFrom.lastEventMillis);
        }
    }

    public final /*synchronized*/ void decrementQuietly() { // quietly = without touching lastEventMillis
        if (this.totalCounter > 0) this.totalCounter--;
        if (this.deltaCounter > 0) this.deltaCounter--;
    }

    
    public final /*synchronized*/ void increment() {
        if (isOveraged()) {
            this.totalCounter = 1;
            this.deltaCounter = 1;
        } else {
            this.totalCounter++;
            this.deltaCounter++;
        }
        this.lastEventMillis = System.currentTimeMillis();
    }
    
    
    public final /*synchronized*/ boolean isOveraged() {
        return this.lastEventMillis != 0 && this.lastEventMillis + this.getResetPeriodMillis() < System.currentTimeMillis();
    }
    
    
    public final /*synchronized*/ int getCounter() {
        return this.totalCounter;
    }

    
    @Override
    public final String toString() {
        return "counter:"+totalCounter+"("+this.deltaCounter+")"+getResetPeriodMillis();
    }
}
