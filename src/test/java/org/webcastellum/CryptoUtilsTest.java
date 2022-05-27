package org.webcastellum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public void testToByteValueWithToLongArgument(){
        CryptoUtils.toByteValue("001");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testToByteValueWithToShortArgument(){
        CryptoUtils.toByteValue("");
    }
    
    @Test(expected = NullPointerException.class)
    public void testToByteValueWithNullArgument(){
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
    public void testToIntValueWithToLongArgument(){
        CryptoUtils.toIntValue("ABCDEF121");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testToIntValueWithToShortArgument(){
        CryptoUtils.toIntValue("");
    }
    
    @Test(expected = NullPointerException.class)
    public void testToIntValueWithNullArgument(){
        CryptoUtils.toIntValue(null);
    }

    @Test
    public void testGenerateRandomToken_boolean() {
        List<String> strings = new ArrayList<>();
        String s = "";
        for(int i = 0 ; i< 100; i++){
            String x = CryptoUtils.generateRandomToken(true);
            assertTrue(x.length() > 6);
            assertTrue(x.length() < 10);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
        for(int i = 0 ; i< 100; i++){
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
        for(int i = 0 ; i< 100; i++){
            String x = CryptoUtils.generateRandomToken(true, 6);
            assertTrue(x.length() == 6);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
        for(int i = 0 ; i< 100; i++){
            String x = CryptoUtils.generateRandomToken(false, 12);
            assertTrue(x.length() == 12);
            assertFalse(strings.contains(x));
            strings.add(x);
        }
    }

    @Test
    public void testGenerateRandomBytes_boolean() {
        List<byte[]> a = new ArrayList<>();

        for(int i = 0; i < 100; i++){
            byte[] bytes = CryptoUtils.generateRandomBytes(true);
            assertTrue("L:" + bytes.length, bytes.length > 9);
            assertTrue("L:" + bytes.length, bytes.length < 18);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
        for(int i = 0; i < 100; i++){
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

        for(int i = 0; i < 100; i++){
            byte[] bytes = CryptoUtils.generateRandomBytes(true, 8);
            assertTrue("L:" + bytes.length, bytes.length == 8);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
        for(int i = 0; i < 100; i++){
            byte[] bytes = CryptoUtils.generateRandomBytes(false, 15);
            assertTrue("L:" + bytes.length, bytes.length == 15);
            assertFalse(a.contains(bytes));
            a.add(bytes);
        }
    }

    /*
    @Test
    public void testGenerateRandomNumber_boolean() {
        
    }

    @Test
    public void testGenerateRandomNumber_3args() {
    }

    @Test
    public void testGetHashLength() throws Exception {
    }

    @Test
    public void testHash() throws Exception {
    }

    @Test
    public void testGetCipher() throws Exception {
    }

    @Test
    public void testGenerateRandomCryptoKeyAndSalt() throws Exception {
    }

    @Test
    public void testEncryptURLSafe() throws Exception {
    }

    @Test
    public void testDecryptURLSafe() throws Exception {
    }

    @Test
    public void testBytesToHex() {
    }

    @Test
    public void testHexToBytes() {
    }

    @Test
    public void testCompress() {
    }

    @Test
    public void testDecompress() throws Exception {
    }
    */
}
