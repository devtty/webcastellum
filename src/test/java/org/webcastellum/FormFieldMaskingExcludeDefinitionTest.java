package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class FormFieldMaskingExcludeDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");
        
    @Test
    public void testFormFieldMaskingExcludeDefinition(){
        FormFieldMaskingExcludeDefinition def = new FormFieldMaskingExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, true, servletPathOrRequestURIPrefilter);
        
        def = new FormFieldMaskingExcludeDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, false, servletPathOrRequestURIPrefilter);
        
        def = new FormFieldMaskingExcludeDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(def, true, null);
    }
    
    @Test
    public void testFormFieldMaskingExcludeDefinitionWithoutIdentification(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new FormFieldMaskingExcludeDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("identification must not be null", npe.getMessage());
    }
    
    @Test
    public void testFormFieldMaskingExcludeDefinitionWithoutDescription(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new FormFieldMaskingExcludeDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("description must not be null", npe.getMessage());
    }
    
    @Test
    public void testFormFieldMaskingExcludeDefinitionWithoutPattern(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new FormFieldMaskingExcludeDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null));
        assertEquals("servletPathOrRequestURIPattern must not be null", npe.getMessage());
    }
    
    private void assertProperties(FormFieldMaskingExcludeDefinition def, boolean expectedEnabled, WordDictionary expectedPrefilter){
        assertEquals(expectedEnabled, def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, def.getServletPathOrRequestURIPattern());
        
        assertNull(def.getFormNamePrefilter());
        assertNull(def.getFieldNamePrefilter());
        assertNull(def.getFormNamePattern());
        assertNull(def.getFormNamePrefilter());
    }
}
