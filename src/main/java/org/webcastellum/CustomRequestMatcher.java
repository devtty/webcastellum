package org.webcastellum;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public interface CustomRequestMatcher {

    void setCustomRequestMatcherProperties(Properties properties) throws CustomRequestMatchingException;

    // NOTE: custom request matchers have also access to the temporarily request parameters injected by WebCastellum
    boolean isRequestMatching(HttpServletRequest request, String clientAddress, String country) throws CustomRequestMatchingException;
    
}
