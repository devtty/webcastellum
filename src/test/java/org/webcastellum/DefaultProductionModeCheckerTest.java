package org.webcastellum;

import javax.servlet.FilterConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class DefaultProductionModeCheckerTest {
    
    private FilterConfig filterConfig;
    
    public DefaultProductionModeCheckerTest() {
    }

    @Before
    public void setUp() {
        filterConfig = Mockito.mock(FilterConfig.class);
    }
    
    @Test
    public void testSetFilterConfigWithNull(){
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        NullPointerException e = assertThrows(NullPointerException.class, () -> instance.setFilterConfig(null));
        assertEquals("filterConfig must not be null", e.getMessage());
    }
    
    @Test
    public void testSetFilterDefaults() throws FilterConfigurationException, ProductionModeCheckingException{
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        instance.setFilterConfig(filterConfig);
        assertTrue(instance.isProductionMode());
    }
    
    @Test
    public void testSetFilterDevelopmentMode() throws FilterConfigurationException, ProductionModeCheckingException{
        when(filterConfig.getInitParameter(DefaultProductionModeChecker.PARAM_PRODUCTION_MODE)).thenReturn("false");
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        instance.setFilterConfig(filterConfig);
        assertFalse(instance.isProductionMode());
    }
    
    @Test
    public void testSetFilterMisConfigured() throws FilterConfigurationException, ProductionModeCheckingException{
        when(filterConfig.getInitParameter(DefaultProductionModeChecker.PARAM_PRODUCTION_MODE)).thenReturn("falrue");
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        instance.setFilterConfig(filterConfig);
        assertFalse(instance.isProductionMode());
    }
    
    @Test
    public void testSetFilterConfigWithCaps() throws FilterConfigurationException, ProductionModeCheckingException{
        when(filterConfig.getInitParameter(DefaultProductionModeChecker.PARAM_PRODUCTION_MODE)).thenReturn("TRUE");
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        instance.setFilterConfig(filterConfig);
        assertTrue(instance.isProductionMode());
    }
    
    @Test
    public void testSetFilterConfigWithCapsTrim() throws FilterConfigurationException, ProductionModeCheckingException{
        when(filterConfig.getInitParameter(DefaultProductionModeChecker.PARAM_PRODUCTION_MODE)).thenReturn(" faLSe ");
        DefaultProductionModeChecker instance = new DefaultProductionModeChecker();
        instance.setFilterConfig(filterConfig);
        assertFalse(instance.isProductionMode());
    }
    
}
