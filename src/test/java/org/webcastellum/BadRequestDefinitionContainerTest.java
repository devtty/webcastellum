package org.webcastellum;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class BadRequestDefinitionContainerTest {
        
    @Test
    public void testCreateRequestDefinition_4args() {
        RuleFileLoader ruleFileLoader = new ClasspathZipRuleFileLoader();
        BadRequestDefinitionContainer instance = new BadRequestDefinitionContainer(ruleFileLoader);
        
        //first test different NPEs
        NullPointerException npe = assertThrows(NullPointerException.class, () -> instance.createRequestDefinition(true, null, null, null));
        assertEquals("identification must not be null", npe.getMessage());
        
        npe = assertThrows(NullPointerException.class, () -> instance.createRequestDefinition(true, "identification", null, null));
        assertEquals("description must not be null", npe.getMessage());
        
        //create simple result and test
        RequestDefinition result = instance.createRequestDefinition(true, "identification", "description", new CustomRequestMatcher() {
            @Override
            public void setCustomRequestMatcherProperties(Properties properties) throws CustomRequestMatchingException {
            }

            @Override
            public boolean isRequestMatching(HttpServletRequest request, String clientAddress, String country) throws CustomRequestMatchingException {
                return country.contains("DE");
            }
        });
        
        assertEquals("description", result.getDescription());
        assertEquals("identification", result.getIdentification());
        assertTrue(result.isEnabled());
        try {
            assertTrue(result.getCustomRequestMatcher().isRequestMatching(null, "test", "DE"));
            assertFalse(result.getCustomRequestMatcher().isRequestMatching(null, "test", "FR"));
        } catch (CustomRequestMatchingException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testCreateRequestDefinition_6args() {
        RuleFileLoader ruleFileLoader = new ClasspathZipRuleFileLoader();
        BadRequestDefinitionContainer instance = new BadRequestDefinitionContainer(ruleFileLoader);
        
        //first test different NPEs
        NullPointerException npe = assertThrows(NullPointerException.class, () -> instance.createRequestDefinition(true, null, null, null, null, true));
        assertEquals("identification must not be null", npe.getMessage());
        
        npe = assertThrows(NullPointerException.class, () -> instance.createRequestDefinition(true, "identification", null, null, null, true));
        assertEquals("description must not be null", npe.getMessage());
        
        npe = assertThrows(NullPointerException.class, () -> instance.createRequestDefinition(true, "identification", "description", null, null, true));
        assertEquals("servletPathPattern must not be null", npe.getMessage());
        
        //create simple result and test
        RequestDefinition result = instance.createRequestDefinition(true, "identification", "description", WordDictionary.createInstance("test1,test22"), Pattern.compile("."), false);
        
        assertEquals("description", result.getDescription());
        assertEquals("identification", result.getIdentification());
        assertEquals("test1", result.getServletPathPrefilter().getWords()[0]);
    }

    //TODO this test is too unspecific
    @Test
    public void testGetMatchingBadRequestDefinition() throws FilterConfigurationException, RuleLoadingException, CustomRequestMatchingException {
        
        FilterConfig filterConfig = Mockito.mock(FilterConfig.class);
        
        RuleFileLoader ruleFileLoader = new ClasspathZipRuleFileLoader();
        ruleFileLoader.setFilterConfig(filterConfig);
        ruleFileLoader.setPath("bad-requests");
       
        RuleFile[] loadRuleFiles = ruleFileLoader.loadRuleFiles();
        
        assertTrue(loadRuleFiles.length > 50);
                
        BadRequestDefinitionContainer instance = new BadRequestDefinitionContainer(ruleFileLoader);
        instance.parseDefinitions();

        assertFalse(instance.definitions.isEmpty());
        assertTrue(instance.hasEnabledDefinitions);
        
        BadRequestDefinition result;
        
        NullPointerException npe = assertThrows(NullPointerException.class, () -> instance.getMatchingBadRequestDefinition(null, ".", "context", "pathInfo", "pathTranslated", "localhost", "localhost", 20, "remoteUser", "authType", "scheme", "POST", "http", "", "UTF-8", 111, null, "url", "uri", "localhost", 80, "test", "test", 80, null, null, null, null, null, null));
        assertEquals("headerMapVariants must not be null", npe.getMessage());
        
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getContentLength()).thenReturn(111);
        
        Map parameterMapExcludingInternal = new HashMap();
        Map headerMapVariants = new HashMap();
        Map cookieMapVariants = new HashMap();
        Map parameterMapVariants = new HashMap();
        
        parameterMapExcludingInternal.put("test", new String[]{"test"});
        
        headerMapVariants.put("test", new Permutation[0]);

        cookieMapVariants.put("test", new Permutation[0]);
        parameterMapVariants.put("test", "test");
        
        result = instance.getMatchingBadRequestDefinition(request, "/", "context", "pathInfo", "pathTranslated", "localhost", "localhost", 20, "remoteUser", "authType", "scheme", "GET", "HTTP/1.0", "", "UTF-8", 111, headerMapVariants, "url", "localhost", "localhost", 80, "test", "test", 80, null, cookieMapVariants, null, null, parameterMapVariants, parameterMapExcludingInternal);
        assertTrue(result.getIdentification().startsWith("bad-requests")); 
    }
    
}
