package org.webcastellum;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public final class CaptchaPointDefinitionContainer extends RequestDefinitionContainer/*<CaptchaPointDefinition>*/ {
    
    private static final String KEY_CAPTCHA_PAGE_CONTENT = "captchaPageContent";
    private static final String KEY_CAPTCHA_IMAGE_HTML = "captchaImageHTML";
    private static final String KEY_CAPTCHA_FORM_HTML = "captchaFormHTML";
    

    public CaptchaPointDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader, true); // true since CaptchaPoint-Definitions should (for security reasons) utilize non-standard permutation variants
    }
    
    
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final CustomRequestMatcher customRequestMatcher) {
        return new CaptchaPointDefinition(enabled, identification, description, customRequestMatcher);
    }
    protected RequestDefinition createRequestDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        return new CaptchaPointDefinition(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }

    
    protected void extractAndRemoveSpecificProperties(final /*CaptchaPointDefinition statt RequestDefinition*/RequestDefinition requestDefinition, final Properties properties) throws IllegalRuleDefinitionFormatException {
        final CaptchaPointDefinition captchaPointDefinition = (CaptchaPointDefinition) requestDefinition;
        
        // load custom properties
        {
            final String value = properties.getProperty(KEY_CAPTCHA_PAGE_CONTENT, "org/webcastellum/captcha.html");
            captchaPointDefinition.setCaptchaPageContent(value.trim());
            // don't forget to remove the used values
            properties.remove(KEY_CAPTCHA_PAGE_CONTENT);
        }
        {
            final String value = properties.getProperty(KEY_CAPTCHA_IMAGE_HTML, "<img src=\"{0}\" width=\"{1}\" height=\"{2}\" border=\"1\" />");
            captchaPointDefinition.setCaptchaImageHTML(value.trim());
            // don't forget to remove the used values
            properties.remove(KEY_CAPTCHA_IMAGE_HTML);
        }
        {
            final String value = properties.getProperty(KEY_CAPTCHA_FORM_HTML, "<form action=\"{0}\" method=\"{1}\"><input type=\"text\" name=\"{2}\" /><input type=\"submit\" value=\"OK\" onclick=\"this.disabled=true\" /><input type=\"hidden\" name=\"{3}\" value=\"{4}\" /></form>");
            captchaPointDefinition.setCaptchaFormHTML(value.trim());
            // don't forget to remove the used values
            properties.remove(KEY_CAPTCHA_FORM_HTML);
        }
    }

    
    public final CaptchaPointDefinition getMatchingCaptchaPointDefinition(HttpServletRequest request, String servletPath, String contextPath, String pathInfo, String pathTranslated, String clientAddress, String remoteHost, int remotePort, String remoteUser, String authType, String scheme, String method, String protocol, String mimeType, String encoding, int contentLength, Map headerMapVariants, String url, String uri, String serverName, int serverPort, String localAddr, String localName, int localPort, String country, Map cookieMapVariants, String requestedSessionId, Permutation queryStringVariants, Map parameterMapVariants, Map parameterMapExcludingInternalParams) throws CustomRequestMatchingException {
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
        return (CaptchaPointDefinition) requestDefinition;
    }
    
}
