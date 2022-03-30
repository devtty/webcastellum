package org.webcastellum;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class RenewSessionAndTokenPointDefinitionContainer extends RequestDefinitionContainer/*<RenewSessionAndTokenPointDefinition>*/ {
    
    private static final String KEY_RENEW_SESSION = "renewSession";
    private static final String KEY_RENEW_SECRET_TOKEN = "renewSecretToken";
    private static final String KEY_RENEW_PARAM_AND_FORM_TOKEN = "renewParamAndFormToken";
    private static final String KEY_RENEW_CRYPTO_KEY = "renewCryptoKey";
    

    public RenewSessionAndTokenPointDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader, true); // true since RenewSessionAndToken-Definitions should (for security reasons) utilize non-standard permutation variants
    }
    
    
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final CustomRequestMatcher customRequestMatcher) {
        return new RenewSessionAndTokenPointDefinition(enabled, identification, description, customRequestMatcher);
    }
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        return new RenewSessionAndTokenPointDefinition(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }

    
    protected void extractAndRemoveSpecificProperties(final /*RenewSessionAndTokenPointDefinition statt RequestDefinition*/RequestDefinition requestDefinition, final Properties properties) throws IllegalRuleDefinitionFormatException {
        final RenewSessionAndTokenPointDefinition renewSessionAndTokenPointDefinition = (RenewSessionAndTokenPointDefinition) requestDefinition;
        
        // load custom properties
        {
            final String value = properties.getProperty(KEY_RENEW_SESSION);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing renew-session-and-token-point specific value: "+KEY_RENEW_SESSION+" for rule: "+renewSessionAndTokenPointDefinition.getIdentification());
            renewSessionAndTokenPointDefinition.setRenewSession( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_RENEW_SESSION);
        }
        {
            final String value = properties.getProperty(KEY_RENEW_SECRET_TOKEN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing renew-session-and-token-point specific value: "+KEY_RENEW_SECRET_TOKEN+" for rule: "+renewSessionAndTokenPointDefinition.getIdentification());
            renewSessionAndTokenPointDefinition.setRenewSecretToken( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_RENEW_SECRET_TOKEN);
        }
        {
            final String value = properties.getProperty(KEY_RENEW_PARAM_AND_FORM_TOKEN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing renew-session-and-token-point specific value: "+KEY_RENEW_PARAM_AND_FORM_TOKEN+" for rule: "+renewSessionAndTokenPointDefinition.getIdentification());
            renewSessionAndTokenPointDefinition.setRenewParamAndFormToken( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_RENEW_PARAM_AND_FORM_TOKEN);
        }
        {
            final String value = properties.getProperty(KEY_RENEW_CRYPTO_KEY);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing renew-session-and-token-point specific value: "+KEY_RENEW_CRYPTO_KEY+" for rule: "+renewSessionAndTokenPointDefinition.getIdentification());
            renewSessionAndTokenPointDefinition.setRenewCryptoKey( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_RENEW_CRYPTO_KEY);
        }
    }

    
    public final RenewSessionAndTokenPointDefinition getMatchingRenewSessionAndTokenPointDefinition(HttpServletRequest request, String servletPath, String contextPath, String pathInfo, String pathTranslated, String clientAddress, String remoteHost, int remotePort, String remoteUser, String authType, String scheme, String method, String protocol, String mimeType, String encoding, int contentLength, Map headerMapVariants, String url, String uri, String serverName, int serverPort, String localAddr, String localName, int localPort, String country, Map cookieMapVariants, String requestedSessionId, Permutation queryStringVariants, Map parameterMapVariants, Map parameterMapExcludingInternalParams) throws CustomRequestMatchingException {
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
        return (RenewSessionAndTokenPointDefinition) requestDefinition;
    }
    
}
