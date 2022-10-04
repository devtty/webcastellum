package org.webcastellum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class ServerUtilsTest {
    
    public ServerUtilsTest() {
    }

    @Test
    public void testUnmaskAmpersandsInLink() {
        assertNull(ServerUtils.unmaskAmpersandsInLink(null));
        assertEquals("https://test?test&test", ServerUtils.unmaskAmpersandsInLink("https://test?test&amp;test"));
    }

    @Test
    public void testParseContentDisposition() {
        //TODO implement
    }

    @Test
    public void testStartsWithJavaScriptOrMailto() {
        assertFalse(ServerUtils.startsWithJavaScriptOrMailto(null));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("javascript:x"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("mailto:x"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto(" mAiltO:x"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("    JaVAscriPt:x"));
        assertFalse(ServerUtils.startsWithJavaScriptOrMailto("    JaVAscriP:x"));
    }

    @Test
    public void testConvertSimpleToObjectArray() {
        int[] values = {1,2,3};
        Integer[] result = ServerUtils.convertSimpleToObjectArray(values);
        
        //TODO refactor convertSimpleToObjectArray to stream usage
        Assert.assertArrayEquals(result, Arrays.stream(values).boxed().toArray(Integer[]::new));
        
        
        values = null;
        assertNull(ServerUtils.convertSimpleToObjectArray(values));
    }

    @Test
    public void testConvertObjectToSimpleArray() {
        Integer[] values = {1,2,3};
        int[] result = ServerUtils.convertObjectToSimpleArray(values);
        //TODO refactor usages of convertObjectToSimpleArray
        Assert.assertArrayEquals(result, Arrays.stream(values).mapToInt(Integer::valueOf).toArray());
        
    }

    @Test
    public void testConvertArrayIntegerListTo2DimIntArray() {
        List<Integer[]> values = null;
        assertNull(ServerUtils.convertArrayIntegerListTo2DimIntArray(values));
        
        values = new ArrayList<>();
        values.add(new Integer[]{1, 2, 3});
        values.add(new Integer[]{4, 5, 6});
        values.add(new Integer[]{7});
        
        int[][] expected = {
            {1, 2, 3},
            {4, 5, 6},
            {7}
        };
        
        Assert.assertArrayEquals(expected, ServerUtils.convertArrayIntegerListTo2DimIntArray(values));
    }

    @Test(expected = NullPointerException.class)
    public void testIsSameServerWithoutReferrer() {
        ServerUtils.isSameServer(null, "test");
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsSameServerWithoutUrl() {
        ServerUtils.isSameServer("test",null);
    }
    
    @Test
    public void testIsSameServer() {
        assertTrue(ServerUtils.isSameServer("http://test.org", "https://test.org/a"));
        assertTrue(ServerUtils.isSameServer("http://test.org", "https://TeSt.org/a"));
        assertTrue(ServerUtils.isSameServer("http://test.org/a", "https://test.org"));
        assertFalse(ServerUtils.isSameServer("http://test.org", "http://test.com"));
        assertFalse(ServerUtils.isSameServer("http://test.org/a", "http://test.com/a"));
        assertFalse(ServerUtils.isSameServer("http://test.org", "htp://test.org"));
        //TODO correct method name would be isSameHostName
    }

    @Test
    public void testContainsColonBeforeFirstSlashOrQuestionmark() {
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark(null));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("noColonHere"));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colonBehind?:"));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colonBehind/:"));
        assertTrue(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colon:before?"));
        assertTrue(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colon:before/"));
    }
    
    @Test
    @Ignore
    public void testIsInternalHostURL() {
        //TODO later
    }

    @Test
    public void testEncodeHtmlSafe() {
        assertNull(ServerUtils.encodeHtmlSafe(null));
        assertEquals("&Auml;a&szlig;", ServerUtils.encodeHtmlSafe("Äaß"));
        //TODO check if there are better methods to encode URLfriendly
    }

    @Test
    public void testDecodeBrokenValueUrlEncodingOnly() {
        assertNull(ServerUtils.decodeBrokenValueUrlEncodingOnly(null));
        assertEquals("Hello World?", ServerUtils.decodeBrokenValueUrlEncodingOnly("Hello%20World%3F"));
        assertEquals("Hello World&", ServerUtils.decodeBrokenValueUrlEncodingOnly("Hello+World%26"));
    }

    @Test
    public void testDecodeBrokenValueHtmlOnly() {
        assertNull(ServerUtils.decodeBrokenValueHtmlOnly(null, true));
        //TODO test more
    }

    @Test
    public void testDecodeBrokenValueExceptUrlEncoding() {
        assertNull(ServerUtils.decodeBrokenValueUrlEncodingOnly(null));
        
        //TODO test more
    }

    @Test
    public void testDecodeBrokenValue() {
        assertNull(ServerUtils.decodeBrokenValue(null));
        assertEquals("¼", ServerUtils.decodeBrokenValue("%BC"));
        assertEquals("¼", ServerUtils.decodeBrokenValue("\u00bc"));
        assertEquals("¼", ServerUtils.decodeBrokenValue("\\XBC"));
        assertEquals("¼ &", ServerUtils.decodeBrokenValue("\\XBC+&amp;"));
        //TODO test more
    }

    @Test
    public void testDecodeBrokenUTF8() {
        assertNull(ServerUtils.decodeBrokenUTF8(null));
        //TODO test more
    }

    @Test
    public void testRemoveNullBytes() {
        assertNull(ServerUtils.removeNullBytes(null));
        //TODO what about \u0000
        assertEquals("test it", ServerUtils.removeNullBytes("tes\0t" + (char) 0x0 + " it"));
    }

    @Test
    public void testRemoveWhitespaces() {
        assertNull(ServerUtils.removeWhitespaces(null, (byte) 0));
        //TODO second param isnt used in removeWhitespaces and byte is the wrong type anyway
        assertEquals("HelloWorld", ServerUtils.removeWhitespaces("Hello Wor\nl\td", (byte) 0));
    }

    @Test
    public void testCompressWhitespaces() {
        assertNull(ServerUtils.compressWhitespaces(null, (byte) 0));
        //TODO second param isnt used in compressWhitespaces and byte is the wrong type anyway
        assertEquals("Hello World", ServerUtils.compressWhitespaces(" Hello     World ", (byte) 0));
    }

    @Test
    public void testRemoveComments() {
        assertNull(ServerUtils.removeComments(null));
        assertEquals("Hello World", ServerUtils.removeComments("Hello /* comment */World/*second comment*/"));
    }

    @Test
    public void testReplaceCommentsWithSpace() {
        assertNull(ServerUtils.replaceCommentsWithSpace(null));
        assertEquals("Hello  World ", ServerUtils.replaceCommentsWithSpace("Hello /* comment */World/*second comment*/"));
    }

    @Test
    public void testRemoveSamePathReferences() {
        assertNull(ServerUtils.removeSamePathReferences(null));
        assertEquals("test", ServerUtils.removeSamePathReferences("./test"));
        assertEquals("/test/.", ServerUtils.removeSamePathReferences("/test/."));
        assertEquals("/test/dir", ServerUtils.removeSamePathReferences("/test/./dir"));
    }

    @Test
    public void testRemoveMultiPathSlashes() {
        assertNull(ServerUtils.removeMultiPathSlashes(null));
        assertEquals("/test/dir", ServerUtils.removeMultiPathSlashes("/test//dir"));
        assertEquals("/test/dir", ServerUtils.removeMultiPathSlashes("/test///dir"));
        assertEquals("/test/dir", ServerUtils.removeMultiPathSlashes("//test//dir"));
    }

    @Test
    public void testRemoveXmlCdataTags() {
        assertNull(ServerUtils.removeXmlCdataTags(null));
        assertEquals("Hello World", ServerUtils.removeXmlCdataTags("Hello <![CDATA[World]]>"));
    }

    @Test
    public void testRemoveBackslashes() {
        assertNull(ServerUtils.removeBackslashes(null));
        assertEquals("Hello World", ServerUtils.removeBackslashes("Hello \\World"));
        assertEquals("Hello \\World", ServerUtils.removeBackslashes("Hello \\\\World"));
        assertEquals("Hello \\World", ServerUtils.removeBackslashes("Hello \\\\\\World"));
        assertEquals("Hello \\World \\Test", ServerUtils.removeBackslashes("Hello \\\\World \\\\\\Test"));
    }

    /* later
    @Test
    public void testPermutateVariants_3args_1() {
    }

    @Test
    public void testPermutateVariants_3args_2() {
    }
    

    @Test
    public void testIsVariantMatching() {
    }
    */

    @Test
    public void testEscapeSpecialCharactersHTML() {
        assertEquals("", ServerUtils.escapeSpecialCharactersHTML(null));
        assertEquals("&lt; &gt; &quot; &#039; &#092; &amp;", ServerUtils.escapeSpecialCharactersHTML("< > \" \' \\ &"));
        //TODO more?
    }

    @Test
    public void testExtractFileFromURL() {
        assertNull(ServerUtils.extractFileFromURL(null));
        assertNull(ServerUtils.extractFileFromURL("http://test?queryString"));
        assertNull(ServerUtils.extractFileFromURL("https://test?queryString"));
        assertNull(ServerUtils.extractFileFromURL("http://test?queryString;jsessionId=xxx"));
        assertNull(ServerUtils.extractFileFromURL("http://test;jsessionId=xxx"));
        assertEquals("test", ServerUtils.extractFileFromURL("http://test.de/test;jsessionId=xxx"));
        assertEquals("test", ServerUtils.extractFileFromURL("file://test?queryString"));
        assertEquals("test", ServerUtils.extractFileFromURL("file://test?queryString"));
        assertEquals("test", ServerUtils.extractFileFromURL("file://test?queryString;jsessionId=xxx"));
        assertEquals("test", ServerUtils.extractFileFromURL("//test?queryString;jsessionId=xxx"));
        assertEquals("test", ServerUtils.extractFileFromURL("test?queryString;jsessionId=xxx"));
        assertEquals("test", ServerUtils.extractFileFromURL("img/test?queryString;jsessionId=xxx"));
    }

    @Test
    public void testConvertCollectionToStringArray() {
        assertNull(ServerUtils.convertCollectionToStringArray(null));
        Collection strings = new ArrayList<>(Arrays.asList("Hello", "World"));
        String[] strArr = ServerUtils.convertCollectionToStringArray(strings);
        assertEquals("Hello", strArr[0]);
        assertEquals("World", strArr[1]);
        //TODO refactor method to stream
    }

    @Test
    public void testConvertMapOfCollectionsToMapOfStringArrays() {
        assertNull(ServerUtils.convertMapOfCollectionsToMapOfStringArrays(null));
        Collection<String> c1 = new ArrayList<>(Arrays.asList("Hello", "World"));
        Collection<String> c2 = new ArrayList<>(Arrays.asList("Here", "we", "are"));
        Map<String, Collection> map = new HashMap();
        map.put("a", c1);
        map.put("b", c2);
        Map converted = ServerUtils.convertMapOfCollectionsToMapOfStringArrays(map);
        assertEquals("World", ((String[]) converted.get("a"))[1]);
        assertEquals("are", ((String[]) converted.get("b"))[2]);
        //TODO refactor method to stream
    }

    @Test
    public void testExtractResourceToBeAccessed() {
        assertEquals("/test/index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/index.html", "test", "http://localhost/test/", true));
        assertEquals("/test/index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/./index.html", "test", "http://localhost/test/", true));
        assertEquals("/test/index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/index.html?query", "test", "http://localhost/test/", true));
        assertEquals("test/index.html", ServerUtils.extractResourceToBeAccessed("test/index.html?query", "test", "http://localhost/test/", true));
        assertEquals("test/index.html", ServerUtils.extractResourceToBeAccessed("./test/index.html?query", "test", "http://localhost/test/", true));
        //without full path
        assertEquals("index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/index.html", "test", "http://localhost/test/", false));
        assertEquals("index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/./index.html", "test", "http://localhost/test/", false));
        assertEquals("index.html", ServerUtils.extractResourceToBeAccessed("http://localhost/test/index.html?query", "test", "http://localhost/test/", false));
        assertEquals("index.html", ServerUtils.extractResourceToBeAccessed("test/index.html?query", "test", "http://localhost/test/", false));
        assertEquals("index.html", ServerUtils.extractResourceToBeAccessed("./test/index.html?query", "test", "http://localhost/test/", false));
    }

    @Test
    public void testIsRelativeLink() {
        assertTrue(ServerUtils.isRelativeLink("index.html"));
        assertTrue(ServerUtils.isRelativeLink("./index.html"));
        assertTrue(ServerUtils.isRelativeLink("../index.html"));
        assertFalse(ServerUtils.isRelativeLink("/index.html"));
        assertFalse(ServerUtils.isRelativeLink("http://localhost/index.html"));
        assertFalse(ServerUtils.isRelativeLink("https://localhost/index.html"));
    }

    @Test
    public void testRemoveParameterFromQueryString() {
        assertNull(ServerUtils.removeParameterFromQueryString(null, "test"));
        assertEquals("test", ServerUtils.removeParameterFromQueryString("test", null));
        assertEquals("", ServerUtils.removeParameterFromQueryString("param=test", "param"));
        assertEquals("param2=test2", ServerUtils.removeParameterFromQueryString("param1=test&param2=test2", "param1"));
    }

    /*
    @Test
    public void testFindReusableSessionContentKeyOrCreateNewOne() {
        
    }

    @Test
    public void testRenameSecretTokenParameterInAllCachedParameterAndFormProtectionObjects() {
    }
    
    TODO later
    */

    @Test
    public void testUrlEncode() throws Exception {
        assertNull(ServerUtils.urlEncode(null));
        assertEquals("", ServerUtils.urlEncode(""));
        assertEquals("Hello+World", ServerUtils.urlEncode("Hello World"));
        assertEquals("The+string+%C3%BC%40foo-bar", ServerUtils.urlEncode("The string ü@foo-bar"));
    }

    @Test
    public void testUrlDecode() throws Exception {
        assertNull(ServerUtils.urlDecode(null));
        assertEquals("", ServerUtils.urlDecode(""));
        assertEquals("The string ü@foo-bar", ServerUtils.urlDecode("The+string+%C3%BC%40foo-bar"));
    }

    @Test
    public void testReplaceEmptyMatchersWithNull() {
        Pattern p = Pattern.compile("");
        Matcher[] mArr = new Matcher[] {
            p.matcher("abc"),
            p.matcher(""),
            p.matcher("sadf")
        };
        
        Matcher[] result = ServerUtils.replaceEmptyMatchersWithNull(mArr);
        assertNull(result[1]);
        assertEquals(3, result.length);
    }

    @Test
    public void testRemoveLeadingWhitespace() {
        assertNull(ServerUtils.removeLeadingWhitespace(null));
        assertEquals("test ", ServerUtils.removeLeadingWhitespace(" test "));
        assertEquals("test ", ServerUtils.removeLeadingWhitespace("  test "));
        //TODO it just trims leading spaces (not whitesp.)
    }

    @Test
    public void testQuoteReplacement() {
        assertNull(ServerUtils.quoteReplacement(null));
        //just catch the NPE
    }

    @Test
    public void testGetAttributeNamesIncludingInternal() {
        //TODO later
    }

    @Test
    public void testGetAttributeIncludingInternal() {
        //TODO later
    }

    @Test
    public void testConvertListOfPatternToArrayOfMatcher() {
    }

    @Test
    public void testConcatenateArrays() {
    }

    @Test
    public void testConvertIntegerListToIntArray() {
    }
    
}
