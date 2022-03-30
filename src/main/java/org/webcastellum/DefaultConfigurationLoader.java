package org.webcastellum;

import javax.servlet.FilterConfig;

/**
 * Default configuration loader to access filter config init-params
 */
public class DefaultConfigurationLoader implements ConfigurationLoader {

    private static final boolean DEBUG = false;

    private FilterConfig filterConfig;

    public DefaultConfigurationLoader() {
        if (DEBUG) System.out.println("Created new DefaultConfigurationLoader");
    }

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        if (DEBUG) System.out.println("Setting filterConfig");
        this.filterConfig = filterConfig;
    }

    public String getConfigurationValue(final String key) {
        if (this.filterConfig == null) throw new IllegalStateException("filterConfig must be set before fetching configuration values");
        if (DEBUG) System.out.println("Fetching config (via DefaultConfigurationLoader) for: "+key);
        return this.filterConfig.getInitParameter(key);
    }

    // Java5 @Override
    public String toString() {
        return "web.xml filter init parameters";
    }

}
