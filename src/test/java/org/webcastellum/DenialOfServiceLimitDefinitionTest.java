package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import static org.junit.Assert.*;


public class DenialOfServiceLimitDefinitionTest {
    
    WordDictionary servletPathPrefilter = new WordDictionary("a b");
    Pattern servletPathPattern = Pattern.compile("/");
    
    
    @Test
    public void testDenialOfServiceLimitDefinition() {
        DenialOfServiceLimitDefinition def = new DenialOfServiceLimitDefinition(true, "identification", "description", servletPathPrefilter, servletPathPattern, true);
        assertProperties(def, true, servletPathPrefilter, servletPathPattern);
        
        def = new DenialOfServiceLimitDefinition(false, "identification", "description", servletPathPrefilter, servletPathPattern, true);
        assertProperties(def, false, servletPathPrefilter, servletPathPattern);
        
        def = new DenialOfServiceLimitDefinition(true, "identification", "description", null, servletPathPattern, true);
        assertProperties(def, true, null, servletPathPattern);
        
        def = new DenialOfServiceLimitDefinition(true, "identification", "description", new CustomRequestMatcher() {
            @Override
            public void setCustomRequestMatcherProperties(Properties properties) throws CustomRequestMatchingException {
            }

            @Override
            public boolean isRequestMatching(HttpServletRequest request, String clientAddress, String country) throws CustomRequestMatchingException {
                return true;
            }
        });
        
        assertProperties(def, true, null, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDenialOfServiceLimitDefinitionWithoutIdentification() {
        new DenialOfServiceLimitDefinition(true, null, "description", servletPathPrefilter, servletPathPattern, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDenialOfServiceLimitDefinitionWithoutDescription() {
        new DenialOfServiceLimitDefinition(true, "identification", null, servletPathPrefilter, servletPathPattern, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDenialOfServiceLimitDefinitionWithoutPattern() {
        new DenialOfServiceLimitDefinition(true, "identification", "description", servletPathPrefilter, null, true);
    }
    
    private void assertProperties(DenialOfServiceLimitDefinition def, boolean expectedEnabled, WordDictionary expectedPrefilter, Pattern pattern){
        assertTrue(expectedEnabled == def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathPrefilter());
        assertEquals(pattern, def.getServletPathPattern());
        
        assertEquals(0, def.getWatchPeriodMillis());
        assertEquals(0, def.getClientDenialOfServiceLimit());
    }
    
}
