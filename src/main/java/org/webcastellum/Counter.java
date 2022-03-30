package org.webcastellum;

import java.io.Serializable;

// NOTE: Be careful about using objects of this type outside of the synchronized code (synchronized on the Map) that is present in the Tracker and Blacklist classes...
// Otherwise when re-using objects of this Counter type in other situations where you synchronize on something other you might break the static locking order....
public interface Counter extends Serializable {

    void increment();
    int getCounter();
    boolean isOveraged();
    long getResetPeriodMillis();
    void setResetPeriodMillis(long resetPeriodMillis);
    
    /* old
    void mergeWith(Counter objectToMergeValuesFrom);
    int getDelta();
    void resetDelta();
    Counter clone() throws CloneNotSupportedException; // stating that implementors of Counter must offer a public clone method
    */
    
}
