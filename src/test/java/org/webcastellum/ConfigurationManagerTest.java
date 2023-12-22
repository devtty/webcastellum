package org.webcastellum;

import javax.servlet.FilterConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class ConfigurationManagerTest {
    
    private FilterConfig filterConfig;
    
    public ConfigurationManagerTest() {
    }

    @Before
    public void setUp(){
        filterConfig = Mockito.mock(FilterConfig.class);   
    }
    
    @Test
    public void testConfigurationManagerWithoutConfig(){
        when(filterConfig.getInitParameter("ConfigurationLoader")).thenReturn(null);
        when(filterConfig.getInitParameter("test")).thenReturn("testcomplete");
        try {
            ConfigurationManager manager = new ConfigurationManager(filterConfig);
            assertEquals(manager.getConfigurationValue("test"), "testcomplete");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | FilterConfigurationException ex) {
            fail(ex.getMessage());
        }
    }
        
    @Test(expected = NullPointerException.class)
    public void testConfigurationManagerWithoutLoader() throws FilterConfigurationException{
        ConfigurationManager manager = new ConfigurationManager(filterConfig, (ConfigurationLoader) null);
    }
    
}
