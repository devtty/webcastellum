package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class SizeLimitDefinitionContainer extends SimpleDefinitionContainer {
    
    private static final String KEY_MAX_HEADER_COUNT = "maxHeaderCount";
    private static final String KEY_MAX_COOKIE_COUNT = "maxCookieCount";
    private static final String KEY_MAX_REQUEST_PARAM_COUNT = "maxRequestParamCount";
    private static final String KEY_MAX_QUERY_STRING_LENGTH = "maxQueryStringLength";
    private static final String KEY_MAX_HEADER_NAME_LENGTH = "maxHeaderNameLength";
    private static final String KEY_MAX_HEADER_VALUE_LENGTH = "maxHeaderValueLength";
    private static final String KEY_MAX_COOKIE_NAME_LENGTH = "maxCookieNameLength";
    private static final String KEY_MAX_COOKIE_VALUE_LENGTH = "maxCookieValueLength";
    private static final String KEY_MAX_REQUEST_PARAM_NAME_LENGTH = "maxRequestParamNameLength";
    private static final String KEY_MAX_REQUEST_PARAM_VALUE_LENGTH = "maxRequestParamValueLength";
    private static final String KEY_MAX_TOTAL_HEADER_SIZE = "maxTotalHeaderSize";
    private static final String KEY_MAX_TOTAL_COOKIE_SIZE = "maxTotalCookieSize";
    private static final String KEY_MAX_TOTAL_REQUEST_PARAM_SIZE = "maxTotalRequestParamSize";
    
    
    public SizeLimitDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }
    
    
    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new SizeLimitDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        SizeLimitDefinition sizeLimitDefinition = (SizeLimitDefinition) definition; // TODO: Java5 infer type SizeLimitDefinition via Generics instead of downcast
        sizeLimitDefinition.setMaxHeaderCount( getValueAndRemoveKey(properties,KEY_MAX_HEADER_COUNT) );
        sizeLimitDefinition.setMaxCookieCount( getValueAndRemoveKey(properties,KEY_MAX_COOKIE_COUNT) );
        sizeLimitDefinition.setMaxRequestParamCount( getValueAndRemoveKey(properties,KEY_MAX_REQUEST_PARAM_COUNT) );
        sizeLimitDefinition.setMaxQueryStringLength( getValueAndRemoveKey(properties,KEY_MAX_QUERY_STRING_LENGTH) );
        sizeLimitDefinition.setMaxHeaderNameLength( getValueAndRemoveKey(properties,KEY_MAX_HEADER_NAME_LENGTH) );
        sizeLimitDefinition.setMaxHeaderValueLength( getValueAndRemoveKey(properties,KEY_MAX_HEADER_VALUE_LENGTH) );
        sizeLimitDefinition.setMaxCookieNameLength( getValueAndRemoveKey(properties,KEY_MAX_COOKIE_NAME_LENGTH) );
        sizeLimitDefinition.setMaxCookieValueLength( getValueAndRemoveKey(properties,KEY_MAX_COOKIE_VALUE_LENGTH) );
        sizeLimitDefinition.setMaxRequestParamNameLength( getValueAndRemoveKey(properties,KEY_MAX_REQUEST_PARAM_NAME_LENGTH) );
        sizeLimitDefinition.setMaxRequestParamValueLength( getValueAndRemoveKey(properties,KEY_MAX_REQUEST_PARAM_VALUE_LENGTH) );
        sizeLimitDefinition.setMaxTotalHeaderSize( getValueAndRemoveKey(properties,KEY_MAX_TOTAL_HEADER_SIZE) );
        sizeLimitDefinition.setMaxTotalCookieSize( getValueAndRemoveKey(properties,KEY_MAX_TOTAL_COOKIE_SIZE) );
        sizeLimitDefinition.setMaxTotalRequestParamSize( getValueAndRemoveKey(properties,KEY_MAX_TOTAL_REQUEST_PARAM_SIZE) );
    }
    
    private int getValueAndRemoveKey(final Properties properties, final String key) throws IllegalRuleDefinitionFormatException {
        final String value = properties.getProperty(key);
        if (value == null) return Integer.MAX_VALUE; // this default makes sense for limit definitions
        try {
            final int parsed = Integer.parseInt(value);
            if (parsed <= 0) throw new IllegalRuleDefinitionFormatException("Configured limit value must be positive: "+parsed);
            properties.remove(key);
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalRuleDefinitionFormatException("Unable to number-parse configured limit value into an integer: "+value, e);
        }
    }

    
    public final boolean isSizeLimitExceeded(final String servletPath, final String requestURI, 
            final int headerCount, final int cookieCount, final int requestParamCount, 
            final int queryStringLength,
            final int greatestHeaderNameLength, final int greatestHeaderValueLength, final int totalHeaderSize, 
            final int greatestCookieNameLength, final int greatestCookieValueLength, final int totalCookieSize, 
            final int greatestRequestParamNameLength, final int greatestRequestParamValueLength, final int totalRequestParamSize) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's limits 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        final SizeLimitDefinition sizeLimitDefinitionToCheckForExceedings = (SizeLimitDefinition) getMatchingSimpleDefinition(servletPath, requestURI); // TODO: Java5 infer type SizeLimitDefinition via Generics instead of downcast
        if (sizeLimitDefinitionToCheckForExceedings == null) return false;
        // now we've got the first definition that matches according to its servletPathOrRequestURI property, so check it now for its limits
        return headerCount > sizeLimitDefinitionToCheckForExceedings.getMaxHeaderCount()
                || cookieCount > sizeLimitDefinitionToCheckForExceedings.getMaxCookieCount()
                || requestParamCount > sizeLimitDefinitionToCheckForExceedings.getMaxRequestParamCount()
                || queryStringLength > sizeLimitDefinitionToCheckForExceedings.getMaxQueryStringLength()
                || greatestHeaderNameLength > sizeLimitDefinitionToCheckForExceedings.getMaxHeaderNameLength()
                || greatestHeaderValueLength > sizeLimitDefinitionToCheckForExceedings.getMaxHeaderValueLength()
                || greatestCookieNameLength > sizeLimitDefinitionToCheckForExceedings.getMaxCookieNameLength()
                || greatestCookieValueLength > sizeLimitDefinitionToCheckForExceedings.getMaxCookieValueLength()
                || greatestRequestParamNameLength > sizeLimitDefinitionToCheckForExceedings.getMaxRequestParamNameLength()
                || greatestRequestParamValueLength > sizeLimitDefinitionToCheckForExceedings.getMaxRequestParamValueLength()
                || totalHeaderSize > sizeLimitDefinitionToCheckForExceedings.getMaxTotalHeaderSize()
                || totalCookieSize > sizeLimitDefinitionToCheckForExceedings.getMaxTotalCookieSize()
                || totalRequestParamSize > sizeLimitDefinitionToCheckForExceedings.getMaxTotalRequestParamSize();
    }


}
