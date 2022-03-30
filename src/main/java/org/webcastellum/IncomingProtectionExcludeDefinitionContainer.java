package org.webcastellum;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class IncomingProtectionExcludeDefinitionContainer extends RequestDefinitionContainer/*<IncomingProtectionExcludeDefinition>*/ {
    
    private static final String KEY_EXCLUDE_FORCE_ENTRANCE_PROTECTION = "excludeForceEntranceProtection";
    private static final String KEY_EXCLUDE_PARAMETER_AND_FORM_PROTECTION = "excludeParameterAndFormProtection";
    private static final String KEY_EXCLUDE_SELECTBOX_FIELD_PROTECTION = "excludeSelectboxFieldProtection";
    private static final String KEY_EXCLUDE_CHECKBOX_FIELD_PROTECTION = "excludeCheckboxFieldProtection";
    private static final String KEY_EXCLUDE_RADIOBUTTON_FIELD_PROTECTION = "excludeRadiobuttonFieldProtection";
    private static final String KEY_EXCLUDE_REFERRER_PROTECTION = "excludeReferrerProtection";
    private static final String KEY_EXCLUDE_SECRET_TOKEN_PROTECTION = "excludeSecretTokenProtection";
    private static final String KEY_EXCLUDE_SESSION_HEADER_BINDING_PROTECTION = "excludeSessionToHeaderBindingProtection";
    private static final String KEY_EXCLUDE_EXTRA_SESSION_TIMEOUT_HANDLING = "excludeExtraSessionTimeoutHandling";
    
    
    public IncomingProtectionExcludeDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader, false); // false since IncomingProtectionExclude-Definitions should (for security reasons) *not* utilize non-standard permutation variants
    }
    
    
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final CustomRequestMatcher customRequestMatcher) {
        return new IncomingProtectionExcludeDefinition(enabled, identification, description, customRequestMatcher);
    }
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        return new IncomingProtectionExcludeDefinition(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }

    
    protected void extractAndRemoveSpecificProperties(final /*IncomingProtectionExclude statt RequestDefinition*/RequestDefinition requestDefinition, final Properties properties) throws IllegalRuleDefinitionFormatException {
        final IncomingProtectionExcludeDefinition incomingProtectionExcludeDefinition = (IncomingProtectionExcludeDefinition) requestDefinition;
        
        // load custom properties
        {
            final String value = properties.getProperty(KEY_EXCLUDE_FORCE_ENTRANCE_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_FORCE_ENTRANCE_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeForceEntranceProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_FORCE_ENTRANCE_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_PARAMETER_AND_FORM_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_PARAMETER_AND_FORM_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeParameterAndFormProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_PARAMETER_AND_FORM_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_SELECTBOX_FIELD_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_SELECTBOX_FIELD_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeSelectboxFieldProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_SELECTBOX_FIELD_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_CHECKBOX_FIELD_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_CHECKBOX_FIELD_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeCheckboxFieldProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_CHECKBOX_FIELD_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_RADIOBUTTON_FIELD_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_RADIOBUTTON_FIELD_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeRadiobuttonFieldProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_RADIOBUTTON_FIELD_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_REFERRER_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_REFERRER_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeReferrerProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_REFERRER_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_SECRET_TOKEN_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_SECRET_TOKEN_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeSecretTokenProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_SECRET_TOKEN_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_SESSION_HEADER_BINDING_PROTECTION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing incoming-protection-exclude specific value: "+KEY_EXCLUDE_SESSION_HEADER_BINDING_PROTECTION+" for rule: "+incomingProtectionExcludeDefinition.getIdentification());
            incomingProtectionExcludeDefinition.setExcludeSessionToHeaderBindingProtection( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_SESSION_HEADER_BINDING_PROTECTION);
        }
        {
            final String value = properties.getProperty(KEY_EXCLUDE_EXTRA_SESSION_TIMEOUT_HANDLING);
            if (value == null){
            	incomingProtectionExcludeDefinition.setExcludeExtraSessionTimeoutHandling(false);
            } else {
            	incomingProtectionExcludeDefinition.setExcludeExtraSessionTimeoutHandling((""+true).equals( value.trim().toLowerCase() ) );
            }
            // don't forget to remove the used values
            properties.remove(KEY_EXCLUDE_EXTRA_SESSION_TIMEOUT_HANDLING);
        }
    }

    public final IncomingProtectionExcludeDefinition getMatchingIncomingProtectionExcludeDefinition(HttpServletRequest request, String servletPath, String contextPath, String pathInfo, String pathTranslated, String clientAddress, String remoteHost, int remotePort, String remoteUser, String authType, String scheme, String method, String protocol, String mimeType, String encoding, int contentLength, Map headerMapVariants, String url, String uri, String serverName, int serverPort, String localAddr, String localName, int localPort, String country, Map cookieMapVariants, String requestedSessionId, Permutation queryStringVariants, Map parameterMapVariants, Map parameterMapExcludingInternalParams) throws CustomRequestMatchingException {
        final RequestDefinition requestDefinition = super.getMatchingRequestDefinition(request,
                                                                                                servletPath,
                                                                                                contextPath,
                                                                                                pathInfo,
                                                                                                pathTranslated,
                                                                                                clientAddress,
                                                                                                remoteHost,
                                                                                                remotePort,
                                                                                                remoteUser,
                                                                                                authType,
                                                                                                scheme,
                                                                                                method,
                                                                                                protocol,
                                                                                                mimeType,
                                                                                                encoding,
                                                                                                contentLength,
                                                                                                headerMapVariants,
                                                                                                url,
                                                                                                uri,
                                                                                                serverName,
                                                                                                serverPort,
                                                                                                localAddr,
                                                                                                localName,
                                                                                                localPort,
                                                                                                country,
                                                                                                cookieMapVariants,
                                                                                                requestedSessionId,
                                                                                                queryStringVariants,
                                                                                                parameterMapVariants, parameterMapExcludingInternalParams,
                                                                                                true, false);
        if (requestDefinition == null) return null;
        return (IncomingProtectionExcludeDefinition) requestDefinition;
    }
    
}
