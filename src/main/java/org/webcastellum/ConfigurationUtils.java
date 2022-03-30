package org.webcastellum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;

public final class ConfigurationUtils {

    private ConfigurationUtils() {}



    // TODO: identische Bestandteile der Methoden in eine eigen private Methode refactoren

    public static final String extractMandatoryConfigValue(final ConfigurationManager configurationManager, final String key) throws FilterConfigurationException {
        return extractMandatoryConfigValue(configurationManager, key, null);
    }
    public static final String extractMandatoryConfigValue(final ConfigurationManager configurationManager, final String key, final Pattern syntaxPattern) throws FilterConfigurationException {
        if (configurationManager == null) throw new NullPointerException("configurationManager must not be null");
        String value = configurationManager.getConfigurationValue(key);
        if (value == null) throw new FilterConfigurationException("Missing mandatory filter init-param: "+key);
        value = value.trim();
        checkSyntax(key, syntaxPattern, value);
        return value;
    }

    public static final String extractOptionalConfigValue(final ConfigurationManager configurationManager, final String key, final String defaultValue) throws FilterConfigurationException {
        return extractOptionalConfigValue(configurationManager, key, defaultValue, null);
    }
    public static final String extractOptionalConfigValue(final ConfigurationManager configurationManager, final String key, final String defaultValue, final Pattern syntaxPattern) throws FilterConfigurationException {
        if (configurationManager == null) throw new NullPointerException("configurationManager must not be null");
        String value = configurationManager.getConfigurationValue(key);
        if (value == null) value = defaultValue;
        if (value != null) value = value.trim();
        checkSyntax(key, syntaxPattern, value);
        return value;
    }




    
    public static final String extractMandatoryConfigValue(final FilterConfig filterConfig, final String key) throws FilterConfigurationException {
        return extractMandatoryConfigValue(filterConfig, key, null);
    }
    public static final String extractMandatoryConfigValue(final FilterConfig filterConfig, final String key, final Pattern syntaxPattern) throws FilterConfigurationException {
        if (filterConfig == null) throw new NullPointerException("filterConfig must not be null");
        String value = filterConfig.getInitParameter(key);
        if (value == null) throw new FilterConfigurationException("Missing mandatory filter init-param: "+key);
        value = value.trim();
        checkSyntax(key, syntaxPattern, value);
        return value;
    }

    public static final String extractOptionalConfigValue(final FilterConfig filterConfig, final String key, final String defaultValue) throws FilterConfigurationException {
        return extractOptionalConfigValue(filterConfig, key, defaultValue, null);
    }
    public static final String extractOptionalConfigValue(final FilterConfig filterConfig, final String key, final String defaultValue, final Pattern syntaxPattern) throws FilterConfigurationException {
        if (filterConfig == null) throw new NullPointerException("filterConfig must not be null");
        String value = filterConfig.getInitParameter(key);
        if (value == null) value = defaultValue;
        if (value != null) value = value.trim();
        checkSyntax(key, syntaxPattern, value);
        return value;
    }

    
    
    public static void checkSyntax(final String key, final Pattern syntaxPattern, final String value) throws FilterConfigurationException {
        if (syntaxPattern != null) {
            // perform syntax check
            final Matcher matcher = syntaxPattern.matcher(value);
            if (!matcher.matches()) throw new FilterConfigurationException("Filter init-param does not validate against syntax pattern ("+syntaxPattern+"): "+key);
        }
    }




    public static ConfigurationManager createConfigurationManager(final FilterConfig filterConfig) throws FilterConfigurationException {
        final ConfigurationManager configManager;
        try {
            configManager = new ConfigurationManager(filterConfig);
        } catch (ClassNotFoundException e) {
            throw new FilterConfigurationException(e);
        } catch (InstantiationException e) {
            throw new FilterConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new FilterConfigurationException(e);
        } catch (FilterConfigurationException e) {
            throw new FilterConfigurationException(e);
        } catch (RuntimeException e) {
            throw new FilterConfigurationException(e);
        }
        assert configManager != null;
        return configManager;
    }

    
}
