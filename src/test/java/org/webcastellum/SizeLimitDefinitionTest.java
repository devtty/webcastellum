package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class SizeLimitDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");
    
    @Test
    public void testSizeLimitDefinition(){
        SizeLimitDefinition sld = new SizeLimitDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(sld, true, servletPathOrRequestURIPrefilter);
        
        sld = new SizeLimitDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(sld, false, servletPathOrRequestURIPrefilter);
        
        sld = new SizeLimitDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(sld, true, null);
    }
    
    private void assertProperties(SizeLimitDefinition sld, boolean expectedEnabled, WordDictionary expectedPrefilter){
        //direct fields
        assertEquals(Integer.MAX_VALUE, sld.getMaxCookieCount());
        assertEquals(Integer.MAX_VALUE, sld.getMaxCookieNameLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxCookieValueLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxHeaderCount());
        assertEquals(Integer.MAX_VALUE, sld.getMaxHeaderNameLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxHeaderValueLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxQueryStringLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxRequestParamCount());
        assertEquals(Integer.MAX_VALUE, sld.getMaxRequestParamNameLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxRequestParamValueLength());
        assertEquals(Integer.MAX_VALUE, sld.getMaxTotalHeaderSize());
        assertEquals(Integer.MAX_VALUE, sld.getMaxTotalCookieSize());
        assertEquals(Integer.MAX_VALUE, sld.getMaxTotalRequestParamSize());
        
        //constructor fields
        assertTrue(expectedEnabled == sld.isEnabled());
        assertEquals("identification", sld.getIdentification());
        assertEquals("description", sld.getDescription());
        assertEquals(expectedPrefilter, sld.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, sld.getServletPathOrRequestURIPattern());
    }
        
    @Test(expected = NullPointerException.class)
    public void testSizeLimitDefinitionWithoutIdentification(){
        new SizeLimitDefinition(true, null, "description", new WordDictionary("a b"), Pattern.compile("/"));
    }
    
    @Test(expected = NullPointerException.class)
    public void testSizeLimitDefinitionWithoutDescription(){
        new SizeLimitDefinition(true, "identification", null, new WordDictionary("a b"), Pattern.compile("/"));
    }
        
    @Test(expected = NullPointerException.class)
    public void testSizeLimitDefinitionWithoutPattern(){
        new SizeLimitDefinition(true, "identification", "description", new WordDictionary("a b"), null);
    }
    
}
