
package org.webcastellum;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import static java.util.Map.entry;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
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

    @Test
    public void testGetQueryString() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("/test?param1=value1&secret_key=token", wrapper.getQueryString());
    }

    @Test
    public void testGetAttribute() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("testAttributeValue", wrapper.getAttribute("testAttribute"));
    }

    @Test
    public void testGetParameter() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertEquals("testParameterValue", wrapper.getParameter("testParameter"));
    }

    @Test
    public void testGetParameterValues() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        assertArrayEquals(new String[]{"x", "y", "z"}, wrapper.getParameterValues("paramValues"));
    }

    @Test
    public void testGetParameterNames() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        Enumeration e = wrapper.getParameterNames();
        assertEquals("param1", (String) e.nextElement());
        assertEquals("param2", (String) e.nextElement());
    }

    @Test
    public void testGetParameterMap() {
        RequestWrapper wrapper = new RequestWrapper(request, helper, sessionCreationTracker,  "123.456.789.000", false, true, true);
        Map parameterMap = wrapper.getParameterMap();
        Object get = parameterMap.get("param1");
        assertNotNull(get);
        assertArrayEquals(new String[]{"a", "b"}, (String[]) get);
    }
    
}
