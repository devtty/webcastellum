package org.webcastellum;

import java.util.regex.Pattern;

/**
 * A simple definition that holds only a servletPath/requestURI kind of thing 
 * aside from the base properties inherited via AbstractDefinition. Also this simple
 * definition does not allow negation (the full-blown RequestDefinition does).
 */
public abstract class SimpleDefinition extends AbstractDefinition {
    
    private final WordDictionary servletPathOrRequestURIPrefilter;
    private final Pattern servletPathOrRequestURIPattern;


    public SimpleDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description);
        this.servletPathOrRequestURIPrefilter = servletPathOrRequestURIPrefilter; // nullable
        if (servletPathOrRequestURIPattern == null) throw new NullPointerException("servletPathOrRequestURIPattern must not be null");
        this.servletPathOrRequestURIPattern = servletPathOrRequestURIPattern;
    }


    
    public final WordDictionary getServletPathOrRequestURIPrefilter() {
        return servletPathOrRequestURIPrefilter;
    }
    
    
    public final Pattern getServletPathOrRequestURIPattern() {
        return servletPathOrRequestURIPattern;
    }
    
    
}
