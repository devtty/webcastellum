package org.webcastellum;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

public class LearnedRequestTest {
    
    public LearnedRequestTest() {
    }

    @Test
    public void testLearnedRequest(){
        assertThrows(NullPointerException.class, () -> new LearnedRequest(null, null));
        assertThrows(NullPointerException.class, () -> new LearnedRequest(null, "test"));
        assertThrows(NullPointerException.class, () -> new LearnedRequest("test", null));
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

}
