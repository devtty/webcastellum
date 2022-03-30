package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ContentModificationExcludeDefinitionContainer extends SimpleDefinitionContainer {

    private static final String KEY_EXCLUDE_OUTGOING_RESPONSES_FROM_MODIFICATION = "excludeOutgoingResponsesFromModification"; // = response of a matching URI will not be modified
    private static final String KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION = "excludeIncomingLinksFromModification"; // = links in responses of other pages pointng to this matching URI won't get modified
    private static final String KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION_EVEN_WHEN_FULL_PATH_REMOVAL_ENABLED = "excludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled"; // = links in responses of other pages pointng to this matching URI won't get modified (even when full path removal is enabled)... better set this setting here to false, so that when full path removal is enabled the path-deltas with relative paths will get solved properly

    
    public ContentModificationExcludeDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }


    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new ContentModificationExcludeDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        final ContentModificationExcludeDefinition contentModificationExcludeDefinition = (ContentModificationExcludeDefinition) definition;
        
        // load custom properties
        {
            final String value = properties.getProperty(KEY_EXCLUDE_OUTGOING_RESPONSES_FROM_MODIFICATION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing content-modification-exclude specific value: "+KEY_EXCLUDE_OUTGOING_RESPONSES_FROM_MODIFICATION+" for rule: "+contentModificationExcludeDefinition.getIdentification());
            contentModificationExcludeDefinition.setExcludeOutgoingResponsesFromModification( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_OUTGOING_RESPONSES_FROM_MODIFICATION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing content-modification-exclude specific value: "+KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION+" for rule: "+contentModificationExcludeDefinition.getIdentification());
            contentModificationExcludeDefinition.setExcludeIncomingLinksFromModification( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION_EVEN_WHEN_FULL_PATH_REMOVAL_ENABLED);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing content-modification-exclude specific value: "+KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION_EVEN_WHEN_FULL_PATH_REMOVAL_ENABLED+" for rule: "+contentModificationExcludeDefinition.getIdentification());
            contentModificationExcludeDefinition.setExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION_EVEN_WHEN_FULL_PATH_REMOVAL_ENABLED);
        }
        
        // plausibility checks
        if (!contentModificationExcludeDefinition.isExcludeIncomingLinksFromModification() && contentModificationExcludeDefinition.isExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled())
            throw new IllegalRuleDefinitionFormatException("content-modification-exclude specific value: "+KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION+" must be enabled too when "+KEY_EXCLUDE_INCOMING_LINKS_FROM_MODIFICATION_EVEN_WHEN_FULL_PATH_REMOVAL_ENABLED+" is enabled for rule: "+contentModificationExcludeDefinition.getIdentification());
    }

    

    
    // general
    
    public final ContentModificationExcludeDefinition getMatchingContentModificationExcludeDefinition(final String servletPath, final String requestURI) {
        final SimpleDefinition simpleDefinition = getMatchingSimpleDefinition(servletPath, requestURI);
        if (simpleDefinition == null) return null;
        return (ContentModificationExcludeDefinition) simpleDefinition;
    }

    
    
    
    // in detail
    
    public final boolean isMatchingIncomingLinkModificationExclusion(final String linkTargetUri) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's flags 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        final ContentModificationExcludeDefinition definition = getMatchingContentModificationExcludeDefinition(null, linkTargetUri); // here we only have an URI and no servletPath
        return definition != null && definition.isExcludeIncomingLinksFromModification();
    }
    
    public final boolean isMatchingIncomingLinkModificationExclusionEvenWhenFullPathRemovalEnabled(final String linkTargetUri) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's flags 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        final ContentModificationExcludeDefinition definition = getMatchingContentModificationExcludeDefinition(null, linkTargetUri); // here we only have an URI and no servletPath
        return definition != null && definition.isExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled();
    }
    
    public final boolean isMatchingOutgoingResponseModificationExclusion(final String servletPath, final String requestURI) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's flags 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        final ContentModificationExcludeDefinition definition = getMatchingContentModificationExcludeDefinition(servletPath, requestURI);
        return definition != null && definition.isExcludeOutgoingResponsesFromModification();
    }


}
