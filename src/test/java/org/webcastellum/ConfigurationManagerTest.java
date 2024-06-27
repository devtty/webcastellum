package org.webcastellum;

import javax.servlet.FilterConfig;
import static org.junit.Assert.*;
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
            assertEquals("testcomplete", manager.getConfigurationValue("test"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | FilterConfigurationException ex) {
            fail(ex.getMessage());
        }
    }
        
    @Test
    public void testConfigurationManagerWithoutLoader() throws FilterConfigurationException{
        NullPointerException e = assertThrows(NullPointerException.class, () -> new ConfigurationManager(filterConfig, (ConfigurationLoader) null));
        assertEquals("configurationLoader must not be null", e.getMessage());
    }
    
}
