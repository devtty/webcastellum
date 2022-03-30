package org.webcastellum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.FilterConfig;

public final class DatabaseRuleFileLoader extends AbstractSqlRuleFileLoader {
    
    public static final String PARAM_JDBC_DRIVER = "RuleFilesJdbcDriver";
    public static final String PARAM_JDBC_URL = "RuleFilesJdbcUrl";
    public static final String PARAM_JDBC_USER = "RuleFilesJdbcUser";
    public static final String PARAM_JDBC_PASSWORD = "RuleFilesJdbcPassword";
    

    private String jdbcDriver, jdbcURL, jdbcUser, jdbcPassword;
    
    
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        super.setFilterConfig(filterConfig);
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        this.jdbcDriver = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_DRIVER);
        this.jdbcURL = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_URL);
        this.jdbcUser = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_USER);
        this.jdbcPassword = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_PASSWORD);
    }
    
    
    protected Connection getConnection() throws SQLException {
        if (this.jdbcURL == null || this.jdbcUser == null || this.jdbcPassword == null) throw new IllegalStateException("FilterConfig must be set before loading rules files");
        try {
            Class.forName(this.jdbcDriver);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new SQLException("Unable to load JDBC driver"/*, ex*/); // TODO Java5: exception parameter in SQLException constructor with Java5 possible
        }
        return DriverManager.getConnection(this.jdbcURL, this.jdbcUser, this.jdbcPassword);
    }

    
}
