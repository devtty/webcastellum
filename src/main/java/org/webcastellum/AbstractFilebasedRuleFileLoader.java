package org.webcastellum;

import javax.servlet.FilterConfig;

public abstract class AbstractFilebasedRuleFileLoader extends AbstractRuleFileLoader {

    public static final String PARAM_RULE_FILES_SUFFIX = "RuleFilesSuffix";
    protected static final String DEFAULT_SUFFIX = "wcr";
    protected String suffix = DEFAULT_SUFFIX; // pre-initialized to have a default when testing via mocks

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        this.suffix = ConfigurationUtils.extractOptionalConfigValue(filterConfig, PARAM_RULE_FILES_SUFFIX, DEFAULT_SUFFIX);
        if (!this.suffix.startsWith(".")) this.suffix = "."+this.suffix;
    }
    
    protected boolean isMatchingSuffix(final String filename) {
        if (filename == null) return false;
        final int pos = filename.lastIndexOf('.');
        if (pos == -1) return false;
        return filename.substring(pos).equalsIgnoreCase(this.suffix);
    }

}
