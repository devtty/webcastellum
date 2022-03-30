package org.webcastellum;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class DenialOfServiceLimitDefinitionContainer extends RequestDefinitionContainer/*<DenialOfServiceLimitDefinition>*/ {

    private static final String KEY_WATCH_PERIOD = "watchPeriod";
    private static final String KEY_CLIENT_DENIAL_OF_SERVICE_LIMIT = "clientDenialOfServiceLimit";
    
    
    public DenialOfServiceLimitDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        // TODO: hier wirklich "true" oder nicht doch lieber "false" ??
        super(ruleFileLoader, true); // true since DenialOfService-Definitions should (for security reasons) utilize non-standard permutation variants
    }
    
    
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final CustomRequestMatcher customRequestMatcher) {
        return new DenialOfServiceLimitDefinition(enabled, identification, description, customRequestMatcher);
    }
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        return new DenialOfServiceLimitDefinition(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }

    
    protected void extractAndRemoveSpecificProperties(final /*DenialOfServiceLimitDefinition statt RequestDefinition*/RequestDefinition requestDefinition, final Properties properties) throws IllegalRuleDefinitionFormatException {
        final DenialOfServiceLimitDefinition denialOfServiceLimitDefinition = (DenialOfServiceLimitDefinition) requestDefinition;
        
        // load custom properties - here for DoS settings we don't have useful defauls, so thes properties are mandatory
        {
            final String watchPeriodValue = properties.getProperty(KEY_WATCH_PERIOD);
            if (watchPeriodValue == null) throw new IllegalRuleDefinitionFormatException("Missing watch period value: "+KEY_WATCH_PERIOD+" for rule: "+denialOfServiceLimitDefinition.getIdentification());
            try {
                final int parsedValue = Integer.parseInt(watchPeriodValue);
                if (parsedValue <= 0) throw new IllegalRuleDefinitionFormatException("Configured watch period value must be positive: "+parsedValue);
                denialOfServiceLimitDefinition.setWatchPeriodSeconds(parsedValue);
                // don't forget to remove the used values
                properties.remove(KEY_WATCH_PERIOD);
            } catch (NumberFormatException e) {
                throw new IllegalRuleDefinitionFormatException("Unable to number-parse watch period value into an integer: "+watchPeriodValue, e);
            }
        }
        {
            final String clientDenialOfServiceLimitValue = properties.getProperty(KEY_CLIENT_DENIAL_OF_SERVICE_LIMIT);
            if (clientDenialOfServiceLimitValue == null) throw new IllegalRuleDefinitionFormatException("Missing client DoS limit value: "+KEY_CLIENT_DENIAL_OF_SERVICE_LIMIT+" for rule: "+denialOfServiceLimitDefinition.getIdentification());
            try {
                final int parsedValue = Integer.parseInt(clientDenialOfServiceLimitValue);
                if (parsedValue <= 0) throw new IllegalRuleDefinitionFormatException("Configured client DoS limit value must be positive: "+parsedValue);
                denialOfServiceLimitDefinition.setClientDenialOfServiceLimit(parsedValue);
                // don't forget to remove the used values
                properties.remove(KEY_CLIENT_DENIAL_OF_SERVICE_LIMIT);
            } catch (NumberFormatException e) {
                throw new IllegalRuleDefinitionFormatException("Unable to number-parse client DoS limit value into an integer: "+clientDenialOfServiceLimitValue, e);
            }
        }
    }


    public final DenialOfServiceLimitDefinition getMatchingDenialOfServiceLimitDefinition(HttpServletRequest request, String servletPath, String contextPath, String pathInfo, String pathTranslated, String clientAddress, String remoteHost, int remotePort, String remoteUser, String authType, String scheme, String method, String protocol, String mimeType, String encoding, int contentLength, Map headerMapVariants, String url, String uri, String serverName, int serverPort, String localAddr, String localName, int localPort, String country, Map cookieMapVariants, String requestedSessionId, Permutation queryStringVariants, Map parameterMapVariants, Map parameterMapExcludingInternalParams) throws CustomRequestMatchingException {
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
        return (DenialOfServiceLimitDefinition) requestDefinition;
    }
    
    
}
