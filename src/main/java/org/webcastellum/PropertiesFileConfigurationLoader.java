package org.webcastellum;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterConfig;

/**
 * Special configuration loader to access a properties file
 */
public class PropertiesFileConfigurationLoader implements ConfigurationLoader {

    private static final String PARAM_PROPERTIES_FILE = "PropertiesFileConfigurationLoader_File";
    private static final String PARAM_PROPERTIES_FALLBACK = "PropertiesFileConfigurationLoader_FallbackToWebXml";

    private static final Logger LOGGER = Logger.getLogger(PropertiesFileConfigurationLoader.class.getName());

    private Properties properties;
    private String filename;
    private FilterConfig filterConfig;


    public PropertiesFileConfigurationLoader() {
        LOGGER.log(Level.FINE, "Created new PropertiesFileConfigurationLoader");
    }

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        LOGGER.log(Level.FINE,"Setting filterConfig");
        // Not using ConfigurationManager here since this class here is itself a configuration loader !
        { // read file pointer
            this.filename = ConfigurationUtils.extractMandatoryConfigValue(filterConfig, PARAM_PROPERTIES_FILE); // yes, read directly hard via filterConfig since we're inside a configuration loader
            this.properties = new Properties();
            InputStream input = null;
            try {
                input = new BufferedInputStream( new FileInputStream(this.filename) );
                this.properties.load(input);
            } catch(IOException e) {
                throw new FilterConfigurationException("Unable to load properties file: "+this.filename, e);
            } finally {
                if (input != null) try { input.close(); } catch (IOException ignored) {}
            }
        }
        { // read fallback to web.xml flag
            final boolean useWebXmlAsFallback = (""+true).equalsIgnoreCase(ConfigurationUtils.extractOptionalConfigValue(filterConfig, PARAM_PROPERTIES_FALLBACK, ""+true)); // yes, read directly hard via filterConfig since we're inside a configuration loader
            this.filterConfig = useWebXmlAsFallback ? filterConfig : null;
        }
    }

    public String getConfigurationValue(final String key) {
        LOGGER.log(Level.FINE, "Fetching config (via PropertiesFileConfigurationLoader) for: {0}", key);
        String result = this.properties.getProperty(key);
        if (this.filterConfig != null && result == null) { // use web.xml fallback
            result = this.filterConfig.getInitParameter(key);
        }
        return result;
    }

    // Java5 @Override
    public String toString() {
        return "properties file "+this.filename;
    }

}
