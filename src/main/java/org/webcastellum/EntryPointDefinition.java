package org.webcastellum;

import java.util.regex.Pattern;

public final class EntryPointDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
 
    public EntryPointDefinition(final boolean enabled, final String identification, final String description,   final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public EntryPointDefinition(final boolean enabled, final String identification, final String description,   final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }
    
}
