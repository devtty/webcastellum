package org.webcastellum.test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import org.junit.Test;
import org.webcastellum.Base64Utils;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.webcastellum.CryptoUtils;

public class Base64UtilsTest{

    @Test
    public void testEncode(){
        String s ="adf";
        assertEquals("YWRm" , Base64Utils.encode(s.getBytes()));
        String a = "Polyfon zwitschernd aßen Mäxchens Vögel Rüben, Joghurt und Quark";
        assertEquals("UG9seWZvbiB6d2l0c2NoZXJuZCBhw59lbiBNw6R4Y2hlbnMgVsO2Z2VsIFLDvGJlbiwgSm9naHVydCB1bmQgUXVhcms." , Base64Utils.encode(a.getBytes()));
    }
    
    @Test
    public void testDecode(){
        String a = "UG9seWZvbiB6d2l0c2NoZXJuZCBhw59lbiBNw6R4Y2hlbnMgVsO2Z2VsIFLDvGJlbiwgSm9naHVydCB1bmQgUXVhcms.";
        byte[] b = Base64Utils.decode(a);
        assertEquals("Polyfon zwitschernd aßen Mäxchens Vögel Rüben, Joghurt und Quark", new String(b));
    }
        
    
    @Test
    public void testUrlCharacters(){
        int leftLimit = 32;
        int rightLimit = 126;
            
        Random random = new Random();
        
        for(int i=0; i<100; i++){
            
            String randomStr = random.ints(leftLimit, rightLimit + 1)
                    .limit(100)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            
            String encoded = Base64Utils.encode(randomStr.getBytes());
        
            byte[] decoded = null;
            
            try{
                decoded = Base64Utils.decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + (randomStr) + " / "  + encoded);
            }
        
            assertEquals(randomStr, new String(decoded));
        }
    }
    
    @Test
    @Ignore
    public void testEncodeLegacy(){
        int leftLimit = 32;
        int rightLimit = 126;
        
        Random random = new Random();
        
        for(int i=0; i<100; i++){
            String randomStr = random.ints(leftLimit, rightLimit + 1)
                    .limit(5)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            
            String encoded = Base64Utils.encode(randomStr.getBytes());
        
            byte[] decoded = null;
            //compare w JDK8
            //byte[] decoded = Base64.getDecoder().decode(encoded);
            try{
                //decoded = Base64.getUrlDecoder().decode(encoded);
                decoded = Base64.getMimeDecoder().decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + randomStr + " / "  + encoded);
            }
        
            assertEquals(randomStr, new String(decoded));
        }
    }
    
    @Test
    @Ignore
    public void testDecodeLegacy(){
        for(int i=0; i<100; i++){
            byte[] x = CryptoUtils.generateRandomBytes(true, 20);
            
            String encoded = new String(Base64.getUrlEncoder().encode(x), StandardCharsets.UTF_8);
            
            byte[] decoded = null;
            
            try{
                decoded = Base64Utils.decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + x.toString() + " / " + encoded);
            }
        
            assertEquals(x, decoded);
        }
    }
}
