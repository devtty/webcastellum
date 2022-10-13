package org.webcastellum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerUtilsTest {
    
    
    private HttpSession session;
    
    
    private static final String PERMUTATION_TEST_STRING = "&Auml;a&szlig; Äaß "
            + "Hello World? Hello%20World%3F Hello World& Hello+World%26"
            + "Hello%20World%3F& Hello%20World%3F&amp; Hello%20World%3F&amp; Hello%20World%3F&amp;"
            + "Hello%20World%3F&± Hello%20World%3F&amp;&plusmn;"
            + "Hello%20World%3F&amp;± Hello%20World%3F&amp;&plusmn;"
            + "Hello%20World%3F& Hello%20World%3F&amp;"
            + "Hello%20World%3F&± Hello%20World%3F&amp;&plusmn;"
            + "¼ %BC ¼ \u00bc \\XBC ¼ & \\XBC+&amp;"
            + "Hello World?& Hello%20World%3F& a/_2%K72F0abcd a%2F_2%K72F0abcd"
            + "a/_2%2KF0abcd a%2F_2%2KF0abcd"
            + "test it tes\0t" + (char) 0x0 + " it"
            + "Hello Wor\nl\td" + " Hello     World "
            + "Hello /* comment */World/*second comment*/"
            + "./test /test/. /test/./dir"
            + "/test/dir /test//dir /test///dir //test//dir"
            + "Hello <![CDATA[World]]>"
            + "Hello \\World Hello \\\\World Hello \\\\\\World Hello \\\\World \\\\\\Test";

    public ServerUtilsTest() {
    }
    
    @Before
    public void setUp(){
        session = Mockito.mock(HttpSession.class);
    }

    @Test
    public void testUnmaskAmpersandsInLink() {
        assertNull(ServerUtils.unmaskAmpersandsInLink(null));
        assertEquals("https://test?test&test", ServerUtils.unmaskAmpersandsInLink("https://test?test&amp;test"));
    }

    @Test
    public void testParseContentDispositionWithoutDisposition() {
        assertTrue(ServerUtils.parseContentDisposition(null).isEmpty());
        assertTrue(ServerUtils.parseContentDisposition("").isEmpty());
        assertTrue(ServerUtils.parseContentDisposition(" ").isEmpty());
    }
    
    @Test
    public void testParseContentDisposition() {
        Map resulta = ServerUtils.parseContentDisposition("Content-Disposition: form-data; name=\"field_value\"; filename=\"file_name.html\"");
        Map resultb = ServerUtils.parseContentDisposition("Content-Disposition: form-data; name=field_value; filename=file_name.html");
        
        assertTrue(resulta.containsKey("filename"));
        assertTrue(resulta.containsKey("name"));
        assertEquals("file_name.html", resulta.get("filename"));
        assertEquals("field_value", resulta.get("name"));
        assertTrue(resultb.containsKey("filename"));
        assertTrue(resultb.containsKey("name"));
        assertEquals("file_name.html", resultb.get("filename"));
        assertEquals("field_value", resultb.get("name"));
    }

    @Test
    public void testStartsWithJavaScriptOrMailto() {
        assertFalse(ServerUtils.startsWithJavaScriptOrMailto(null));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("javascript:test"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("mailto:test"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("JAVASCRIPT:test"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("MAILTO:test"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto(" mAiltO:test"));
        assertTrue(ServerUtils.startsWithJavaScriptOrMailto("    JaVAscriPt:test"));
        assertFalse(ServerUtils.startsWithJavaScriptOrMailto("    JaVAscriP:test"));
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
        assertNull(ServerUtils.convertObjectToSimpleArray(null));
        
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
        assertFalse(ServerUtils.isSameServer("http://test.org/index.html", "index.html"));
        assertFalse(ServerUtils.isSameServer("index.html", "index.html"));
        assertFalse(ServerUtils.isSameServer("index.html", "http://test.org/index.html"));
    }

    @Test
    public void testContainsColonBeforeFirstSlashOrQuestionmark() {
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark(null));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("noColonHere"));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colonBehind?:"));
        assertFalse(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colonBehind/:"));
        assertTrue(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colon:before?"));
        assertTrue(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("colon:before/"));
        assertTrue(ServerUtils.containsColonBeforeFirstSlashOrQuestionmark("has:colon"));
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsInternalHostURLWithoutCompareWith(){
        ServerUtils.isInternalHostURL(null, "linkedUrl");
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsInternalHostURLWithoutUrl(){
        ServerUtils.isInternalHostURL("url", null);
    }
    
    @Test
    public void testIsInternalHostURL() {
        String currentRequestUrlToCompareWith = "http://test.org";
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "http://test.org/index.php?hello=world"));
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "https://test.org/index.php?hello=world"));
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "./index.php"));
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "index.php"));
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "/test/index.php"));
        assertTrue(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "http://test.org:8080/index.php"));
        
        assertFalse(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "http://test.de:/index.php"));
        assertFalse(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "https://test.de:/index.php"));
        assertFalse(ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith, "https://test.org:-80/index.php"));
        
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
        assertEquals("Hello%20World%3F&", ServerUtils.decodeBrokenValueHtmlOnly("Hello%20World%3F&amp;", true));
        assertEquals("Hello%20World%3F&amp;", ServerUtils.decodeBrokenValueHtmlOnly("Hello%20World%3F&amp;", false));
        assertEquals("Hello%20World%3F&±", ServerUtils.decodeBrokenValueHtmlOnly("Hello%20World%3F&amp;&plusmn;", true));
        assertEquals("Hello%20World%3F&amp;±", ServerUtils.decodeBrokenValueHtmlOnly("Hello%20World%3F&amp;&plusmn;", false));
    }

    @Test
    public void testDecodeBrokenValueExceptUrlEncoding() {
        assertNull(ServerUtils.decodeBrokenValueUrlEncodingOnly(null));
        
        assertEquals("Hello%20World%3F&", ServerUtils.decodeBrokenValueExceptUrlEncoding("Hello%20World%3F&amp;"));
        assertEquals("Hello%20World%3F&±", ServerUtils.decodeBrokenValueExceptUrlEncoding("Hello%20World%3F&amp;&plusmn;"));
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
        assertEquals("Hello World?&", ServerUtils.decodeBrokenUTF8("Hello%20World%3F&"));
        assertEquals("a/_2%K72F0abcd", ServerUtils.decodeBrokenUTF8("a%2F_2%K72F0abcd"));
        assertEquals("a/_2%2KF0abcd", ServerUtils.decodeBrokenUTF8("a%2F_2%2KF0abcd"));
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

    @Test
    public void testPermutateVariants_3args_1() {
        Map map = Map.ofEntries(
                entry("param1", new String[]{"alpha", "beta", "gamma"}),
                entry("param2", new String[]{"c", "d"})
        );
        Map permutateVariants = ServerUtils.permutateVariants(map, true, (byte) 1);
        permutateVariants.keySet().forEach(action -> {
            System.out.println(action);
        });
        
        //System.out.println((String[]) permutateVariants.get("param1"));
        
        //Permutation p = (String[]) permutateVariants.get("param1");
        
        Permutation[] p = (Permutation[]) permutateVariants.get("param1");
        for(Permutation x : p){
            x.getStandardPermutations().forEach(action -> {
                System.out.println(action);
            });
        }
                
        
        /*p.getStandardPermutations().forEach(action ->{
            System.out.println(action);
        });*/
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPermutateVariantsWithNegativeLevel(){
        ServerUtils.permutateVariants("", true, (byte) -1);
    }
    
    
    // The permutate test just count the results (so.. no real tests) 
    @Test
    public void testPermutateVariantsLevel0(){
        Permutation variants = ServerUtils.permutateVariants(PERMUTATION_TEST_STRING, true, (byte) 0);
        assertEquals(0, variants.getNonStandardPermutations().size());
        assertEquals(1, variants.getStandardPermutations().size());
        assertEquals(1, variants.size());
    }
    
    @Test
    public void testPermutateVariantsLevel1(){
        Permutation variants = ServerUtils.permutateVariants(PERMUTATION_TEST_STRING, true, (byte) 1);
        assertEquals(8, variants.getNonStandardPermutations().size());
        assertEquals(3, variants.getStandardPermutations().size());
        assertEquals(11, variants.size());
    }
    
    @Test
    public void testPermutateVariantsLevel2(){
        Permutation variants = ServerUtils.permutateVariants(PERMUTATION_TEST_STRING, true, (byte) 2);
        assertEquals(21, variants.getNonStandardPermutations().size());
        assertEquals(3, variants.getStandardPermutations().size());
        assertEquals(24, variants.size());
    }
    
    @Test
    public void testPermutateVariantsLevel3(){
        Permutation variants = ServerUtils.permutateVariants(PERMUTATION_TEST_STRING, true, (byte) 3);
        assertEquals(21, variants.getNonStandardPermutations().size());
        assertEquals(3, variants.getStandardPermutations().size());
        assertEquals(24, variants.size());
    }
    
    @Test
    public void testPermutateVariantsLevel4(){
        Permutation variants = ServerUtils.permutateVariants(PERMUTATION_TEST_STRING, true, (byte) 4);
        assertEquals(106, variants.getNonStandardPermutations().size());
        assertEquals(3, variants.getStandardPermutations().size());
        assertEquals(109, variants.size());
    }
    
    @Test
    public void testIsVariantMatching() {
        Permutation permutation = new Permutation();
        permutation.addStandardPermutation("hello");
        permutation.addStandardPermutation("world");
        permutation.addNonStandardPermutation("nonstandard");
        
        
        Matcher emptyMatcherToReuse = Pattern.compile("").matcher("");
        
        assertFalse(ServerUtils.isVariantMatching(permutation, new WordDictionary("test"), emptyMatcherToReuse, true));
        assertTrue(ServerUtils.isVariantMatching(permutation, new WordDictionary("hello"), emptyMatcherToReuse, true));
        assertTrue(ServerUtils.isVariantMatching(permutation, new WordDictionary("world"), emptyMatcherToReuse, true));
        assertTrue(ServerUtils.isVariantMatching(permutation, new WordDictionary("world"), emptyMatcherToReuse, false));
        assertTrue(ServerUtils.isVariantMatching(permutation, new WordDictionary("nonstandard"), emptyMatcherToReuse, true));
        assertFalse(ServerUtils.isVariantMatching(permutation, new WordDictionary("nonstandard"), emptyMatcherToReuse, false));
        
    }

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
        
        assertEquals("/test", ServerUtils.extractResourceToBeAccessed("", "test", "http://localhost/test", true));
        assertEquals("/test", ServerUtils.extractResourceToBeAccessed("?", "test", "http://localhost/test", true));
        assertEquals("/test/index.html", ServerUtils.extractResourceToBeAccessed("", "test", "http://localhost/test/index.html", true));
        assertEquals("test/index.html", ServerUtils.extractResourceToBeAccessed("./index.html", "test", "http://localhost/test/", true));
        assertEquals("test/", ServerUtils.extractResourceToBeAccessed("test/", "test", "http://localhost/test/", true));
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
        assertEquals("", ServerUtils.removeParameterFromQueryString("param", "param"));
        assertEquals("", ServerUtils.removeParameterFromQueryString("param=test", "param"));
        assertEquals("param2=test2", ServerUtils.removeParameterFromQueryString("param1=test&param2=test2", "param1"));
        assertEquals("param1=test", ServerUtils.removeParameterFromQueryString("param1=test&param2=test2", "param2"));
        assertEquals("hello", ServerUtils.removeParameterFromQueryString("hello", "world"));
        //assertEquals("param1=test", ServerUtils.removeParameterFromQueryString("param1=test&", "world"));
    }

    
    
    @Test(expected = NullPointerException.class)
    public void testFindReusableSessionContentKeyOrCreateNewOneWithoutContent() {
        ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, null, true, true);
    }

    @Test
    public void testFindReusableSessionContentKeyOrCreateNewOneWithoutReuseSession(){
        ParameterAndFormProtection content = new ParameterAndFormProtection(true);
        String result = ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, content, false, false);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(session).setAttribute(WebCastellumFilter.INTERNAL_CONTENT_PREFIX+result, content);
    }
    
    @Test
    public void testFindReusableSessionContentKeyOrCreateNewOneWithReuseSession(){
        ParameterAndFormProtection content = new ParameterAndFormProtection(true);
        
        when(session.getAttribute(WebCastellumFilter.SESSION_REUSABLE_KEY_LIST_KEY)).thenReturn(new ArrayList(Arrays.asList("testKey")));

        //correct key but wrong content
        String result = ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, content, true, false);
        assertNotEquals("testKey", result);
        
        
        //key and content matches
        when(session.getAttribute(WebCastellumFilter.INTERNAL_CONTENT_PREFIX+"testKey")).thenReturn(content);
        result = ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, content, true, true);
        
        assertEquals("testKey", result);
    }
    
    
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
        assertEquals("te\\\\\"st", ServerUtils.quoteReplacement("te\\\"st"));
        assertEquals("te\\$t", ServerUtils.quoteReplacement("te$t"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetAttributeNamesIncludingInternalWithoutSession(){
        ServerUtils.getAttributeNamesIncludingInternal(null);
    }
    
    @Test
    public void testGetAttributeNamesIncludingInternal(){
        List list = Arrays.asList("attribute1", "attribute2");
        when(session.getAttributeNames()).thenReturn(Collections.enumeration(list));

        ServerUtils.getAttributeNamesIncludingInternal(session).asIterator().forEachRemaining(action -> {
            assertTrue(list.contains(action));
        });
    }
    
    @Test(expected = NullPointerException.class)
    public void testGetAttributeIncludingInternalWithoutSession() {
        ServerUtils.getAttributeIncludingInternal(null,"");
    }
    
    @Test
    public void testGetAttributeIncludingInternal() {
        when(session.getAttribute("testAttribute")).thenReturn("testValue");
        assertEquals("testValue", ServerUtils.getAttributeIncludingInternal(session,"testAttribute"));
    }

    @Test
    public void testConvertListOfPatternToArrayOfMatcherWithoutPatterns() {
        assertNull(ServerUtils.convertListOfPatternToArrayOfMatcher(null));
    }
    
    @Test
    public void testConvertListOfPatternToArrayOfMatcher() {
        List patterns = Arrays.asList(
                Pattern.compile("[A-Z]"), 
                Pattern.compile("[0-9]"));
        Matcher[] m = ServerUtils.convertListOfPatternToArrayOfMatcher(patterns);
        
        assertEquals(2, m.length);
        assertEquals("[A-Z]", m[0].pattern().pattern());
        assertEquals("[0-9]", m[1].pattern().pattern());
    }

    @Test
    public void testConcatenateArrays() {
        String[] original = new String[]{"Kermit", "Gonzo", "Fozzie"};
        String[] additional = new String[]{"Waldorf", "Statler"};
        
        assertArrayEquals(additional, ServerUtils.concatenateArrays(null, additional));
        assertArrayEquals(additional, ServerUtils.concatenateArrays(new String[0], additional));
        assertArrayEquals(original, ServerUtils.concatenateArrays(original, null));
        assertArrayEquals(original, ServerUtils.concatenateArrays(original, new String[0]));
        
        String[] combined = ServerUtils.concatenateArrays(original, additional);
        assertEquals(5, combined.length);
        assertEquals("Fozzie", combined[2]);
        assertEquals("Waldorf", combined[3]);
    }

    @Test
    public void testConvertIntegerListToIntArray() {
        List intList = Arrays.asList(1, 2, 3, 4 ,5);
        
        assertNull(ServerUtils.convertIntegerListToIntArray(null));
        int[] intArray = ServerUtils.convertIntegerListToIntArray(intList);
        assertEquals(5, intArray.length);
        assertEquals(1, intArray[0]);
        assertEquals(5, intArray[4]);
    }
    
}
