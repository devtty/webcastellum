package org.webcastellum;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;

/**
 * Default configuration loader to access filter config init-params
 */
public class DefaultConfigurationLoader implements ConfigurationLoader {

    private static final Logger LOGGER = Logger.getLogger(DefaultConfigurationLoader.class.getName());
    
    private FilterConfig filterConfig;

    public DefaultConfigurationLoader() {
        LOGGER.log(Level.FINE, "Created new DefaultConfigurationLoader");
    }

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        LOGGER.log(Level.FINE, "Setting filterConfig");
        this.filterConfig = filterConfig;
    }

    public String getConfigurationValue(final String key) {
        if (this.filterConfig == null) 
            throw new IllegalStateException("filterConfig must be set before fetching configuration values");
        
        LOGGER.log(Level.FINE, "Fetching config (via DefaultConfigurationLoader) for: {0}", key);
        return this.filterConfig.getInitParameter(key);
    }

    // Java5 @Override
    public String toString() {
        return "web.xml filter init parameters";
    }

}
