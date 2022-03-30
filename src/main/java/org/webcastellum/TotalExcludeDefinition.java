package org.webcastellum;

import java.util.regex.Pattern;

public final class TotalExcludeDefinition extends SimpleDefinition {
    private static final long serialVersionUID = 1L;
    
    public TotalExcludeDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }
    
}
