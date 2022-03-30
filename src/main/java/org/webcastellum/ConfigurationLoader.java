package org.webcastellum;

/**
 * Interface for loading configuration from a specific source
 */
public interface ConfigurationLoader extends Configurable {

    String getConfigurationValue(final String key);

}
