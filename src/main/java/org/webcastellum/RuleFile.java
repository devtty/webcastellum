package org.webcastellum;

import java.io.Serializable;
import java.util.Properties;

public final class RuleFile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final Properties properties;
    
    public RuleFile(final String name, final Properties properties) {
        if (name == null) throw new NullPointerException("name must not be null");
        if (properties == null) throw new NullPointerException("properties must not be null");
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public Properties getProperties() {
        return properties;
    }
    
    
    //1.5@Override
    public String toString() {
        return this.name;
    }
    
    
}
