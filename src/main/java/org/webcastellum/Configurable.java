package org.webcastellum;

import javax.servlet.FilterConfig;

public interface Configurable {

    void setFilterConfig(FilterConfig filterConfig) throws FilterConfigurationException;

    // TODO: add .destroy() method ? 

}
