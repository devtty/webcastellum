package org.webcastellum;

import java.util.Enumeration;
import java.util.Iterator;

public final class IteratorEnumerationAdapter implements Enumeration {

    private final Iterator iterator;

    public IteratorEnumerationAdapter(final Iterator iter) {
        this.iterator = iter;
    }

    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    public Object nextElement() {
        return this.iterator.next();
    }
    
}
