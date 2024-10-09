package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;
public class MultipartSizeLimitDefinitionTest{
    
    WordDictionary servletPathOrRequestURIPrefilter = new WordDictionary("a b");
    Pattern servletPathOrRequestURIPattern = Pattern.compile("/");

    @Test
    public void testMultipartSizeLimitDefinition() {
        MultipartSizeLimitDefinition def = new MultipartSizeLimitDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, true, servletPathOrRequestURIPrefilter);
        
        def = new MultipartSizeLimitDefinition(false, "identification", "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
        assertProperties(def, false, servletPathOrRequestURIPrefilter);
        
        def = new MultipartSizeLimitDefinition(true, "identification", "description", null, servletPathOrRequestURIPattern);
        assertProperties(def, true, null);
    }

    @Test
    public void testMultipartSizeLimitDefinitionWithoutIdentification() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new MultipartSizeLimitDefinition(true, null, "description", servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("identification must not be null", npe.getMessage());
    }

    @Test
    public void testMultipartSizeLimitDefinitionWithoutDescription() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new MultipartSizeLimitDefinition(true, "identification", null, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern));
        assertEquals("description must not be null", npe.getMessage());
    }
    
    @Test
    public void testMultipartSizeLimitDefinitionWithoutPattern() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new MultipartSizeLimitDefinition(true, "identification", "description", servletPathOrRequestURIPrefilter, null));
        assertEquals("servletPathOrRequestURIPattern must not be null", npe.getMessage());
    }
    
    private void assertProperties(MultipartSizeLimitDefinition def, boolean expectedEnabled, WordDictionary expectedPrefilter){
        assertEquals(expectedEnabled, def.isEnabled());
        assertEquals("identification", def.getIdentification());
        assertEquals("description", def.getDescription());
        assertEquals(expectedPrefilter, def.getServletPathOrRequestURIPrefilter());
        assertEquals(servletPathOrRequestURIPattern, def.getServletPathOrRequestURIPattern());

        assertFalse(def.isMultipartAllowed());
        assertEquals(0, def.getMaxFileNameLength());
        assertEquals(0, def.getMaxInputStreamLength());
        assertEquals(0, def.getMaxFileUploadCount());
        assertEquals(0, def.getMaxFileUploadSize());
        assertEquals(0, def.getZipBombThresholdTotalSize());
        assertEquals(0, def.getZipBombThresholdFileCount());
        assertEquals(0, def.getZipBombThresholdCompressionRatio());
    }
    
}
