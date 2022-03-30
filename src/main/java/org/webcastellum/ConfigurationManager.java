package org.webcastellum;

import javax.servlet.FilterConfig;

/**
 * Capable of loading (using ConfigurationLoader implementations) configurations from different sources
 */
public final class ConfigurationManager {

    private static final String PARAM_CONFIGURATION_LOADER = "ConfigurationLoader";

    private final ConfigurationLoader loader;

    public ConfigurationManager(final FilterConfig filterConfig) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FilterConfigurationException {
        this( filterConfig, filterConfig.getInitParameter(PARAM_CONFIGURATION_LOADER) == null ? "org.webcastellum.DefaultConfigurationLoader" : filterConfig.getInitParameter(PARAM_CONFIGURATION_LOADER) );
    }
    public ConfigurationManager(final FilterConfig filterConfig, final String configurationLoaderClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FilterConfigurationException {
        this( filterConfig, Class.forName(configurationLoaderClassName) );
    }
    public ConfigurationManager(final FilterConfig filterConfig, final Class/*Java5<ConfigurationLoader>*/ configurationLoaderClass) throws InstantiationException, IllegalAccessException, FilterConfigurationException {
        this( filterConfig, (ConfigurationLoader) configurationLoaderClass.newInstance() );
    }
    public ConfigurationManager(final FilterConfig filterConfig, final ConfigurationLoader configurationLoader) throws FilterConfigurationException {
        if (configurationLoader == null) throw new NullPointerException("configurationLoader must not be null");
        this.loader = configurationLoader;
        this.loader.setFilterConfig(filterConfig);
    }

    public String getConfigurationValue(final String key) {
        return this.loader.getConfigurationValue(key);
    }

    // Java5 @Override
    public String toString() {
        return "Loading configuration via "+loader;
    }

}
