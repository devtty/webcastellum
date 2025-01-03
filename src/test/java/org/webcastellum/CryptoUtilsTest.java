package org.webcastellum;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

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

    @Test
    public void testToByteValueWithToLongArgument() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.toByteValue("001"));
        assertEquals("hex must be at max a two-digit hex value like B1", iae.getMessage());
    }

    @Test
    public void testToByteValueWithToShortArgument() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.toByteValue(""));
        assertEquals("hex must be at max a two-digit hex value like B1", iae.getMessage());
    }

    @Test
    public void testToByteValueWithNullArgument() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> CryptoUtils.toByteValue(null));
        assertEquals("Cannot invoke \"String.length()\" because \"hex\" is null", npe.getMessage());
    }

    @Test
    public void testToIntValue() {
        assertEquals(1, CryptoUtils.toIntValue("01"));
        assertEquals(154, CryptoUtils.toIntValue("9A"));
        assertEquals(154, CryptoUtils.toIntValue("9a"));
        assertEquals(255, CryptoUtils.toIntValue("FF"));
        assertEquals(1, CryptoUtils.toIntValue("1"));
    }

    @Test
    public void testToIntValueWithToLongArgument() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.toIntValue("ABCDEF121"));
        assertEquals("hex must be at max a eight-digit hex value like ABCDEF12", iae.getMessage());
    }

    @Test
    public void testToIntValueWithToShortArgument() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.toIntValue(""));
        assertEquals("hex must be at max a eight-digit hex value like ABCDEF12", iae.getMessage());
    }

    @Test
    public void testToIntValueWithNullArgument() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> CryptoUtils.toIntValue(null));
        assertEquals("Cannot invoke \"String.length()\" because \"hex\" is null", npe.getMessage());
    }

    //TODO testing randomness through adding values to a list is not reliable bc. double values are possible
    
    @Test
    @Ignore("see TODO")
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
    @Ignore("see TODO")
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
    @Ignore("see TODO")
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
    @Ignore("see TODO")
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
    @Ignore("see TODO")
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
    @Ignore("see TODO")
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
    
    @Test
    public void testGenerateRandomNumber_negativeLow(){
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.generateRandomNumber(true, -1, 10));
        assertEquals("Values cannot be negative", iae.getMessage());
    }
    
    @Test
    public void testGenerateRandomNumber_negativeHigh(){
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.generateRandomNumber(true, 1, -10));
        assertEquals("Values cannot be negative", iae.getMessage());
    }
    
    @Test
    public void testGenerateRandomNumber_negativeDifference(){
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> CryptoUtils.generateRandomNumber(true, 5, 4));
        assertEquals("Low value must be lower than high value (low=5 and high=4)", iae.getMessage());
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
    public void testencryptAndDecryptURLSafe(){
        encryptAndDecryptURLSafe("https://test.com/context/path?id=1&test=d");
        encryptAndDecryptURLSafe("https://test.com/context/path?id=1%20&test=d");
        encryptAndDecryptURLSafe("https://test.com/context/path?id=1+&test=d");
    }

    private void encryptAndDecryptURLSafe(String content){

        try {
            CryptoKeyAndSalt ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
            String s = CryptoUtils.encryptURLSafe(content, ckas, null);
            
            String decrypted = CryptoUtils.decryptURLSafe(s, ckas);
            
            assertEquals(content, decrypted);
            
            ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(false);
            s = CryptoUtils.encryptURLSafe(content, ckas, null);
            decrypted = CryptoUtils.decryptURLSafe(s, ckas);
            assertEquals(content, decrypted);
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            fail("En-/De-cryption failed");
        }

    }
    
    @Test
    public void testDecryptURLSafeWithoutContent(){
        try{
            CryptoKeyAndSalt ckas = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
            NullPointerException npe = assertThrows(NullPointerException.class, () -> CryptoUtils.decryptURLSafe(null, ckas));
            assertEquals("content must not be null", npe.getMessage());
        }catch (NoSuchAlgorithmException e){
            fail("En-/De-cryption failed");
        }
    }
    
    @Test
    public void testDecryptURLSafeWithoutKey(){
        NullPointerException npe = assertThrows(NullPointerException.class, () -> CryptoUtils.decryptURLSafe("test", null));
        assertEquals("key must not be null", npe.getMessage());
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
