package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class TotalExcludeDefinitionContainer extends SimpleDefinitionContainer {
    
    public TotalExcludeDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }


    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new TotalExcludeDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        // nothing special to do here for total-excludes
    }

    
    
    public final boolean isTotalExclude(final String servletPath, final String requestURI) {
        return getMatchingSimpleDefinition(servletPath, requestURI) != null;
    }


}
