package org.webcastellum;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class HoneylinkUtilsTest {
    
    public HoneylinkUtilsTest() {
    }

    @Test
    public void testGenerateHoneylink() {
        String honeyLink = null;
        Set<String> links = new HashSet<>();
        for(int i=0;i<10;i++){
            honeyLink = HoneylinkUtils.generateHoneylink(null, "prefix", "suffix", false);
            assertTrue(honeyLink.contains("prefix"));
            assertTrue(honeyLink.contains("suffix"));
            links.add(honeyLink);
            System.out.println(honeyLink);
        }
        
        assertEquals(10, links.size());
    }

    @Test
    public void testIsHoneylinkFilename() {
        
        final String LONG_STRING = (new Random()).ints(97, 123)
                .limit(301)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        
        assertFalse(HoneylinkUtils.isHoneylinkFilename(null));
        assertFalse(HoneylinkUtils.isHoneylinkFilename("aa"));
        assertFalse(HoneylinkUtils.isHoneylinkFilename(LONG_STRING));
        assertFalse(HoneylinkUtils.isHoneylinkFilename("mustFailBecauseItStartsLowerCase"));
        assertFalse(HoneylinkUtils.isHoneylinkFilename("FailsBecauseMagicWordIsMissing"));
        assertTrue(HoneylinkUtils.isHoneylinkFilename("ExecCorrect"));
        assertTrue(HoneylinkUtils.isHoneylinkFilename("testPath/ValidatorCorrect"));
    }
    
}
