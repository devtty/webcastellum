package org.webcastellum;

import javax.servlet.FilterConfig;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class FilesystemRuleFileLoaderTest {
    
    private FilterConfig filterConfig;
    
    public FilesystemRuleFileLoaderTest() {
    }
        
    @Before
    public void setUp() {
        filterConfig = Mockito.mock(FilterConfig.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void testSetFilterConfigWithoutConfig() throws Exception {
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        instance.setFilterConfig(null);
    }
    
    @Test(expected = FilterConfigurationException.class)
    public void testSetFilterConfigWithoutInitParam() throws FilterConfigurationException{
        try{
            FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
            instance.setFilterConfig(filterConfig);
        }catch(RuntimeException e){
            assertEquals("Missing mandatory filter init-param: RulesFilesBasePath", e.getMessage());
            throw e;
        }
        fail("FilterConfigurationException not thrown");
    }
    
    @Test(expected = FilterConfigurationException.class)
    public void testSetFilterConfigWithInitParamNull() throws FilterConfigurationException{
        when(filterConfig.getInitParameter("RuleFilesBasePath")).thenReturn(null);
        try{
            FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
            instance.setFilterConfig(filterConfig);
        }catch(RuntimeException e){
            assertEquals("Missing mandatory filter init-param: RuleFilesBasePath", e.getMessage()); //TODO Rule(s)FilesBasePath see WithoutInitParam?
            throw e;
        }
        fail("FilterConfigurationException not thrown");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testLoadRuleFilesWithoutPath() throws FilterConfigurationException, RuleLoadingException{
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn("./wrongdir");
        try{
            FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
            instance.setFilterConfig(filterConfig);
            instance.loadRuleFiles();
        }catch(RuntimeException e){
            assertEquals("Path must be set before loading rules files", e.getMessage());
            throw e;
        }
        fail("IllegalStateException not thrown");
    }
    
    @Test(expected = RuleLoadingException.class)
    public void testLoadRuleFilesWrongDir() throws FilterConfigurationException, RuleLoadingException{
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn("./wrongdir");
        try{
            FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
            instance.setFilterConfig(filterConfig);
            instance.setPath("test");
            instance.loadRuleFiles();
        } catch (RuleLoadingException e) { 
            assertTrue(e.getMessage().contains("Directory does not exist"));
            throw e;
        }
        fail("RuleLoadingException not thrown");
    }

    @Test
    public void testLoadRuleFiles() throws Exception {
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn("src/main/config/rules/");
        
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        instance.setPath("bad-requests");

        RuleFile[] result = instance.loadRuleFiles();
        
        //passes if rule files in src dir could be read
        //test may fail if rules are changed
        assertTrue(result.length > 50);
        assertTrue(result[0].getName().contains("DirectoryTraversal-Direct"));
    }
    
}
