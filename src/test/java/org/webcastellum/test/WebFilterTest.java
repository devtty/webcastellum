package org.webcastellum.test;

import java.io.IOException;
import java.util.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.Before;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.webcastellum.WebCastellumFilter;

public class WebFilterTest {
    /*
    @Mock FilterChain chain;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    
    @Before
    public void setUp() throws Exception {
	request = mock( HttpServletRequest.class );
	response = mock( HttpServletResponse.class );
	chain = mock( FilterChain.class );
    }
    */

     //TODO: several problems with request headers
    @Test
    public void testSimpleRequest() throws ServletException, IOException {

	HttpServletRequest request = mock( HttpServletRequest.class );
	HttpServletResponse response = mock( HttpServletResponse.class );
	FilterChain chain = mock( FilterChain.class );

	
	    WebCastellumFilter f = new WebCastellumFilter();
	    FilterConfig c = mock(FilterConfig.class);
	    Mockito.when(c.getInitParameter("RedirectWelcomePage")).thenReturn("/demoapp");
	    Mockito.when(c.getInitParameter("ApplicationName")).thenReturn("horstp");
	    Mockito.when(c.getInitParameter("LogVerboseForDevelopmentMode")).thenReturn("true");
	    Mockito.when(c.getInitParameter("ProductionMode")).thenReturn("false");
	    Mockito.when(c.getInitParameter("DefaultProductionModeCheckerValue")).thenReturn("false");
	    Mockito.when(c.getInitParameter("Debug")).thenReturn("true");
	    
	    
	    Mockito.when(request.getContextPath()).thenReturn("");
	    Mockito.when(request.getRequestURI()).thenReturn("/js/asd.html");
	    Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
	    Mockito.when(request.getRemoteAddr()).thenReturn("127.0.0.1");
	    Mockito.when(request.getServletPath()).thenReturn("/as");
	    Mockito.when(request.getRemoteHost()).thenReturn("localhost");
	    Mockito.when(request.getScheme()).thenReturn("http");
	    Mockito.when(request.getMethod()).thenReturn("GET");
	    Mockito.when(request.getProtocol()).thenReturn("HTTP/1.1");
	    Mockito.when(request.getServerName()).thenReturn("localhost");
	    
	    Map<String,String> headers = new HashMap<>();
	    headers.put(null, "HTTP/1.1 200 OK");
	    headers.put("user-agent", "User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");
	    headers.put("User-Agent", "User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:94.0) Gecko/20100101 Firefox/94.0");

		
	    Enumeration<String> headerNames = Collections.enumeration(headers.keySet());



	    Map<String,String> params = new HashMap<>();
	    params.put(null, "asdf");
	    Enumeration<String> paramNames = Collections.enumeration(params.keySet());

	    
	    Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
	    Mockito.when(request.getParameterNames()).thenReturn(paramNames);

	    Mockito.doAnswer(new Answer<Enumeration<String>>() {
		    @Override
		    public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
			Object[] args = invocation.getArguments();
			List l = new ArrayList<String>();
			l.add(headers.get((String) args[0]));
			return Collections.enumeration(l);
		    }
		}).when(request).getHeaders("User-Agent");

	    //TODO check source getHeaders(String) vs getHeader(String) ; for user-agent both nec.?
	    Mockito.doAnswer(new Answer<String>() {
		    @Override
		    public String answer(InvocationOnMock invocation) throws Throwable {
			Object[] args = invocation.getArguments();
			return headers.get((String) args[0]);
		    }
		}).when(request).getHeader("user-agent");

	    
	    f.init(c);
	    
	    f.doFilter(request, response, chain);

	    Mockito.verify(chain).doFilter(request, response);
	    
	    // https://stackoverflow.com/questions/13365536/how-to-junit-test-servlet-filter-which-has-specific-response
	    //	    verify(response).sendError(503);
    }

}
