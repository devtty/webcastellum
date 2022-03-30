package org.webcastellum;

public abstract class AbstractCounter implements Counter {
    
    private volatile long resetPeriodMillis;
    
    
    public AbstractCounter(final long resetPeriodMillis) {
        this.resetPeriodMillis = resetPeriodMillis;
    }
    
    
    // copy-constructor
    public AbstractCounter(final AbstractCounter objectToCopy) {
        if (objectToCopy == null) throw new NullPointerException("objectToCopy must not be null");
        this.resetPeriodMillis = objectToCopy.resetPeriodMillis;
    }

    
    
    public long getResetPeriodMillis() {
        return resetPeriodMillis;
    }
    public void setResetPeriodMillis(long resetPeriodMillis) {
        this.resetPeriodMillis = resetPeriodMillis;
    }
    
    
}
