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

    @Test
    public void testDecodingPermutationDefinitionWithoutIdentification() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new DecodingPermutationDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("identification must not be null", npe.getMessage());
    }
    
    @Test
    public void testDecodingPermutationDefinitionWithoutDescription() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new DecodingPermutationDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("description must not be null", npe.getMessage());
    }
    
    @Test
    public void testDecodingPermutationDefinitionWithoutPattern() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new DecodingPermutationDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null));
        assertEquals("servletPathOrRequestURIPattern must not be null", npe.getMessage());
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
