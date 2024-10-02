package org.webcastellum;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class ZipScannerUtilsTest {
    
    public ZipScannerUtilsTest() {
    }

    @Test
    public void testExtractNameAndCommentStrings_File() throws Exception {
        File file = new File("src/test/resources/test.zip");
        String[] str = ZipScannerUtils.extractNameAndCommentStrings(file);
        Assert.assertArrayEquals(str, new String[]{"test1.txt", "test1comment", "test2.txt"});
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void testIsZipBombWithNegativeTotalSize() throws IOException{
        ZipScannerUtils.isZipBomb(new File("src/test/resources/test.zip"), -1, 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsZipBombWithNegativeFileCount() throws IOException{
        ZipScannerUtils.isZipBomb(new File("src/test/resources/test.zip"), 0, -1);
    }
    
    @Test
    public void testIsZipBomb_3args_2() throws Exception {
        File file = new File("src/test/resources/test.zip");
        //test.zip contains 2 files with a 7 bytes
        assertFalse(ZipScannerUtils.isZipBomb(file, 14, 2));
        assertTrue(ZipScannerUtils.isZipBomb(file, 13, 2));
        assertTrue(ZipScannerUtils.isZipBomb(file, 14, 1));
    }
    
}
