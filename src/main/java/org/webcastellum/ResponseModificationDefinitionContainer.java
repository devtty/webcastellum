package org.webcastellum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.http.HttpServletRequest;

public final class ResponseModificationDefinitionContainer extends RequestDefinitionContainer/*<ResponseModificationDefinition>*/ {
    
    private static final String KEY_MATCHES_SCRIPTS = "matchesScripts";
    private static final String KEY_MATCHES_TAGS = "matchesTags";
    //private static final String KEY_TAG_NAMES = "tagNames";
    private static final String KEY_URL_CAPTURING_PREFILTER = "urlCapturingPattern@prefilter";
    private static final String KEY_URL_CAPTURING_PATTERN = "urlCapturingPattern";
    private static final String KEY_CAPTURING_GROUP_NUMBERS = "capturingGroupNumbers";
    private static final String KEY_URL_EXCLUSION_PREFILTER = "urlExclusionPattern@prefilter";
    private static final String KEY_URL_EXCLUSION_PATTERN = "urlExclusionPattern";
    private static final String KEY_TAG_EXCLUSION_PREFILTER = "tagExclusionPattern@prefilter";
    private static final String KEY_TAG_EXCLUSION_PATTERN = "tagExclusionPattern";
    private static final String KEY_SCRIPT_EXCLUSION_PREFILTER = "scriptExclusionPattern@prefilter";
    private static final String KEY_SCRIPT_EXCLUSION_PATTERN = "scriptExclusionPattern";
    

    public ResponseModificationDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader, false); // false since ResponseModification-Definitions should (for security reasons) *not* utilize non-standard permutation variants
    }
    
    
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final CustomRequestMatcher customRequestMatcher) {
        return new ResponseModificationDefinition(enabled, identification, description, customRequestMatcher);
    }
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        return new ResponseModificationDefinition(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }

    
    protected void extractAndRemoveSpecificProperties(final /*ResponseModificationDefinition statt RequestDefinition*/RequestDefinition requestDefinition, final Properties properties) throws IllegalRuleDefinitionFormatException {
        final ResponseModificationDefinition responseModificationDefinition = (ResponseModificationDefinition) requestDefinition;
        
        // load custom properties
        {
            final String value = properties.getProperty(KEY_MATCHES_SCRIPTS);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_MATCHES_SCRIPTS+" for rule: "+responseModificationDefinition.getIdentification());
            responseModificationDefinition.setMatchesScripts( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_MATCHES_SCRIPTS);
        }
        {
            final String value = properties.getProperty(KEY_MATCHES_TAGS);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_MATCHES_TAGS+" for rule: "+responseModificationDefinition.getIdentification());
            responseModificationDefinition.setMatchesTags( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_MATCHES_TAGS);
        }
        /*
        {
            String value = properties.getProperty(KEY_TAG_NAMES);
            responseModificationDefinition.clearTagNames();
            if (value != null) { // optional to be backward-compatible (empty ot missing means check against all tags - for backwards-compatibility)
                value = value.trim();
                if (value.length() > 0) {
                    final StringTokenizer tokenizer = new StringTokenizer(value);
                    while (tokenizer.hasMoreTokens()) {
                        final String tagName = tokenizer.nextToken().trim();
                        responseModificationDefinition.addTagName(tagName);
                    }
                }
                // don't forget to remove the used values
                properties.remove(KEY_TAG_NAMES);
            }
        }
        */
        {
            final String value = properties.getProperty(KEY_URL_CAPTURING_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_URL_CAPTURING_PATTERN+" for rule: "+responseModificationDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                responseModificationDefinition.setUrlCapturingPattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_URL_CAPTURING_PATTERN+" for rule: "+responseModificationDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_URL_CAPTURING_PREFILTER);
            if (prefilter != null) responseModificationDefinition.setUrlCapturingPrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_URL_CAPTURING_PATTERN);
            properties.remove(KEY_URL_CAPTURING_PREFILTER);
        }
        {
            final String value = properties.getProperty(KEY_URL_EXCLUSION_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_URL_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                responseModificationDefinition.setUrlExclusionPattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_URL_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_URL_EXCLUSION_PREFILTER);
            if (prefilter != null) responseModificationDefinition.setUrlExclusionPrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_URL_EXCLUSION_PATTERN);
            properties.remove(KEY_URL_EXCLUSION_PREFILTER);
        }
        {
            final String value = properties.getProperty(KEY_TAG_EXCLUSION_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_TAG_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                responseModificationDefinition.setTagExclusionPattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_TAG_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_TAG_EXCLUSION_PREFILTER);
            if (prefilter != null) responseModificationDefinition.setTagExclusionPrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_TAG_EXCLUSION_PATTERN);
            properties.remove(KEY_TAG_EXCLUSION_PREFILTER);
        }
        {
            final String value = properties.getProperty(KEY_SCRIPT_EXCLUSION_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_SCRIPT_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                responseModificationDefinition.setScriptExclusionPattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_SCRIPT_EXCLUSION_PATTERN+" for rule: "+responseModificationDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_SCRIPT_EXCLUSION_PREFILTER);
            if (prefilter != null) responseModificationDefinition.setScriptExclusionPrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_SCRIPT_EXCLUSION_PATTERN);
            properties.remove(KEY_SCRIPT_EXCLUSION_PREFILTER);
        }
        {
            final String value = properties.getProperty(KEY_CAPTURING_GROUP_NUMBERS);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing response-modification specific value: "+KEY_CAPTURING_GROUP_NUMBERS+" for rule: "+responseModificationDefinition.getIdentification());
            try {
                final String[] tokens = WordMatchingUtils.split(value);
                final List/*<Integer>*/ groupNumbers = new ArrayList(tokens.length);
                for (String token : tokens) {
                    final int groupNumber = Integer.parseInt(token.trim());
                    if (groupNumber > responseModificationDefinition.getUrlCapturingPattern().matcher("TEST").groupCount()) throw new IllegalRuleDefinitionFormatException("Configured pattern has no corresponding capturing group for response-modification specific value: "+KEY_CAPTURING_GROUP_NUMBERS+" for rule: "+responseModificationDefinition.getIdentification()+": "+value);
                    groupNumbers.add(new Integer(groupNumber));
                }
                responseModificationDefinition.setCapturingGroupNumbers(groupNumbers);
            } catch(NumberFormatException e) {
                throw new IllegalRuleDefinitionFormatException("Unable to number-parse configured response-modification specific value: "+KEY_CAPTURING_GROUP_NUMBERS+" for rule: "+responseModificationDefinition.getIdentification()+": "+value);
            }
            // don't forget to remove the used values
            properties.remove(KEY_CAPTURING_GROUP_NUMBERS);
        }
    }

    
    public final ResponseModificationDefinition[] getAllMatchingResponseModificationDefinitions(HttpServletRequest request, String servletPath, String contextPath, String pathInfo, String pathTranslated, String clientAddress, String remoteHost, int remotePort, String remoteUser, String authType, String scheme, String method, String protocol, String mimeType, String encoding, int contentLength, Map headerMapVariants, String url, String uri, String serverName, int serverPort, String localAddr, String localName, int localPort, String country, Map cookieMapVariants, String requestedSessionId, Permutation queryStringVariants, Map parameterMapVariants, Map parameterMapExcludingInternalParams) throws CustomRequestMatchingException {
        final RequestDefinition[] definitions = super.getAllMatchingRequestDefinitions(request,
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
        final ResponseModificationDefinition[] results = new ResponseModificationDefinition[definitions.length];
        for (int i=0; i<definitions.length; i++) {
            final RequestDefinition definition = definitions[i];
            results[i] = (ResponseModificationDefinition) definition;
        }
        return results;
    }
    
}
