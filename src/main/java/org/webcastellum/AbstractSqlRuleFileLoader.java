package org.webcastellum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;

public abstract class AbstractSqlRuleFileLoader extends AbstractRuleFileLoader {

    public static final String VALID_DATABASE_SYNTAX = "[a-zA-Z0-9_\\.]{1,250}";

    public static final String PARAM_JDBC_TABLE = "RuleFilesJdbcTable";
    public static final String PARAM_JDBC_COLUMN_PATH = "RuleFilesJdbcColumnPath";
    public static final String PARAM_JDBC_COLUMN_FILENAME = "RuleFilesJdbcColumnFilename";
    public static final String PARAM_JDBC_COLUMN_PROPERTY_KEY = "RuleFilesJdbcColumnPropertyKey";
    public static final String PARAM_JDBC_COLUMN_PROPERTY_VALUE = "RuleFilesJdbcColumnPropertyValue";
    
    
    protected String table, columnPath, columnFilename, columnPropertyKey, columnPropertyValue;
    

    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        final Pattern allowedDatabaseCharacters = Pattern.compile(VALID_DATABASE_SYNTAX);
        this.table = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_TABLE, allowedDatabaseCharacters);
        this.columnPath = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_COLUMN_PATH, allowedDatabaseCharacters);
        this.columnFilename = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_COLUMN_FILENAME, allowedDatabaseCharacters);
        this.columnPropertyKey = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_COLUMN_PROPERTY_KEY, allowedDatabaseCharacters);
        this.columnPropertyValue = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_COLUMN_PROPERTY_VALUE, allowedDatabaseCharacters);
    }   
    


    
    public final RuleFile[] loadRuleFiles() throws RuleLoadingException {
        if (this.path == null) throw new IllegalStateException("Path must be set before loading rules files");
        try {
            final List<RuleFile> rules = new ArrayList<>();
            
            Connection connection = null; 
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = getConnection();
                preparedStatement = connection.prepareStatement("SELECT "+this.columnFilename+", "+this.columnPropertyKey+", "+this.columnPropertyValue+" FROM "+this.table+" WHERE "+this.columnPath+" = ? ORDER BY "+this.columnFilename);
                preparedStatement.setString(1, this.path);
                resultSet = preparedStatement.executeQuery();
                
                String currentFilename = null;
                Properties properties = null;
                while ( resultSet.next() ) {
                    final String filename = resultSet.getString(this.columnFilename);
                    if (filename == null) throw new RuleLoadingException("Filename column must not have NULL values in database");
                    if (!filename.equals(currentFilename)) {
                        currentFilename = filename;
                        properties = new Properties();
                        rules.add( new RuleFile(currentFilename,properties) );
                    }
                    
                    final String key = resultSet.getString(this.columnPropertyKey);
                    final String value = resultSet.getString(this.columnPropertyValue);
                    if (key == null) throw new RuleLoadingException("Property key column must not have NULL values in database");
                    if (value == null) throw new RuleLoadingException("Property value column must not have NULL values in database");
                    assert properties != null;
                    properties.setProperty(key, value);
                }
                
            } finally {
                if (resultSet != null) try { resultSet.close(); } catch (SQLException ignored) {}
                if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException ignored) {}
                if (connection != null) try { connection.close(); } catch (SQLException ignored) {}
            }
            
            return (RuleFile[])rules.toArray(new RuleFile[0]);
        } catch (Exception e) {
            throw new RuleLoadingException(e);
        }
    }

    
    protected abstract Connection getConnection() throws SQLException;
    
}
