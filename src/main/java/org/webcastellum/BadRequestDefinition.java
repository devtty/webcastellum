package org.webcastellum;

import java.util.regex.Pattern;

public final class BadRequestDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
 
    public BadRequestDefinition(final boolean enabled, final String identification, final String description,   final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public BadRequestDefinition(final boolean enabled, final String identification, final String description,   final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }
    
}
