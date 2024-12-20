package org.webcastellum;

import java.util.Arrays;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class LearnedRequestTest {
    
    public LearnedRequestTest() {
    }

    @Test
    public void testLearnedRequest(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> new LearnedRequest(null, null));
        assertEquals("servletPath must not be null", npe.getMessage());
        npe = assertThrows(NullPointerException.class, () -> new LearnedRequest(null, "test"));
        assertEquals("servletPath must not be null", npe.getMessage());
        npe = assertThrows(NullPointerException.class, () -> new LearnedRequest("test", null));
        assertEquals("method must not be null", npe.getMessage());
        
        LearnedRequest learnedRequest = new LearnedRequest("path", "method");
        assertEquals("method", learnedRequest.getMethod());
        assertEquals("path", learnedRequest.getServletPath());                
    }
    
    @Test
    public void testCreateServletPathPattern() {
        LearnedRequest learnedRequest = new LearnedRequest("path", "method");
        Pattern p = learnedRequest.createServletPathPattern();
        
        assertEquals("\\A\\Qpath\\E\\z", p.pattern());
    }
    
    @Test
    public void testCreateMethodPattern() {
        LearnedRequest learnedRequest = new LearnedRequest("path", "method");
        Pattern p = learnedRequest.createMethodPattern();
        
        assertEquals("\\A\\Qmethod\\E\\z", p.pattern());
    }

    @Test
    public void testGetParameterFeature(){
        LearnedRequest learnedRequest = new LearnedRequest("path", "method");
        
        learnedRequest.addParameterOccurence(null, Arrays.asList("test1", "test2", "test3"));
        learnedRequest.addParameterOccurence("test", null);
        
        assertNull(learnedRequest.getParameterFeature("colour"));
        
        learnedRequest.addParameterOccurence("colour",Arrays.asList("red"));
        
        assertNotNull(learnedRequest.getParameterFeature("colour"));
        
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingMultipleValues());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingDigits());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingMail());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingMathSymbols());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingPunctation());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingTags());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingWhitespace());
        
        learnedRequest.addParameterOccurence("colour", Arrays.asList("blue", "yellow1", "green+", "purple.", "black@mail.de"));
        
        assertTrue(learnedRequest.getParameterFeature("colour").isHavingMultipleValues());
        assertTrue(learnedRequest.getParameterFeature("colour").isHavingDigits());
        assertTrue(learnedRequest.getParameterFeature("colour").isHavingMail());
        assertTrue(learnedRequest.getParameterFeature("colour").isHavingMathSymbols());
        assertTrue(learnedRequest.getParameterFeature("colour").isHavingPunctation());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingTags());
        assertFalse(learnedRequest.getParameterFeature("colour").isHavingWhitespace());
    }

}
