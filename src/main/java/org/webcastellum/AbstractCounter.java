package org.webcastellum;

public abstract class AbstractCounter implements Counter {
    
    private volatile long resetPeriodMillis;
    
    protected AbstractCounter(final long resetPeriodMillis) {
        this.resetPeriodMillis = resetPeriodMillis;
    }
    
    // copy-constructor
    protected AbstractCounter(final AbstractCounter objectToCopy) {
        if (objectToCopy == null) throw new NullPointerException("objectToCopy must not be null");
        this.resetPeriodMillis = objectToCopy.resetPeriodMillis;
    }

    @Override
    public long getResetPeriodMillis() {
        return resetPeriodMillis;
    }

    @Override
    public void setResetPeriodMillis(long resetPeriodMillis) {
        this.resetPeriodMillis = resetPeriodMillis;
    }
    
}
