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
    
    @Test(expected = NullPointerException.class)
    public void testExtractMandatoryConfigValueWithoutConfigManager() throws FilterConfigurationException{
        ConfigurationUtils.extractMandatoryConfigValue((ConfigurationManager) null, "test");
    }
    
    @Test(expected = NullPointerException.class)
    public void testExtractMandatoryConfigValueWithPatternWithoutConfigManager() throws FilterConfigurationException{
        ConfigurationUtils.extractMandatoryConfigValue((ConfigurationManager) null, "test", Pattern.compile("regextest"));
    }
    
    @Test(expected = FilterConfigurationException.class)
    public void testExtractMandatoryConfigValueMissing() throws FilterConfigurationException{
        ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test");
    }
    
    @Test(expected = FilterConfigurationException.class)
    public void testExtractMandatoryConfigValueMissingWithPattern() throws FilterConfigurationException{
        ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test", Pattern.compile("regextest"));
    }
    
    @Test
    public void testExtractMandatoryConfigValueWithPattern(){
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        
        boolean exceptionThrown = false;
        try {
            ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1", Pattern.compile("regextest"));
        } catch (FilterConfigurationException ex) {
            exceptionThrown = true;
        }
        
        try {
            assertEquals(ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1", Pattern.compile("testcomplete")), "testcomplete");
        } catch (FilterConfigurationException ex) {
            fail(ex.getMessage());
        }
        
        assertTrue(exceptionThrown);
    }
    
    @Test
    public void testExtractMandatoryConfigValue_ConfigurationManager_String() throws Exception {
        when(filterConfig.getInitParameter("test1")).thenReturn("testcomplete");
        when(filterConfig.getInitParameter("test2")).thenReturn(" testcomplete ");
        
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test1"));
        assertEquals("testcomplete", ConfigurationUtils.extractMandatoryConfigValue(configurationManager, "test2"));
    }
    

    @Test(expected = NullPointerException.class)
    public void testExtractOptionalConfigValueWithoutFilterConfig() throws FilterConfigurationException{
        ConfigurationUtils.extractOptionalConfigValue((FilterConfig) null, "test", "default");
    }
    
    @Test(expected = NullPointerException.class)
    public void testExtractOptionalConfigValueWithoutConfigManager() throws FilterConfigurationException{
        ConfigurationUtils.extractOptionalConfigValue((ConfigurationManager) null, "test", "default");
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

    @Test
    public void testCreateConfigurationManager() throws Exception {
    }
    
}
