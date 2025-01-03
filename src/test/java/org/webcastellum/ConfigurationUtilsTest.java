package org.webcastellum;

import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class ConfigurationUtilsTest {
    
    private FilterConfig filterConfig;
    private ConfigurationManager configurationManager;
    
    public ConfigurationUtilsTest() {
    }

    @Before
    public void setUp(){
        filterConfig = Mockito.mock(FilterConfig.class);
        try {
            configurationManager = new ConfigurationManager(filterConfig);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | FilterConfigurationException ex) {
            fail(ex.getMessage());
        }
    }
    
    @Test
    public void testExtractMandatoryConfigValueWithoutConfigManager(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> ConfigurationUtils.extractMandatoryConfigValue((ConfigurationManager) null, "test"));
        assertEquals("configurationManager must not be null", npe.getMessage());
    }
    
    @Test
    public void testExtractMandatoryConfigValueWithPatternWithoutConfigManager(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> ConfigurationUtils.extractMandatoryConfigValue((ConfigurationManager) null, "test", Pattern.compile("regextest")));
        assertEquals("configurationManager must not be null", npe.getMessage());
    }
    
    @Test
    public void testExtractMandatoryConfigValueMissing() throws FilterConfigurationException{
        FilterConfigurationException fce = assertThrows(FilterConfigurationException.class, () -> ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test"));
        assertEquals("Missing mandatory filter init-param: test", fce.getMessage());
    }
    
    @Test
    public void testExtractMandatoryConfigValueMissingWithPattern(){
        FilterConfigurationException fce = assertThrows(FilterConfigurationException.class, () -> ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test", Pattern.compile("regextest")));
        assertEquals("Missing mandatory filter init-param: test", fce.getMessage());
    }
    
    @Test
    public void testExtractMandatoryConfigValueWithPattern() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1", Pattern.compile("regextest")));
        
        assertEquals("Filter init-param does not validate against syntax pattern (regextest): test1", e.getMessage());
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1", Pattern.compile("testcomplete")));
    }
    
    @Test
    public void testExtractMandatoryConfigValue_ConfigurationManager_String() throws Exception {
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        when(filterConfig.getInitParameter("test2")).thenReturn(" testcomplete ");
        
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1"));
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test2"));
    }
    
    @Test
    public void testExtractMandatoryConfigValue_WithFilterConfig() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(filterConfig, "test1"));
    }
    
    @Test
    public void testExtractMandatoryConfigValue_WithFilterConfigNull() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(filterConfig, "test1", Pattern.compile("testcomplete")));
    }

    @Test
    public void testExtractOptionalConfigValueWithoutFilterConfig(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> ConfigurationUtils.extractOptionalConfigValue((FilterConfig) null, "test", "default"));
        assertEquals("filterConfig must not be null", npe.getMessage());
    }
    
    @Test
    public void testExtractOptionalConfigValueWithoutConfigManager(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> ConfigurationUtils.extractOptionalConfigValue((ConfigurationManager) null, "test", "default"));
        assertEquals("configurationManager must not be null", npe.getMessage());
    }
    
    @Test
    public void testExtractOptionalConfigValue() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("test1")).thenReturn(null);
        when(filterConfig.getInitParameter("test2")).thenReturn("testcomplete");
        when(filterConfig.getInitParameter("test3")).thenReturn(" testcomplete ");
        
        assertEquals("replacednull", ConfigurationUtils.extractOptionalConfigValue(configurationManager, "test1", "replacednull"));
        assertEquals("testcomplete", ConfigurationUtils.extractOptionalConfigValue(configurationManager, "test2", "default"));
        assertEquals("testcomplete", ConfigurationUtils.extractOptionalConfigValue(configurationManager, "test3", "default"));
                
    }

    @Test
    public void testCheckSyntax(){
        
        try {
            ConfigurationUtils.checkSyntax("key", null, "test");
        } catch (FilterConfigurationException ex) {
            fail("exception without pattern");
        
        }
        boolean exceptionThrown = false;
        
        try {
            ConfigurationUtils.checkSyntax("key", Pattern.compile("[0-9]"), "test");
        } catch (FilterConfigurationException ex) {
            exceptionThrown = true;
        }
        
        assertTrue(exceptionThrown);
    }
    
}
