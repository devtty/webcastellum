package org.webcastellum;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 *
 * @author user
 */
public class RequestUtilsTest {

    HttpSession session;
    HttpServletRequest request;
    CryptoKeyAndSalt key;
    
    public RequestUtilsTest() {
    }
    
    @Before
    public void setUp(){
        session = Mockito.mock(HttpSession.class);
        
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getContentType()).thenReturn(null);
        when(request.getHeader("Content-Type")).thenReturn("text/html");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getParameter("param1")).thenReturn("value1");
        when(request.getQueryString()).thenReturn("param1=value1&param2=value2");
        
        try {
            key = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
        } catch (NoSuchAlgorithmException ex) {
            fail("No key");
        }
    }

    @Test
    public void testGetContentType() {
        assertEquals("text/html", RequestUtils.getContentType(request));
    }

    @Test
    public void testDetermineClientIp() {
        ClientIpDeterminator clientIpDeterminator = new ClientIpDeterminator() {
            @Override
            public String determineClientIp(HttpServletRequest request) throws ClientIpDeterminationException {
                return request.getRemoteAddr();
            }

            @Override
            public void setFilterConfig(FilterConfig filterConfig) throws FilterConfigurationException {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };
        assertEquals("192.168.1.1", RequestUtils.determineClientIp(request, clientIpDeterminator));
    }

    @Test
    public void testExtractSecurityRelevantRequestContent() {
        assertEquals("value1", request.getParameter("param1"));
        assertEquals("param1=value1&param2=value2", request.getQueryString());

        String extracted = RequestUtils.extractSecurityRelevantRequestContent(request, "192.168.1.1", true, Pattern.compile("param1"), Pattern.compile("xxx"), Pattern.compile("value1"), true);
        assertFalse(extracted.contains("param1=value1"));
        assertTrue(extracted.contains("param2=value2"));

        extracted = RequestUtils.extractSecurityRelevantRequestContent(request, "192.168.1.1", true, Pattern.compile("xxx"), Pattern.compile("param1=value1"), Pattern.compile("xxx"), true);
        assertFalse(extracted.contains("param1"));
        assertFalse(extracted.contains("value1"));
        assertTrue(extracted.contains("param2"));
        assertTrue(extracted.contains("value2"));
        
        //test message append
        assertTrue(extracted.contains("client = 192.168.1.1"));
        assertTrue(extracted.contains("queryString (sensitive data removed) = <SENSITIVE-DATA-REMOVED>"));
    }

    @Test
    public void testPrintParameterMap() {
        assertNull(RequestUtils.printParameterMap(null));
        Map<String, String[]> map = new HashMap<>();
        map.put("test1", new String[]{"a", "b"});
        map.put("test2", new String[]{"x", "y", "z"});
        
        String print = RequestUtils.printParameterMap(map);
        assertTrue(print.contains("test1-->[a, b]"));
        assertTrue(print.contains("test2-->[x, y, z]"));
        assertEquals("test2-->[x, y, z]   test1-->[a, b]   ", RequestUtils.printParameterMap(map));
    }

    @Test
    public void testChangeKeysToUpperCaseAndUnifyValues() {
        Map<String, String[]> map = new HashMap<>();
        map.put("pets", new String[]{"cat", "dog"});
        map.put("animals", new String[]{"horse", "alligator", "bee"});
        map.put("Pets", new String[]{"tweety"});
        
        RequestUtils.changeKeysToUpperCaseAndUnifyValues(map);
 
        String join = String.join(",", map.get("PETS"));
        
        assertTrue(join.contains("cat"));
        assertTrue(join.contains("dog"));
        assertTrue(join.contains("tweety"));
        assertFalse(join.contains("horse"));
        assertFalse(join.contains("alligator"));
        assertFalse(join.contains("bee"));
    }

    @Test
    public void testCombineArrays() {
        String[] leftPart = new String[]{"a", "b"};
        String[] rightPart = new String[]{"c", "d"};
        String[] combined = RequestUtils.combineArrays(leftPart, rightPart);
        assertEquals(4, combined.length);
        assertEquals("d", combined[3]);
    }

    @Test
    public void testCreateHeaderMap() {        
        when(request.getHeaderNames()).thenReturn(null);
        Map headerMap = RequestUtils.createHeaderMap(request);
        assertTrue(headerMap.isEmpty());
        
        when(request.getHeaderNames()).thenReturn(new StringTokenizer("test value"));
        when(request.getHeaders("test")).thenReturn(new StringTokenizer("rabbit"));
        when(request.getHeaders("value")).thenReturn(new StringTokenizer("roger"));
        
        headerMap = RequestUtils.createHeaderMap(request);
        
        assertEquals("rabbit", ((String[]) headerMap.get("TEST"))[0]);   
    }

    @Test
    public void testCreateCookieMap() {
        when(request.getCookies()).thenReturn(null);
        Map cookieMap = RequestUtils.createCookieMap(request);
        assertTrue(cookieMap.isEmpty());
        
        Cookie[] cookieArray = new Cookie[2];
        cookieArray[0] = new Cookie("test1", "bugs");
        cookieArray[1] = new Cookie("test2", "bunny");
        
        when(request.getCookies()).thenReturn(cookieArray);
        
        assertEquals("bugs", ((String[]) RequestUtils.createCookieMap(request).get("TEST1"))[0]);
    }

    @Test
    @Ignore
    public void testRemoveParameter() {
        //this is tested in the original org.webcastellum.test.RequestUtilsTest
        //TODO compare RequestUtils.removeParameter and ServerUtils.removeParameterFromQueryString
    }

    @Test
    public void testCreateOrRetrieveRandomTokenFromSession_HttpSession_String() {
        String token = RequestUtils.createOrRetrieveRandomTokenFromSession(session, "sessionKey");
        assertNotNull(token);
        assertNotEquals(RequestUtils.createOrRetrieveRandomTokenFromSession(session, "sessionKey"), token);
    }

    @Test
    public void testCreateOrRetrieveRandomTokenFromSession_4args() {
        String token = RequestUtils.createOrRetrieveRandomTokenFromSession(session, "sessionKey", 4, 5);
        assertNotNull(token);
        assertTrue(token.length()>=4);
        assertTrue(token.length()<=5);
        assertNotEquals(RequestUtils.createOrRetrieveRandomTokenFromSession(session, "sessionKey"), token);
    }

    @Test
    public void testRetrieveRandomTokenFromSessionIfExisting() {
        when(request.getSession(false)).thenReturn(null);
        assertNull(RequestUtils.retrieveRandomTokenFromSessionIfExisting(request, "sessionKey"));
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("sessionKey")).thenReturn("hardKey");
        assertEquals("hardKey", RequestUtils.retrieveRandomTokenFromSessionIfExisting(request, "sessionKey"));
    }

    @Test
    public void testCreateOrRetrieveRandomCryptoKeyFromSession() throws Exception {
        CryptoKeyAndSalt cryptoKeyAndSalt = RequestUtils.createOrRetrieveRandomCryptoKeyFromSession(session, "sessionKey", true);
        assertNotNull(cryptoKeyAndSalt);
    }

    /*@Test
    public void testDecryptQueryStringInServletPathWithQueryString() {
        assertNull(RequestUtils.decryptQueryStringInServletPathWithQueryString("", null, "", "", key, "", true, true, true, true, true));
        assertNull(RequestUtils.decryptQueryStringInServletPathWithQueryString("", "", null, "", key, "", true, true, true, true, true));
        
        System.out.println(RequestUtils.decryptQueryStringInServletPathWithQueryString("contextPath", "servletPath", "servletPathWithQueryStringEncrypted", "cryptoDetectionString", key, "uriRequested", true, true, true, true, true));
        String encryptURLSafe = null;
        
        try {
           encryptURLSafe = CryptoUtils.encryptURLSafe("kkk=test.de/context", key, null);
            
        } catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(RequestUtilsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RequestUtils.DecryptedQuerystring s = RequestUtils.decryptQueryStringInServletPathWithQueryString("context", "sadf" , encryptURLSafe, "kkk", key, "http://test", true, true, true, true, true);
        
        System.out.println(s);
    }*/
    
    @Test(expected = NullPointerException.class)
    public void testDecryptQueryStringInServletPathWithQueryStringWithoutContextPath(){
        RequestUtils.decryptQueryStringInServletPathWithQueryString(null, "", "", "", key, "", true, true, true, true, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDecryptQueryStringInServletPathWithQueryStringWithoutCryptoDetectionString(){
        RequestUtils.decryptQueryStringInServletPathWithQueryString("", "", "", null, key, "", true, true, true, true, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDecryptQueryStringInServletPathWithQueryStringWithoutKey(){
        RequestUtils.decryptQueryStringInServletPathWithQueryString("", "", "", "", null, "", true, true, true, true, true);
    }
    
    @Test(expected = NullPointerException.class)
    public void testDecryptQueryStringInServletPathWithQueryStringWithoutRequestUri(){
        RequestUtils.decryptQueryStringInServletPathWithQueryString("", "", "", "", key, null, true, true, true, true, true);
    }
    
    @Test
    public void testIsMismatch() {
        List<String> expectedValues = new ArrayList<>(Arrays.asList("test1", "test2", "test3"));
        
        String[] actualSubmittedValues = new String[]{"test1", "test2"};
        assertTrue(RequestUtils.isMismatch(expectedValues, actualSubmittedValues));
        assertTrue(RequestUtils.isMismatch(expectedValues, null));
        assertTrue(RequestUtils.isMismatch(expectedValues, new String[0]));
        
        actualSubmittedValues = new String[]{"test1", "test2", "test3", "test4"};
        assertTrue(RequestUtils.isMismatch(expectedValues, actualSubmittedValues));
        
        actualSubmittedValues = new String[]{"test1", "test2", "test3"};
        assertFalse(RequestUtils.isMismatch(expectedValues, actualSubmittedValues));
        
        //mismatch because submitted values where removed from expected values
        actualSubmittedValues = new String[]{"test3", "test2", "test1"};
        assertTrue(RequestUtils.isMismatch(expectedValues, actualSubmittedValues));
        
        assertFalse(RequestUtils.isMismatch(new ArrayList<>(Arrays.asList("test1", "test2", "test3")), actualSubmittedValues));
    }
    
    @Test(expected = NullPointerException.class)
    public void testIsMismatchWithoutExpected(){
        String[] actualSubmittedValues = new String[]{"test1", "test2"};
        RequestUtils.isMismatch(null, actualSubmittedValues);
    }
    
    
    @Test
    public void testFilterRequestParameterMap() {
        Set requestParameterMap = new HashSet();
        requestParameterMap.add("test1");
        requestParameterMap.add("test2");
        requestParameterMap.add("_]ygx");
        
        Set filterRequestParameterMap = RequestUtils.filterRequestParameterMap(requestParameterMap);
        
        assertTrue(filterRequestParameterMap.contains("test1"));
        
        //TODO filter removes 'gx' --> dont know how it helps to for imageMapParameterExclude here, seems wrong
        assertFalse(filterRequestParameterMap.contains("_]ygx"));
        assertEquals(3, filterRequestParameterMap.size());
        assertTrue(filterRequestParameterMap.contains("_]y"));
    }

}
