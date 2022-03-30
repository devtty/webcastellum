package org.webcastellum;

import java.util.regex.Pattern;

public final class ContentModificationExcludeDefinition extends SimpleDefinition {
    private static final long serialVersionUID = 1L;

    private boolean excludeOutgoingResponsesFromModification, excludeIncomingLinksFromModification, excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled;

    public ContentModificationExcludeDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }


    public boolean isExcludeOutgoingResponsesFromModification() {
        return excludeOutgoingResponsesFromModification;
    }
    public void setExcludeOutgoingResponsesFromModification(boolean excludeOutgoingResponsesFromModification) {
        this.excludeOutgoingResponsesFromModification = excludeOutgoingResponsesFromModification;
    }

    public boolean isExcludeIncomingLinksFromModification() {
        return excludeIncomingLinksFromModification;
    }
    public void setExcludeIncomingLinksFromModification(boolean excludeIncomingLinksFromModification) {
        this.excludeIncomingLinksFromModification = excludeIncomingLinksFromModification;
    }
    
    public boolean isExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled() {
        return excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled;
    }
    public void setExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled(boolean excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled) {
        this.excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled = excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled;
    }

    
    
}
