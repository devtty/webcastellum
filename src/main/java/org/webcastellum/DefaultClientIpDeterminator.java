package org.webcastellum;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

public final class DefaultClientIpDeterminator implements ClientIpDeterminator {

    public static final String PARAM_SPLIT_HEADER_VALUE = "DefaultClientIpDeterminatorSplitHeaderValue";
    public static final String PARAM_HEADER_NAME = "DefaultClientIpDeterminatorHeaderName"; 
    public static final String LEGACY_PARAM_HEADER_NAME = "ClientIpDetermination";
    
    private boolean splitHeaderValue = false;
    private String headerName = "";
    
    public static String extractFirstIP(final String headerFetchedClientIpValue) {
        final int posFirstComma = headerFetchedClientIpValue.indexOf(',');
        if (posFirstComma > 0) {
            return headerFetchedClientIpValue.substring(0, posFirstComma - 1).trim();
        }
        return headerFetchedClientIpValue.trim();
    }
    
    @Override
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException { // TODO: use  ConfigurationUtils.extractOptionalConfigValue
        if (filterConfig == null) throw new NullPointerException("filterConfig must not be null");
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        setHeaderName(configManager);
        setSplitHeaderValue(configManager);
    }

    @Override
    public String determineClientIp(final HttpServletRequest request) throws ClientIpDeterminationException {
        final String remoteAddr = request.getRemoteAddr();
        if (this.headerName == null || this.headerName.length() == 0) 
            return remoteAddr;
        final String headerFetchedClientIpValue = request.getHeader(this.headerName);
        if (!this.splitHeaderValue) 
            return headerFetchedClientIpValue != null ? headerFetchedClientIpValue : remoteAddr;
        // in case the header value shall be splitted: (required when a cascade of multiple proxies enhances the value to a comma-separated list - in reverse order of traversal (i.e. closest proxy first))
        if (headerFetchedClientIpValue != null && headerFetchedClientIpValue.length() > 0) {
            return extractFirstIP(headerFetchedClientIpValue);
        }
        return remoteAddr;
    }

    private void setSplitHeaderValue(final ConfigurationManager configManager) {
        String value = configManager.getConfigurationValue(PARAM_SPLIT_HEADER_VALUE);
        if (value == null) 
            value = ""+false;
        this.splitHeaderValue = (""+true).equals( value.trim().toLowerCase() );
    }

    private void setHeaderName(final ConfigurationManager configManager) {
        String value = configManager.getConfigurationValue(PARAM_HEADER_NAME);
        if (value == null) 
            value = configManager.getConfigurationValue(LEGACY_PARAM_HEADER_NAME);
        if (value == null) 
            value = "";
        this.headerName = value.trim();
    }

    
}
