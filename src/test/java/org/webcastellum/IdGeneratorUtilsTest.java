package org.webcastellum;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class IdGeneratorUtilsTest {
    
    public IdGeneratorUtilsTest() {
    }

    @Test
    public void testCreateId() {
        String uuid = "";
        
        Set<String> s = new HashSet<>();
        
        for(int i = 0; i<100; i++){
            uuid = IdGeneratorUtils.createId();
            assertEquals(32 ,uuid.length());
            assertTrue(uuid.matches("^[0-9A-F]{8}[0-9A-F]{4}[0-9A-F]{4}[0-9A-F]{4}[0-9A-F]{12}$"));
            s.add(uuid);
        }
        
        assertEquals(100, s.size());
    }
        
}
