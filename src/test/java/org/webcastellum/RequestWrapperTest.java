
package org.webcastellum;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import static java.util.Map.entry;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class RequestWrapperTest {
    
    HttpServletRequest request;
    ContentInjectionHelper helper;
    AttackHandler attackHandler;
    SessionCreationTracker sessionCreationTracker;
    
    public RequestWrapperTest() {
        request = Mockito.mock(HttpServletRequest.class);
        when(request.getQueryString()).thenReturn("/test?param1=value1&secret_key=token");
        when(request.getAttribute("testAttribute")).thenReturn("testAttributeValue");
        when(request.getParameter("testParameter")).thenReturn("testParameterValue");
        when(request.getParameterMap()).thenReturn(Map.ofEntries(
                entry("param1", new String[]{"a", "b"}),
                entry("param2", new String[]{"c", "d"})
        ));
        when(request.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList("param1", "param2")));
        when(request.getParameterValues("paramValues")).thenReturn(new String[]{"x", "y", "z"});
        helper = new ContentInjectionHelper();
        attackHandler = new AttackHandler(new DefaultAttackLogger(), 123, 600000, 100000, 300000, 300000, null, "MOCK",
                false, false, 
                0, false, false,
                Pattern.compile("sjghggfakgfjagfgajgfjasgfs"), Pattern.compile("sjghggfakgfjagfgajgfjasgfs"), true);
        sessionCreationTracker = new SessionCreationTracker(attackHandler, 0, 600000, 300000, 0, "", "", "", "");        
    }

    @Test(expected = NullPointerException.class)
    public void testRequestWrapperConstructorWithoutContentInjectionHelper(){
        new RequestWrapper(request, null, sessionCreationTracker, "", false, false, false);
    }
    
    @Test(expected = NullPointerException.class)
    public void testRequestWrapperConstructorWithoutSessionCreationTracker(){
        new RequestWrapper(request, helper, null, "", false, false, false);
    }
    
    @Test(expected = NullPointerException.class)
    public void testRequestWrapperConstructorWithoutClient(){
        new RequestWrapper(request, helper, sessionCreationTracker, null, false, false, false);
    }
    
    @Test
    public void testGetQueryString() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("/test?param1=value1&secret_key=token", wrapper.getQueryString());
        

        //add session and secret key
        HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute(WebCastellumFilter.SESSION_SECRET_RANDOM_TOKEN_KEY_KEY)).thenReturn("secret_key");
        
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        //when(request.getSession(true)).thenReturn(session);
        
        when(request.getQueryString()).thenReturn("/test?param1=value1&secret_key=token");
        
        //secret_key shouldn't appear in query string
        assertEquals("/test?param1=value1", wrapper.getQueryString());
        
        when(session.getAttribute(WebCastellumFilter.SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY)).thenReturn("secret_paf");
        when(request.getQueryString()).thenReturn("/test?param1=value1&secret_key=token&secret_paf=token2");
        
        //secret_key & secret_paf shouldn't appear in query string
        assertEquals("/test?param1=value1", wrapper.getQueryString());
    }
    
    @Test
    public void testQueryStringTransparentQueryStringIsFalse(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, false, true);
        assertEquals("/test?param1=value1&secret_key=token", wrapper.getQueryString());
    }

    @Test
    public void testGetAttribute() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("testAttributeValue", wrapper.getAttribute("testAttribute"));
    }
    
    @Test
    public void testGetAttributeNullWhenApiSpecific(){
        when(request.getAttribute("javax.servlet.forward.context_path")).thenReturn("test");
        when(request.getAttribute("attribute")).thenReturn("value");
        
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("test", wrapper.getAttribute("javax.servlet.forward.context_path"));
        
        helper.setEncryptQueryStringInLinks(true);

        wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertNull(wrapper.getAttribute("javax.servlet.forward.context_path"));
        assertEquals("value", wrapper.getAttribute("attribute"));
        
        wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, false);
        assertEquals("test", wrapper.getAttribute("javax.servlet.forward.context_path"));        
        assertEquals("value", wrapper.getAttribute("attribute"));
    }

    @Test
    public void testGetParameter() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("testParameterValue", wrapper.getParameter("testParameter"));
    }
    
    @Test
    public void testGetParameterIfRemoved(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.removeParameter("testParameter");
        assertNull(wrapper.getParameter("testParameter"));
    }
    
    @Test
    public void testGetParameterIfOverwritten(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.setParameter("testParameter", new String[]{"x"}, false);
        assertEquals("x", wrapper.getParameter("testParameter"));
    }
    

    @Test
    public void testGetParameterValues() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertArrayEquals(new String[]{"x", "y", "z"}, wrapper.getParameterValues("paramValues"));
    }
    
    @Test
    public void testGetParameterValuesIfRemoved(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.removeParameter("paramValues");
        assertNull(wrapper.getParameterValues("paramValues"));
    }
    
    @Test
    public void testGetParameterValuesIfOverwritten(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.setParameter("paramValues", new String[]{"x", "y", "z"}, true);
        assertArrayEquals(new String[]{"x", "y", "z"}, wrapper.getParameterValues("paramValues"));
    }

    @Test
    public void testGetParameterNames() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        Enumeration e = wrapper.getParameterNames();
        assertEquals("param1", (String) e.nextElement());
        assertTrue(e.hasMoreElements());
        assertEquals("param2", (String) e.nextElement());
        assertFalse(e.hasMoreElements());
    }
    
    @Test
    public void testGetParameterNamesWithRemovedParameter() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.removeParameter("param1");
        
        Enumeration e = wrapper.getParameterNames();
        assertEquals("param2", (String) e.nextElement());
        assertFalse(e.hasMoreElements());
    }
    
    @Test
    public void testGetParameterNamesWithParameterOverwrite(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.setParameter("param1", new String[]{"x", "y"}, true);
        
        Enumeration e = wrapper.getParameterNames();
        assertEquals("param1", (String) e.nextElement());
        assertTrue(e.hasMoreElements());
        assertEquals("param2", (String) e.nextElement());
        assertFalse(e.hasMoreElements());
    }

    @Test
    public void testGetParameterMap() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        Map parameterMap = wrapper.getParameterMap();
        Object get = parameterMap.get("param1");
        assertNotNull(get);
        assertArrayEquals(new String[]{"a", "b"}, (String[]) get);
    }
    
    @Test
    public void testGetParameterMapWithParameterMapIsNull(){
        when(request.getParameterMap()).thenReturn(null);
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        
        assertNull(wrapper.getParameterMap());
    }
    
    @Test
    public void testGetParameterMapWithOverwrittenParameter(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.setParameter("param1", new String[]{"overwritten"}, true);
        
        Map parameterMap = wrapper.getParameterMap();
        Object get = parameterMap.get("param1");
        assertNotNull(get);
        assertArrayEquals(new String[]{"overwritten"}, (String[]) get);
    }
    
    @Test
    public void testGetParameterMapWithOverwrittenParameterAndMapIsNull(){
        when(request.getParameterMap()).thenReturn(null);
        
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        wrapper.setParameter("param1", new String[]{"overwritten"}, true);
        
        Map parameterMap = wrapper.getParameterMap();
        Object get = parameterMap.get("param1");
        assertNotNull(get);
        assertArrayEquals(new String[]{"overwritten"}, (String[]) get);  
    }
    
    @Test
    public void testGetSession(){
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        
        when(request.getSession()).thenReturn(null);
        assertNull(wrapper.getSession());
        
        HttpSession session = Mockito.mock(HttpSession.class);
        
        when(request.getSession()).thenReturn(session);
        
        HttpSession retrievedSession = wrapper.getSession();
        
        assertNotNull(retrievedSession);
        
        assertTrue(retrievedSession instanceof SessionWrapper);        
    }

}
