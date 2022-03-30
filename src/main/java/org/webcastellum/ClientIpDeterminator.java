package org.webcastellum;

import javax.servlet.http.HttpServletRequest;


public interface ClientIpDeterminator extends Configurable {
    
    String determineClientIp(final HttpServletRequest request) throws ClientIpDeterminationException;
    
}
