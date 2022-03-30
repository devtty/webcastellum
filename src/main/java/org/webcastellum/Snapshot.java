package org.webcastellum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class Snapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String type, systemIdentifier;
    
    private Map/*<String,IncrementingCounter>*/ payload;
    private List/*<String>*/ removals;
    private long removalTimestamp;

    // used internally
    private Snapshot(String type, String systemIdentifier) {
        this.type = type;
        this.systemIdentifier = systemIdentifier;
    }
    
    // used to publish increments
    public Snapshot(String type, String systemIdentifier, Map payload) {
        this(type, systemIdentifier);
        this.payload = payload;
    }
    
    // used to publish removals
    public Snapshot(String type, String systemIdentifier, List removals) {
        this(type, systemIdentifier);
        this.removals = removals;
        this.removalTimestamp = System.currentTimeMillis();
    }

    /**
     * @return map of things to increment
     */
    public Map/*<String,IncrementingCounter>*/ getPayload() {
        return payload;
    }

    /**
     * @return list of things to remove
     */
    public List/*<String>*/ getRemovals() {
        return removals;
    }
    
    public boolean hasPayload() {
        return this.payload != null && !this.payload.isEmpty();
    }
    
    public boolean hasRemovals() {
        return this.removals != null && !this.removals.isEmpty();
    }
    
    public long getRemovalTimestamp() {
        return this.removalTimestamp;
    }
    
    public boolean isEmpty() {
        return !hasPayload() && !hasRemovals();
    }

    public String getSystemIdentifier() {
        return systemIdentifier;
    }

    public String getType() {
        return type;
    }
    
    
    
    
    
    
    
    /* just testing serialization * /
    public static final void main(String[] args) throws Exception {
        Map payload = new HashMap();
        for (int i=0; i<1000; i++) {
            IncrementingCounter counter = new IncrementingCounter((long)(Math.random()*10000)+5000);
            int x = (int)(Math.random()*1500);
            System.out.println(x);
            for (int k=0; k<x; k++) counter.increment();
            payload.put(IdGeneratorUtils.createId(), counter);
        }
        Snapshot s = new Snapshot("ThisIsJustATestType", IdGeneratorUtils.createId(), payload);
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream("/tmp/file.test") ) );
            output.writeObject(s);
            output.flush();
        } finally {
            if (output != null) output.close();
        }
    }
    /**/

    
    
    
    
}
