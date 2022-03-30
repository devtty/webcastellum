package org.webcastellum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Simply a container that is serializable (to match with the contrac that all objects placed into the session should be serializable)
 * but keeps a transient reference to the non-serializable SessionWrapper (since that should also be inside a live session until serialized)
 */
public class SessionWrapperTransientBox implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final transient SessionWrapper sessionWrapper;

    public SessionWrapperTransientBox() {
        // required for special serialization handling
        this(null);
    }
    public SessionWrapperTransientBox(SessionWrapper sessionWrapper) {
        this.sessionWrapper = sessionWrapper;
    }

    public SessionWrapper getSessionWrapper() {
        return sessionWrapper;
    }
    

    // required for special serialization handling
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    //1.5@Override
    public String toString() {
        return "SWTB"; // SWTB = SessionWrapperTransientBox
    }
    
    
}
