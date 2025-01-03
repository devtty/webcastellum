package org.webcastellum;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;

public class ResponseUtilsTest {
    
    Cipher cipher;
    CryptoKeyAndSalt key;
    HttpServletResponse response;
    
    public ResponseUtilsTest() {
    }

    @Before
    public void setUp(){
        try {
            cipher = CryptoUtils.getCipher();
            key = CryptoUtils.generateRandomCryptoKeyAndSalt(true);
            response = Mockito.mock(HttpServletResponse.class);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            fail("failed setUp");
        }
    }
    
    @Test
    public void testExtractURI() {
        assertNull(ResponseUtils.extractURI(null));
        assertEquals("http://test", ResponseUtils.extractURI("http://test?test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test?test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test?test;test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test#test;test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test;test#test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test#test?test"));
        assertEquals("http://test/test", ResponseUtils.extractURI("http://test/test#test?test;test"));
        assertEquals("ftp://user@test:25/test", ResponseUtils.extractURI("ftp://user@test:25/test#test?test;test"));
                
    }

    @Test
    public void testInjectParameterIntoURL() {
        final String testUrl = "test?param=value+1%20%3F&param2=value2";
        assertNull(ResponseUtils.injectParameterIntoURL(null, "tokenKey", "tokenValue", true, true, true));
        assertEquals(testUrl, ResponseUtils.injectParameterIntoURL(testUrl, null, "tokenValue", true, true, true));
        assertEquals(testUrl, ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", null, true, true, true));
        assertEquals(testUrl, ResponseUtils.injectParameterIntoURL(testUrl, "param", "anotherValue", true, true, true));
        
        assertEquals("test?param=value+1%20%3F&param2=value2&amp;tokenKey=tokenValue&", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", true, true, true));
        assertEquals("test?param=value+1%20%3F&param2=value2&amp;tokenKey=tokenValue&", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", true, true, false));
        assertEquals("test?param=value+1%20%3F&param2=value2&tokenKey=tokenValue&", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", false, true, false));
        assertEquals("test?param=value+1%20%3F&param2=value2&tokenKey=tokenValue&", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", false, true, true));
        assertEquals("test?param=value+1%20%3F&param2=value2&tokenKey=tokenValue", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", false, false, false));
        assertEquals("test?param=value+1%20%3F&param2=value2&amp;tokenKey=tokenValue", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", true, false, false));
        assertEquals("test?param=value+1%20%3F&param2=value2&tokenKey=tokenValue", ResponseUtils.injectParameterIntoURL(testUrl, "tokenKey", "tokenValue", false, false, true));
        
        assertEquals("javascript://test?tokenKey=tokenValue", ResponseUtils.injectParameterIntoURL("javascript://test", "tokenKey", "tokenValue", false, false, true));
        assertEquals("javascript://test", ResponseUtils.injectParameterIntoURL("javascript://test", "tokenKey", "tokenValue", false, false, false));
    }
    
    @Test
    public void testInjectParameterIntoURLWithAnchor() {
        assertEquals("test?param=value+1%20%3F&param2=value2&tokenKey=tokenValue#anchor", ResponseUtils.injectParameterIntoURL(
                "test?param=value+1%20%3F&param2=value2#anchor",
                "tokenKey", "tokenValue", false, false, true));
    }

    @Test
    public void testSetFieldValue() {
        assertNull(ResponseUtils.setFieldValue(null, "test"));
        
        assertEquals("<input value=\"\"  type=\"radio\"  ></input>", ResponseUtils.setFieldValue(" <input type=\"radio\" value=\"empty field\"></input> ", null));
        assertEquals("<input value=\"test\"  type=\"radio\"  ></input>", ResponseUtils.setFieldValue(" <input type=\"radio\" value=\"empty field\"></input> ", "test"));
        assertEquals("<input value='test'  type='radio'  ></input>", ResponseUtils.setFieldValue(" <input type='radio' value='empty field'></input> ", "test"));
        //TODO DR method removes old value and inserts new value but doesnt care about position and spaces; test should proof content not string equality
    }

    @Test
    public void testSetFieldAction() {
        assertNull(ResponseUtils.setFieldAction(null, "test"));
        
        assertEquals("<form action=\"\"    method=\"POST\">", ResponseUtils.setFieldAction("<form action=\"oldAction\" method=\"POST\">", null));
        assertEquals("<form action=\"newAction\"    method=\"POST\">", ResponseUtils.setFieldAction("<form action=\"oldAction\" method=\"POST\">", "newAction"));
        assertEquals("<form action='newAction'    method='POST'>", ResponseUtils.setFieldAction("<form action='oldAction' method='POST'>", "newAction"));
        
        assertEquals(" action=\"newAction\"  ", ResponseUtils.setFieldAction("action=\"oldAction", "newAction"));
        assertEquals(" action=\"newAction\"  ", ResponseUtils.setFieldAction("action=\"oldAction", "newAction"));
        assertEquals("<form action=\"newAction\"  method=\"GET\"  ", ResponseUtils.setFieldAction("<form method=\"GET\" action=\"oldAction", "newAction"));
        assertEquals("<form action=\"newAction\"  method=GET  ", ResponseUtils.setFieldAction("<form method=GET action=oldAction", "newAction"));
        //TODO DR method removes old value and inserts new value but doesnt care about position and spaces; test should proof content not string equality
    }

    @Test
    public void testExtractFieldValue() {
        assertNull(ResponseUtils.extractFieldValue(null));
        assertEquals("test", ResponseUtils.extractFieldValue("<input value=\"test\">"));
        assertEquals("test", ResponseUtils.extractFieldValue("<input value='test'>"));
        assertEquals("test", ResponseUtils.extractFieldValue("<input value=test>"));
        assertEquals("test", ResponseUtils.extractFieldValue("<input value = test >"));
        assertNull(ResponseUtils.extractFieldValue("<input type = radio"));
    }

    @Test
    public void testExtractFieldEnctype() {
        assertNull(ResponseUtils.extractFieldEnctype(null));
        assertEquals("test", ResponseUtils.extractFieldEnctype("<form enctype=\"test\">"));
        assertEquals("test", ResponseUtils.extractFieldEnctype("<form enctype='test'>"));
        assertEquals("test", ResponseUtils.extractFieldEnctype("<form enctype=test>"));
        assertEquals("test", ResponseUtils.extractFieldEnctype("<form enctype = test >"));
        assertNull(ResponseUtils.extractFieldEnctype("<form method = POST >"));
    }

    @Test
    public void testExtractFieldName() {
        assertNull(ResponseUtils.extractFieldName(null));
        assertEquals("test", ResponseUtils.extractFieldName("<input name=\"test\">"));
        assertEquals("test", ResponseUtils.extractFieldName("<input name='test'>"));
        assertEquals("test", ResponseUtils.extractFieldName("<input name=test>"));
        assertEquals("test", ResponseUtils.extractFieldName("<input name = test >"));
    }

    @Test
    public void testIsMultipartForm() {
        assertTrue(ResponseUtils.isMultipartForm("<form enctype=\"multipart/form-data\">"));
        assertTrue(ResponseUtils.isMultipartForm("<form enctype='multipart/form-data'>"));
        assertFalse(ResponseUtils.isMultipartForm("<form enctype=\"text/plain\">"));
        assertFalse(ResponseUtils.isMultipartForm("<form enctype='text/plain'>"));
        assertFalse(ResponseUtils.isMultipartForm("<form enctype=\"application/x-www-form-urlencoded\">"));
        assertFalse(ResponseUtils.isMultipartForm("<form enctype='application/x-www-form-urlencoded'>"));
        assertFalse(ResponseUtils.isMultipartForm("<form>"));
        assertFalse(ResponseUtils.isMultipartForm("<form'>"));
        assertFalse(ResponseUtils.isMultipartForm(null));
    }

    @Test
    public void testIsAlreadyEncrypted() {
        assertTrue(ResponseUtils.isAlreadyEncrypted("test", "url?test=1"));
        assertTrue(ResponseUtils.isAlreadyEncrypted("test", "url?param1&test=1"));
        assertFalse(ResponseUtils.isAlreadyEncrypted("test", "url"));
        assertFalse(ResponseUtils.isAlreadyEncrypted(null, "test"));
    }
    
    @Test
    public void testEncryptQueryStringInURLWithBothRemovals(){
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> ResponseUtils.encryptQueryStringInURL("", "", "", "", true, true, Boolean.TRUE, true, "", cipher, key, true, true, true, response, true));
        assertEquals("additionalFullResourceRemoval AND additionalMediumResourceRemoval is impossible", iae.getMessage());
    }

    @Test
    public void testEncryptQueryStringInURLWithoutURL(){
        assertNull(ResponseUtils.encryptQueryStringInURL("currentRequestUrlToCompareWith", "currentContextPathAccessed", "currentServletPathAccessed", null, true, false, Boolean.TRUE, true, "cryptoDetectionString", cipher, key, true, true, true, response, true));
    }
    
    @Test
    public void testEncryptQueryStringInURLRemovals(){
        String encryptedString = ResponseUtils.encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2", "contextPath", "servletPath", "http://www.example.com/demo/test.html?id=16&huhu=haha#anchor7;jsession=uuuuuu", true, false, Boolean.TRUE, true, "1234567890", cipher, key, false, false, false, response, false);
        assertTrue(encryptedString.startsWith("http://www.example.com/demo/test.html"));
        assertTrue(encryptedString.contains("#anchor7"));
        assertTrue(encryptedString.contains("jsession=uuuuuu"));
        
        encryptedString = ResponseUtils.encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2", "contextPath", "servletPath", "http://www.example.com/demo/test.html?id=16&huhu=haha#anchor7;jsession=uuuuuu", true, false, Boolean.TRUE, true, "1234567890", cipher, key, false, false, true, response, false);
        assertTrue(encryptedString.startsWith("http://www.example.com/demo"));
        assertFalse(encryptedString.contains("test.html"));
        assertTrue(encryptedString.contains("#anchor7"));
        assertTrue(encryptedString.contains("jsession=uuuuuu"));

        encryptedString = ResponseUtils.encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2", "contextPath", "servletPath", "http://www.example.com/demo/test.html?id=16&huhu=haha#anchor7;jsession=uuuuuu", true, false, Boolean.TRUE, true, "1234567890", cipher, key, false, true, false, response, false);
        assertFalse(encryptedString.contains("http://www.example.com"));
        assertFalse(encryptedString.contains("demo"));
        assertFalse(encryptedString.contains("test.html"));
        assertTrue(encryptedString.contains("#anchor7"));
        assertTrue(encryptedString.contains("jsession=uuuuuu"));
    }
    
    @Test
    public void testEncryptQueryStringInURL() {
        String encryptedString = ResponseUtils.encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2", "contextPath", "servletPath", "http://www.example.com/demo/test?id=16&huhu=haha#anchor7;jsession=uuuuuu", true, false, Boolean.TRUE, true, "1234567890", cipher, key, false, false, true, response, false);
        
        assertTrue(encryptedString.startsWith("http://www.example.com/demo/"));
        assertFalse(encryptedString.contains("huhu=haha"));
        assertTrue(encryptedString.contains("#anchor7"));
        assertTrue(encryptedString.endsWith("jsession=uuuuuu"));
        
        RequestUtils.DecryptedQuerystring r = RequestUtils.decryptQueryStringInServletPathWithQueryString("contextPath", "servletPath", encryptedString, "1234567890", key, "http://www.example.com/demo/test", true, true, true, true, true);
        
        assertTrue(r.isFormSubmit);
        assertFalse(r.isFormMultipart);
        assertTrue(r.decryptedString.startsWith("test"));
        assertTrue(r.decryptedString.contains("&huhu=haha#anchor7"));
        assertFalse(r.wasManipulated);
        
        //now with dir
        encryptedString = ResponseUtils.encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2", "contextPath", "servletPath", "/demo/?id=16&huhu=haha#anchor7;jsession=uuuuuu", true, false, Boolean.TRUE, true, "1234567890", cipher, key, false, false, true, response, false);
        
        assertTrue(encryptedString.startsWith("/demo/"));
        assertFalse(encryptedString.contains("huhu=haha"));
        assertTrue(encryptedString.contains("#anchor7"));
        assertTrue(encryptedString.endsWith("jsession=uuuuuu"));
        
        r = RequestUtils.decryptQueryStringInServletPathWithQueryString("contextPath", "servletPath", encryptedString, "1234567890", key, "http://www.example.com/demo/test", true, true, true, true, true);
        
        assertTrue(r.isFormSubmit);
        assertFalse(r.isFormMultipart);
        assertTrue(r.decryptedString.contains("&huhu=haha#anchor7"));
        assertFalse(r.wasManipulated);        
    }

    @Test
    public void testGetKeyForParameterProtectionOnly() {
        ParameterAndFormProtection parameterAndFormProtection = new ParameterAndFormProtection(true);
        HttpSession session = Mockito.mock(HttpSession.class);
        
        String r = ResponseUtils.getKeyForParameterAndFormProtection("http://test?param=value", parameterAndFormProtection, session, true, true);
        assertNotNull(r);
    }

    @Test
    public void testGetKeyForParameterAndFormProtection() {
        HttpSession session = Mockito.mock(HttpSession.class);
        
        String r = ResponseUtils.getKeyForParameterProtectionOnly("http://test?param=value", session, true, true, true);
        assertNotNull(r);        
    }

    @Test
    public void testExtractActionUrlOfCurrentForm() {
        assertNull(ResponseUtils.extractActionUrlOfCurrentForm(null, true));
        assertEquals("test", ResponseUtils.extractActionUrlOfCurrentForm("<form action=\"test?param=value\">", false));
        assertEquals("test?param=value", ResponseUtils.extractActionUrlOfCurrentForm("<form action=\"test?param=value\">", true));
        assertEquals("", ResponseUtils.extractActionUrlOfCurrentForm("<form action=\"\">", true));
        assertEquals("", ResponseUtils.extractActionUrlOfCurrentForm("<form>", true));
    }

    @Test
    public void testExtractQueryStringOfActionUrl() {
        assertNull(ResponseUtils.extractQueryStringOfActionUrl(null));
        assertEquals("param=value", ResponseUtils.extractQueryStringOfActionUrl("test?param=value"));
    }

    @Test
    public void testRemoveQueryStringFromActionUrlOfCurrentFormWithAllRemoves() {
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("", true, true, "", null, true, true));
        assertEquals("additionalFullResourceRemoval AND additionalMediumResourceRemoval is impossible", iae.getMessage());
    }
    
    @Test
    public void testRemoveQueryStringFromActionUrlOfCurrentForm() {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        
        assertEquals(ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action=\"test?param=value\">", true, false, "contextPath", response, true, true).replace(" ", "x"), "<form action=\"null?&\"   >", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action=\"test?param=value\">", true, false, "contextPath", response, true, true)); //TODO spaces
        assertEquals(ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action='test?param=value'>", true, false, "contextPath", response, true, true).replace(" ", "x"), "<form action='null?&'   >", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action='test?param=value'>", true, false, "contextPath", response, true, true));
                assertEquals(ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action=\"test?param=value\">", true, false, "contextPath", response, true, true).replace(" ", "x"), "<form action=\"null?&\"   >", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action=\"test?param=value\">", false, true, "contextPath", response, true, true)); //TODO spaces
        assertEquals(ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action='test?param=value'>", true, false, "contextPath", response, true, true).replace(" ", "x"), "<form action='null?&'   >", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action='test?param=value'>", false, true, "contextPath", response, true, true));
        assertEquals("<form action=\"test\">", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action=\"test?param=value\">", false, false, "contextPath", response, true, true)); //TODO spaces
        assertEquals("<form action='test'>", ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm("<form action='test?param=value'>", false, false, "contextPath", response, true, true));

    }


    @Test
    public void testStripQueryString() {
        assertNull(ResponseUtils.stripQueryString(null));
        assertEquals(" ", ResponseUtils.stripQueryString(" "));
        assertEquals("http://demo/test", ResponseUtils.stripQueryString("http://demo/test?param=value"));
        assertEquals("http://demo/test#anker", ResponseUtils.stripQueryString("http://demo/test#anker?param=value&param2=value2"));
    }

    @Test
    public void testRemoveAttributeValues() {
        assertNull(ResponseUtils.removeAttributeValues(null));
        assertEquals("<input type  id  name >", ResponseUtils.removeAttributeValues("<input type=\"text\" id=\"test\" name=\"name\">"));
        assertEquals("<input type  id  name >", ResponseUtils.removeAttributeValues("<input type='text' id='test' name='name'>"));
    }

    @Test
    public void testIsFormFieldDisabled() {
        assertFalse(ResponseUtils.isFormFieldDisabled("<input type=\"\">"));
        assertTrue(ResponseUtils.isFormFieldDisabled("<input type=\"\" disabled>"));
        assertTrue(ResponseUtils.isFormFieldDisabled("<input type=\"\" disabled=\"disabled\">"));
        assertTrue(ResponseUtils.isFormFieldDisabled("<input type='' disabled='disabled'>"));
    }

    @Test
    public void testIsFormFieldReadonly() {
        assertFalse(ResponseUtils.isFormFieldReadonly("<input type=\"\">"));
        assertTrue(ResponseUtils.isFormFieldReadonly("<input readonly type=\"\">"));
        assertTrue(ResponseUtils.isFormFieldReadonly("<input type=\"\" readonly=\"readonly\">"));
        assertTrue(ResponseUtils.isFormFieldReadonly("<input type='' readonly='readonly'>"));
    }

    @Test
    public void testIsFormFieldMultiple() {
        assertFalse(ResponseUtils.isFormFieldMultiple("<input type=\"file\">"));
        assertTrue(ResponseUtils.isFormFieldMultiple("<input multiple type=\"file\">"));
        assertTrue(ResponseUtils.isFormFieldMultiple("<input type=\"file\" multiple=\"multiple\">"));
        assertTrue(ResponseUtils.isFormFieldMultiple("<input type='file' multiple='multiple'>"));
    }

    @Test
    public void testIsFormFieldHavingAttribute() {
        assertFalse(ResponseUtils.isFormFieldHavingAttribute(null, "type"));
        assertTrue(ResponseUtils.isFormFieldHavingAttribute("<input type=\"radio\">", "type"));
        assertTrue(ResponseUtils.isFormFieldHavingAttribute("<input type='radio'>", "type"));
        assertTrue(ResponseUtils.isFormFieldHavingAttribute("<INPUT TYPE=\"radio\">", "type"));
        assertTrue(ResponseUtils.isFormFieldHavingAttribute("<INPUT TYPE='radio'>", "type"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExtractFormFieldValue() {
        ResponseUtils.extractFormFieldValue("");
    }

    @Test
    public void testFirstTenCharactersLower() {
        assertEquals("testfirst", ResponseUtils.firstTenCharactersLower("testFirst"));
        assertEquals("testfirstt", ResponseUtils.firstTenCharactersLower("testFirstT"));
        assertEquals("testfirstt", ResponseUtils.firstTenCharactersLower("testFirstTe"));       
    }
    
}
