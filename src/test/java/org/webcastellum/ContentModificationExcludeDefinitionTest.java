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

    @Test(expected = NullPointerException.class)
    public void testContentModificationExcludeDefinitionWithoutIdentification(){
        new ContentModificationExcludeDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }
    
    @Test(expected = NullPointerException.class)
    public void testContentModificationExcludeDefinitionWithoutDescription(){
        new ContentModificationExcludeDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }
    
    @Test(expected = NullPointerException.class)
    public void testContentModificationExcludeDefinitionWithoutPattern(){
        new ContentModificationExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null);
    }
    
    private void assertProperties(ContentModificationExcludeDefinition def, boolean  expectedEnabled, WordDictionary expectedPrefilter){
        assertTrue(expectedEnabled == def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, def.getServletPathOrRequestURIPattern());
        
        assertFalse(def.isExcludeOutgoingResponsesFromModification());
        assertFalse(def.isExcludeIncomingLinksFromModification());
        assertFalse(def.isExcludeIncomingLinksFromModificationEvenWhenFullPathRemovalEnabled());
    }
    
}
