package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class TotalExcludeDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");
    
    @Test
    public void testTotalExcludeDefinition() {
        TotalExcludeDefinition definition = new TotalExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(definition, true, servletPathOrRequestURIPrefilter);
        
        definition = new TotalExcludeDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(definition, false, servletPathOrRequestURIPrefilter);
        
        definition = new TotalExcludeDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(definition, true, null);
    }
    
    @Test
    public void testTotalExcludeDefinitionWithoutIdentification(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new TotalExcludeDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("identification must not be null", npe.getMessage());
    }
    
    @Test
    public void testTotalExcludeDefinitionWithoutDescription(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new TotalExcludeDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("description must not be null", npe.getMessage());
    }
    
    @Test
    public void testTotalExcludeDefinitionWithoutPattern(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new TotalExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null));
        assertEquals("servletPathOrRequestURIPattern must not be null", npe.getMessage());
    }

    private void assertProperties(TotalExcludeDefinition definition, boolean expectedEnabled, WordDictionary expectedPrefilter) {
        assertEquals(expectedEnabled, definition.isEnabled());
        assertEquals("identification", definition.getIdentification());
        assertEquals("description", definition.getDescription());
        assertEquals(expectedPrefilter, definition.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, definition.getServletPathOrRequestURIPattern());
    }

    

}
