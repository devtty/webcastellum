package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class ContentModificationExcludeDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");

    @Test
    public void testContentModificationExcludeDefinition(){
        ContentModificationExcludeDefinition definition = new ContentModificationExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(definition, true, servletPathOrRequestURIPrefilter);
        
        definition = new ContentModificationExcludeDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(definition, false, servletPathOrRequestURIPrefilter);

        definition = new ContentModificationExcludeDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(definition, true, null);
    }

    @Test
    public void testContentModificationExcludeDefinitionWithoutIdentification(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new ContentModificationExcludeDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("identification must not be null", npe.getMessage());
    }
    
    @Test
    public void testContentModificationExcludeDefinitionWithoutDescription(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new ContentModificationExcludeDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("description must not be null", npe.getMessage());
    }
    
    @Test
    public void testContentModificationExcludeDefinitionWithoutPattern(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new ContentModificationExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null));
        assertEquals("servletPathOrRequestURIPattern must not be null", npe.getMessage());
    }
    
    private void assertProperties(ContentModificationExcludeDefinition def, boolean  expectedEnabled, WordDictionary expectedPrefilter){
        assertEquals(expectedEnabled, def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, def.getServletPathOrRequestURIPattern());
        
        assertFalse(def.isExcludeOutgoingResponsesFromModification());
        assertFalse(def.isExcludeIncomingLinksFromModification());
        assertFalse(def.isExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled());
    }
    
}
