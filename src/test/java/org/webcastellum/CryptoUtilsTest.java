package org.webcastellum;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import org.junit.Test;
import static org.junit.Assert.*;

public class CryptoUtilsTest {

    public CryptoUtilsTest() {
    }

    @Test
    public void testToHexString() {
        assertEquals("01", CryptoUtils.toHexString((byte) 0x01));
        assertEquals("9A", CryptoUtils.toHexString((byte) 0x9A));
        assertEquals("FF", CryptoUtils.toHexString((byte) 0xff));
    }

    @Test
    public void testToByteValue() {
        assertEquals((byte) 0x01, CryptoUtils.toByteValue("01"));
        assertEquals((byte) 0x9A, CryptoUtils.toByteValue("9A"));
        assertEquals((byte) 0x9A, CryptoUtils.toByteValue("9a"));
        assertEquals((byte) 0xff, CryptoUtils.toByteValue("FF"));
        assertEquals((byte) 0x1, CryptoUtils.toByteValue("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToByteValueWithToLongArgument() {
        CryptoUtils.toByteValue("001");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToByteValueWithToShortArgument() {
        CryptoUtils.toByteValue("");
    }

    @Test(expected = NullPointerException.class)
    public void testToByteValueWithNullArgument() {
        CryptoUtils.toByteValue(null);
    }

    @Test
    public void testToIntValue() {
        assertEquals(1, CryptoUtils.toIntValue("01"));
        assertEquals(154, CryptoUtils.toIntValue("9A"));
        assertEquals(154, CryptoUtils.toIntValue("9a"));
        assertEquals(255, CryptoUtils.toIntValue("FF"));
        assertEquals(1, CryptoUtils.toIntValue("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntValueWithToLongArgument() {
        CryptoUtils.toIntValue("ABCDEF121");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToIntValueWithToShortArgument() {
        CryptoUtils.toIntValue("");
    }

    @Test(expected = NullPointerException.class)
    public void testToIntValueWithNullArgument() {
        CryptoUtils.toIntValue(null);
    }

    //TODO testing randomness through adding values to a list is not reliable bc. double values are possible
    
    @Test
    public void testGenerateRandomToken_boolean() {
        List<String> strings = new ArrayList<>();
        String s = "";
        for (int i = 0; i < 100; i++) {
            String x = CryptoUtils.generateRandomToken(true);
            assertTrue(x.length() > 6);
            assertTrue(x.length() < 10);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
        for (int i = 0; i < 100; i++) {
            String x = CryptoUtils.generateRandomToken(false);
            assertTrue(x.length() > 6);
            assertTrue(x.length() < 10);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
    }

    @Test
    public void testGenerateRandomToken_boolean_int() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String x = CryptoUtils.generateRandomToken(true, 6);
            assertTrue(x.length() == 6);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
        for (int i = 0; i < 100; i++) {
            String x = CryptoUtils.generateRandomToken(false, 12);
            assertTrue(x.length() == 12);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
    }

    @Test
    public void testGenerateRandomBytes_boolean() {
        List<byte[]> a = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] bytes = CryptoUtils.generateRandomBytes(true);
            assertTrue("L:" + bytes.length, bytes.length > 9);
            assertTrue("L:" + bytes.length, bytes.length < 18);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
        for (int i = 0; i < 100; i++) {
            byte[] bytes = CryptoUtils.generateRandomBytes(false);
            assertTrue("L:" + bytes.length, bytes.length > 9);
            assertTrue("L:" + bytes.length, bytes.length < 18);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
    }

    @Test
    public void testGenerateRandomBytes_boolean_int() {
        List<byte[]> a = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] bytes = CryptoUtils.generateRandomBytes(true, 8);
            assertTrue("L:" + bytes.length, bytes.length == 8);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
        for (int i = 0; i < 100; i++) {
            byte[] bytes = CryptoUtils.generateRandomBytes(false, 15);
            assertTrue("L:" + bytes.length, bytes.length == 15);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
    }

    @Test
    public void testGenerateRandomNumber_boolean() {
        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            int r = CryptoUtils.generateRandomNumber(true);
            assertFalse(a.contains(r));
            a.add(r);
        }
        for (int i = 0; i < 100; i++){
            int r = CryptoUtils.generateRandomNumber(false);
            assertFalse(a.contains(r));
            a.add(r);
        }
    }

    @Test
    public void testGenerateRandomNumber_3args() {
        List<Integer> a = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            int r = CryptoUtils.generateRandomNumber(true, 500, 50000);
            assertTrue(r > 500);
            assertTrue(r < 50000);
            assertFalse(a.contains(r));
            a.add(r);
        }
        for(int i = 0; i < 20; i++){
            int r = CryptoUtils.generateRandomNumber(false, 50000, 100000);
            assertTrue(r > 50000);
            assertTrue(r < 100000);
            assertFalse(a.contains(r));
            a.add(r);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateRandomNumber_negativeMin(){
        CryptoUtils.generateRandomNumber(true, -1, 10);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateRandomNumber_negativeDifference(){
        CryptoUtils.generateRandomNumber(true, 5, 4);
    }

    @Test
    public void testGetHashLength() throws Exception {
        assertTrue(CryptoUtils.getHashLength() == 32);
    }

    
    @Test
    public void testHash() throws Exception {
        byte[] saltBefore = "salt".getBytes();
        byte[] saltAfter = "pepper".getBytes();
                
        assertNull(CryptoUtils.hash(saltBefore, null, saltAfter, 0));
        
        byte[] hashContent0 = CryptoUtils.hash(saltBefore, "content", saltAfter, 0);
        byte[] hashContent1 = CryptoUtils.hash(saltBefore, "content", saltAfter, 1);
        byte[] hashContent2 = CryptoUtils.hash(saltBefore, "content", saltAfter, 2);
        
        assertNotNull(hashContent0);
        assertEquals(32, hashContent0.length);
        
        assertNotNull(hashContent1);
        assertEquals(32, hashContent1.length);
        assertNotEquals(hashContent0, hashContent1);
        
        assertNotNull(hashContent2);
        assertEquals(32, hashContent2.length);
        assertNotEquals(hashContent1, hashContent2);
    }

    
    /*
    @Test
    public void testGetCipher() throws Exception {
    }

    */
    
    @Test
    public void testGenerateRandomCryptoKeyAndSalt() throws Exception {
        List<CryptoKeyAndSalt> a = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            CryptoKeyAndSalt ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
            assertFalse(ckas.isExtraHashingProtection());
            assertNotNull(ckas.getKey());
            assertNull(ckas.getSaltBefore());
            assertNull(ckas.getSaltAfter());
            assertTrue(ckas.getRepeatedHashingCount() == 0);
            assertFalse(a.contains(ckas));
            a.add(ckas);
        }
        for(int i = 0; i < 100; i++){
            CryptoKeyAndSalt ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
            assertTrue(ckas.isExtraHashingProtection());
            assertNotNull(ckas.getKey());
            assertNotNull(ckas.getSaltBefore());
            assertTrue(ckas.getSaltBefore().length > 9);
            assertTrue(ckas.getSaltBefore().length < 18);
            assertNotNull(ckas.getSaltAfter());
            assertTrue(ckas.getSaltAfter().length > 9);
            assertTrue(ckas.getSaltAfter().length < 18);
            assertTrue(ckas.getRepeatedHashingCount() >= 2);
            assertTrue(ckas.getRepeatedHashingCount() <= 5);
            assertFalse(a.contains(ckas));
            a.add(ckas);
        }
        
    }

    @Test
    public void testEncryptAndDecryptURLSafe() throws Exception {

        String URL = "https://test.com/context/path?id=1&test=d";

        CryptoKeyAndSalt ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
        String s = CryptoUtils.encryptURLSafe(URL, ckas, null);

        String decrypted = CryptoUtils.decryptURLSafe(s, ckas);

        assertEquals(URL, decrypted);

        ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
        s = CryptoUtils.encryptURLSafe(URL, ckas, null);
        decrypted = CryptoUtils.decryptURLSafe(s, ckas);
        assertEquals(URL, decrypted);

    }

    @Test
    public void testBytesToHex() {
        try {
            assertNull(CryptoUtils.bytesToHex(null));
            assertEquals("", CryptoUtils.bytesToHex(new byte[0]));
            
            String test = "Hello World!";
            assertEquals("48656C6C6F20576F726C6421", CryptoUtils.bytesToHex(test.getBytes("UTF-8")));
            assertEquals("C88593939640E6969993845A", CryptoUtils.bytesToHex(test.getBytes("IBM01140")));
        } catch (UnsupportedEncodingException ex) {
            fail("Unsupported Encoding");
        }
    }

    @Test
    public void testHexToBytes() {
        assertEquals("Hello World!", new String(CryptoUtils.hexToBytes("48656C6C6F20576F726C6421")));
    }

    @Test
    public void testCompressAndBack() {
        byte[] original = "Hello World!".getBytes();
        byte[] compressed = CryptoUtils.compress(original);
        
        assertNotNull(compressed);
        assertNotEquals(original, compressed);
        
        try {
            assertArrayEquals(original, CryptoUtils.decompress(compressed));
        } catch (DataFormatException ex) {
            fail("DataFormat Exception");
        }
    }
}
