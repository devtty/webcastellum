package org.webcastellum;

import java.util.regex.Pattern;

public final class DenialOfServiceLimitDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
 
    private int watchPeriodMillis;
    private int clientDenialOfServiceLimit;
    
    public DenialOfServiceLimitDefinition(final boolean enabled, final String identification, final String description,     final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public DenialOfServiceLimitDefinition(final boolean enabled, final String identification, final String description,     final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }

    
    
    public int getWatchPeriodMillis() {
        return watchPeriodMillis;
    }
    protected void setWatchPeriodSeconds(int seconds) {
        this.watchPeriodMillis = seconds*1000;
    }

    public int getClientDenialOfServiceLimit() {
        return clientDenialOfServiceLimit;
    }
    protected void setClientDenialOfServiceLimit(int limit) {
        this.clientDenialOfServiceLimit = limit;
    }
    
}
