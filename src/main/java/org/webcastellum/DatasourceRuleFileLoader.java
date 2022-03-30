package org.webcastellum;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterConfig;
import javax.sql.DataSource;

public final class DatasourceRuleFileLoader extends AbstractSqlRuleFileLoader {
    
    public static final String PARAM_JDBC_DATASOURCE = "RuleFilesJdbcDatasource";
    
    
    private String jdbcDatasource; // something like "java:comp/env/jdbc/myds"
    
    private transient DataSource dataSource;
    
    
    public void setFilterConfig(final FilterConfig filterConfig) throws FilterConfigurationException {
        super.setFilterConfig(filterConfig);
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        this.jdbcDatasource = ConfigurationUtils.extractMandatoryConfigValue(configManager, PARAM_JDBC_DATASOURCE);
    }
    
    
    protected synchronized Connection getConnection() throws SQLException {
        if (this.jdbcDatasource == null) throw new IllegalStateException("FilterConfig must be set before loading rules files");
        if (this.dataSource == null) {
            final Set/*<String>*/ names = new HashSet(3);
            names.add(this.jdbcDatasource);
            if (!this.jdbcDatasource.startsWith("java:comp/env/")) {
                names.add("java:comp/env/"+this.jdbcDatasource);
                names.add("java:comp/env/jdbc/"+this.jdbcDatasource);
            }
            try {
                final InitialContext context = new InitialContext();
                for (final Iterator iter = names.iterator(); iter.hasNext();) {
                    final String name = (String) iter.next();
                    try {
                        this.dataSource = (DataSource) context.lookup(name);
                        break;
                    } catch (NamingException ignored) {}
                }
                if (this.dataSource == null) {
                    throw new SQLException("Unable to load datasource from JNDI: "+this.jdbcDatasource);
                }
            } catch (NamingException ex) {
                ex.printStackTrace();
                throw new SQLException("Unable to load datasource from JNDI: "+this.jdbcDatasource/*, ex*/); // TODO Java5: exception parameter in SQLException constructor with Java5 possible
            }
        }
        assert this.dataSource != null;
        return this.dataSource.getConnection();
    }
    
    
}
