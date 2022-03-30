package org.webcastellum;

import java.io.Serializable;

public final class Attack implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String message;
    private String logReferenceId;
    
    
    
    public Attack(final String message) {
        if (message == null) throw new NullPointerException("message must not be null");
        this.message = message;
    }
    public Attack(final String message, final String logReferenceId) {
        this(message);
        this.logReferenceId = logReferenceId;
    }
    
    

    public String getLogReferenceId() {
        return logReferenceId;
    }

    public String getMessage() {
        return message;
    }

    
    //1.5@Override
    public String toString() {
        return this.message;
    }
    
    
}
