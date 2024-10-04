package org.webcastellum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class ZipScannerUtilsTest {
    
    @Test
    public void testExtractNameAndCommentStrings_Stream() throws IOException{
        InputStream input = new FileInputStream(new File("src/test/resources/test.zip"));
        String[] str = ZipScannerUtils.extractNameAndCommentStrings(input);
        Assert.assertArrayEquals(str, new String[]{"test1.txt", "test1comment", "test2.txt"});
    }
    
    @Test
    public void testExtractNameAndCommentStrings_File() throws IOException{
        File file = new File("src/test/resources/test.zip");
        String[] str = ZipScannerUtils.extractNameAndCommentStrings(file);
        Assert.assertArrayEquals(str, new String[]{"test1.txt", "test1comment", "test2.txt"});
    }

    @Test
    public void testIsZipBombWithNegativeTotalSize(){
        File file = new File("src/test/resources/test.zip");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ZipScannerUtils.isZipBomb(file, -1, 0, 3.0));
        assertEquals("thresholdTotalSize must not be negative", ex.getMessage());
    }
    
    @Test
    public void testIsZipBombWithNegativeFileCount(){
        File file = new File("src/test/resources/test.zip");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ZipScannerUtils.isZipBomb(file, 0, -1, 3.0));
        assertEquals("thresholdFileCount must not be negative", ex.getMessage());
    }
    
    @Test
    public void testIsZipBomb_3args_2() throws IOException{
        File file = new File("src/test/resources/test.zip");
        //test.zip contains 2 files with a 7 bytes
        assertFalse(ZipScannerUtils.isZipBomb(file, 14, 2, 3));
        assertTrue(ZipScannerUtils.isZipBomb(file, 13, 2, 3));
        assertTrue(ZipScannerUtils.isZipBomb(file, 14, 1, 3));
    }
    
    @Test
    public void testIsZipBomb_Stream() throws IOException{
        InputStream input = new FileInputStream(new File("src/test/resources/test.zip"));
        assertFalse(ZipScannerUtils.isZipBomb(input, 14, 2, 3));
    }
    
}
