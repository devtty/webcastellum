package org.webcastellum;

import javax.servlet.FilterConfig;

public final class DefaultProductionModeChecker implements ProductionModeChecker {

    public static final String PARAM_PRODUCTION_MODE = "DefaultProductionModeCheckerValue";
    
    private boolean productionMode = true;
    
    @Override
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException { // TODO: use  ConfigurationUtils.extractOptionalConfigValue
        if (filterConfig == null) throw new NullPointerException("filterConfig must not be null");
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        String value = configManager.getConfigurationValue(PARAM_PRODUCTION_MODE);
        if (value == null) value = ""+true;
        this.productionMode = "true".equalsIgnoreCase(value);
    }

    @Override
    public boolean isProductionMode() throws ProductionModeCheckingException {
        return this.productionMode;
    }

}
