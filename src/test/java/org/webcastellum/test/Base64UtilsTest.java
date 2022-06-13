package org.webcastellum.test;

import org.junit.Test;
import org.webcastellum.Base64Utils;
import static org.junit.Assert.*;

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
        
    
    /*
    @Test
    @Ignore
    public void testLegacy(){
        for(int i=0; i<100; i++){
            byte[] x = CryptoUtils.generateRandomBytes(false, 12);
            String encoded = Base64Utils.encode(x);
        
            byte[] decoded = null;
            //compare w JDK8
            //byte[] decoded = Base64.getDecoder().decode(encoded);
            try{
                decoded = Base64Utils.decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + (new String(x)) + " / "  + encoded);
            }
        
            assertEquals(x, decoded);
        }
    }
    
    @Test
    @Ignore
    public void testEncodeLegacy(){
        for(int i=0; i<100; i++){
            byte[] x = CryptoUtils.generateRandomBytes(true, 20);
            String encoded = Base64Utils.encode(x);
        
            byte[] decoded = null;
            //compare w JDK8
            //byte[] decoded = Base64.getDecoder().decode(encoded);
            try{
                decoded = Base64.getMimeDecoder().decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + x.toString() + " / "  + encoded);
            }
        
            assertEquals(x, decoded);
        }
    }
    
    @Test
    @Ignore
    public void testDecodeLegacy(){
        for(int i=0; i<100; i++){
            byte[] x = CryptoUtils.generateRandomBytes(true, 20);
            
            String encoded = new String(Base64.getMimeEncoder().encode(x), StandardCharsets.UTF_8);
            
            byte[] decoded = null;
            
            try{
                decoded = Base64Utils.decode(encoded);
            }catch(IllegalArgumentException e){
                fail("Char: " + x.toString() + " / " + encoded);
            }
        
            assertEquals(x, decoded);
        }
    }*/
}
