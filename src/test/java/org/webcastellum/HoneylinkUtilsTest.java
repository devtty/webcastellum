package org.webcastellum;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class HoneylinkUtilsTest {
    
    private static final int ITERATIONS = 5000;
    
    @Test
    public void testGenerateHoneyLinkWithRandom(){
        Random random = new java.util.Random();
        testGenerateHoneylink(random, "prefix", "suffix", false);
    }
    
    @Test
    public void testGenerateHoneyLinkWithoutPrefix(){
        testGenerateHoneylink(null, null, "suffix", false);
    }
    
    @Test
    public void testGenerateHoneyLinkWithoutSuffix(){
        testGenerateHoneylink(null, "prefix", null, false);
    }
    
    @Test
    public void testGenerateHoneyLinkWithinTable(){
        testGenerateHoneylink(null, "prefix", "suffix", true);
    }
    
    private void testGenerateHoneylink(Random random, String prefix, String suffix, boolean withinTable) {
        String honeyLink = null;
        Set<String> links = new HashSet<>();
        for(int i=0;i<ITERATIONS;i++){
            honeyLink = HoneylinkUtils.generateHoneylink(random, prefix, suffix, withinTable);
            if(prefix != null){
                assertTrue(honeyLink.contains(prefix));
            }
            if(suffix != null){
                assertTrue(honeyLink.contains("suffix"));
            }
            links.add(honeyLink);
            assertTrue(honeyLink.startsWith("<!--"));
            assertTrue(honeyLink.endsWith("-->"));
            
            if(withinTable){
                assertTrue(honeyLink.contains("<td>"));
                assertTrue(honeyLink.contains("</td>"));
            }
        }
        
        assertEquals(ITERATIONS, links.size());
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
