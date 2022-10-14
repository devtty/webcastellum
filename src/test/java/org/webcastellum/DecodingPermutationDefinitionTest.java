package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class DecodingPermutationDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");
    
    @Test
    public void testDecodingPermutationDefinition() {
        DecodingPermutationDefinition def = new DecodingPermutationDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, true, servletPathOrRequestURIPrefilter);
        
        def = new DecodingPermutationDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, false, servletPathOrRequestURIPrefilter);
        
        def = new DecodingPermutationDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(def, true, null);
    }

    @Test(expected = NullPointerException.class)
    public void testDecodingPermutationDefinitionWithoutIdentification() {
        new DecodingPermutationDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);        
    }
    
    @Test(expected = NullPointerException.class)
    public void testDecodingPermutationDefinitionWithoutDescription() {
        new DecodingPermutationDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);        
    }
    
    @Test(expected = NullPointerException.class)
    public void testDecodingPermutationDefinitionWithoutPattern() {
        new DecodingPermutationDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null);        
    }
        
    private void assertProperties(DecodingPermutationDefinition def, boolean expectedEnabled, WordDictionary expectedPrefilter){
        assertEquals(0, def.getLevel());
        
        assertEquals(expectedEnabled, def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, def.getServletPathOrRequestURIPattern());   
    }
}
