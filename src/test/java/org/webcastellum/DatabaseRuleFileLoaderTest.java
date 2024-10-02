package org.webcastellum;

import java.sql.SQLException;
import javax.servlet.FilterConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class DatabaseRuleFileLoaderTest {
    
    private FilterConfig filterConfig;
    
    @Before
    public void setUp() {
        filterConfig = Mockito.mock(FilterConfig.class);
    }
    
    @Test
    public void testSetFilterConfigWithoutConfig(){
        DatabaseRuleFileLoader instance = new DatabaseRuleFileLoader();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(null));
        assertTrue(e.getMessage().contains("NullPointerException"));
        assertTrue(e.getMessage().contains(" because \"filterConfig\" is null"));
    }
    
    @Test
    public void testSetFilterConfigWithoutDriverInClasspath() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("RuleFilesJdbcTable")).thenReturn("table");
        when(filterConfig.getInitParameter("RuleFilesJdbcColumnPath")).thenReturn("coulmnPath");
        when(filterConfig.getInitParameter("RuleFilesJdbcColumnFilename")).thenReturn("filename");
        when(filterConfig.getInitParameter("RuleFilesJdbcColumnPropertyKey")).thenReturn("propertyKey");
        when(filterConfig.getInitParameter("RuleFilesJdbcColumnPropertyValue")).thenReturn("propertyValue");
        when(filterConfig.getInitParameter("RuleFilesJdbcDriver")).thenReturn("jdbcDriver");
        when(filterConfig.getInitParameter("RuleFilesJdbcUrl")).thenReturn("jdbcUrl");
        when(filterConfig.getInitParameter("RuleFilesJdbcUser")).thenReturn("jdbcUser");
        when(filterConfig.getInitParameter("RuleFilesJdbcPassword")).thenReturn("jdbcPassword");
        DatabaseRuleFileLoader instance = new DatabaseRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        SQLException e = assertThrows(SQLException.class, () -> instance.getConnection());
        assertEquals("Unable to load JDBC driver: jdbcDriver", e.getMessage());
    }
    
}
