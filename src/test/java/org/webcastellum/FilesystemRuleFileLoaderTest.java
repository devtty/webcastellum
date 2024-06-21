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
    
    @Test
    public void testSetFilterConfigWithoutInitParam() throws FilterConfigurationException{
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Missing mandatory filter init-param: RuleFilesBasePath", e.getMessage());
    }
    
    @Test
    public void testSetFilterConfigWithInitParamNull() throws FilterConfigurationException{
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn(null);
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Missing mandatory filter init-param: RuleFilesBasePath", e.getMessage());
    }
    
    @Test
    public void testLoadRuleFilesWithoutPath() throws FilterConfigurationException, RuleLoadingException{
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn("./wrongdir");
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> instance.loadRuleFiles());
        assertEquals("Path must be set before loading rules files", e.getMessage());
    }
    
    @Test
    public void testLoadRuleFilesWrongDir() throws FilterConfigurationException, RuleLoadingException{
        when(filterConfig.getInitParameter(FilesystemRuleFileLoader.PARAM_RULE_FILES_BASE_PATH)).thenReturn("./wrongdir");
        FilesystemRuleFileLoader instance = new FilesystemRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        instance.setPath("test");
        RuleLoadingException e = assertThrows(RuleLoadingException.class, () -> instance.loadRuleFiles());
        assertTrue(e.getMessage().contains("Directory does not exist"));
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
        
        boolean directoryTraversal = false;
        for(RuleFile f : result){
            if(f.getName().contains("DirectoryTraversal-Direct"))
                directoryTraversal=true;
        }

        assertTrue(directoryTraversal);
    }
    
}
