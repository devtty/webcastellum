package org.webcastellum;


public abstract class AbstractRuleFileLoader implements RuleFileLoader {
    
    protected String path;
    
    public final void setPath(final String path) {
        if (path == null) throw new NullPointerException("path must not be null");
        this.path = path.trim();
    }    
    public final String getPath() {
        return this.path;
    }
    
    
}
