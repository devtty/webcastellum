package org.webcastellum;

import javax.servlet.FilterConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ClasspathZipRuleFileLoaderTest {
    
    private FilterConfig filterConfig;
 
    public ClasspathZipRuleFileLoaderTest() {
    }
    
    @Before
    public void setUp() {
        filterConfig = Mockito.mock(FilterConfig.class);
    }
    
    @Test
    public void testSetFilterConfigWithoutConfig(){
        ClasspathZipRuleFileLoader instance = new ClasspathZipRuleFileLoader();
        NullPointerException e = assertThrows(NullPointerException.class, () -> instance.setFilterConfig(null));
        assertEquals("filterConfig must not be null", e.getMessage());
    }
    
    @Test
    public void testLoadRulesWithoutPath() throws FilterConfigurationException{
        ClasspathZipRuleFileLoader instance = new ClasspathZipRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> instance.loadRuleFiles());
        assertEquals("Path must be set before loading rules files", e.getMessage());
    }
    
    @Test
    public void testLoadRuleFiles() throws FilterConfigurationException, RuleLoadingException{
        ClasspathZipRuleFileLoader instance = new ClasspathZipRuleFileLoader();
        instance.setFilterConfig(filterConfig);
        instance.setPath("bad-requests");
        RuleFile[] result = instance.loadRuleFiles();
        
        assertTrue(result.length > 50);
        
        boolean directoryTraversal = false;
        for(RuleFile f : result){
            if(f.getName().contains("DirectoryTraversal-Direct"))
                directoryTraversal=true;
        }

        assertTrue(directoryTraversal);
    }
    
}
