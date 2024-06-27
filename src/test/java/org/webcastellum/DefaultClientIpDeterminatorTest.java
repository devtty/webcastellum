package org.webcastellum;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class DefaultClientIpDeterminatorTest {

    private FilterConfig filterConfig;
    private HttpServletRequest request;
    
    public DefaultClientIpDeterminatorTest() {
    }

    @Before
    public void setUp() {
        filterConfig = Mockito.mock(FilterConfig.class);
        request = Mockito.mock(HttpServletRequest.class);
    }
    
    @Test
    public void testExtractFirstIP() {
        String headerFetchedClientIpValue = "127.0.0.1";
        String expResult = "127.0.0.1";
        String result = DefaultClientIpDeterminator.extractFirstIP(headerFetchedClientIpValue);
        
        assertEquals(expResult, result);
    }
    
    @Test
    public void testExtractFirstIPProxied() {
        //TODO DR extractFirst wouldn't work without space before comma, where does come from or implm. error?
        String headerFetchedClientIpValue = "127.0.0.1 , 127.0.0.2 , 127.0.0.3";
        String expResult = "127.0.0.1";
        String result = DefaultClientIpDeterminator.extractFirstIP(headerFetchedClientIpValue);
        assertEquals(expResult, result);
    }

    @Test
    public void testSetFilterConfigWithNull(){
        DefaultClientIpDeterminator instance = new DefaultClientIpDeterminator();
        NullPointerException e = assertThrows(NullPointerException.class, () -> instance.setFilterConfig(null));
        assertEquals("filterConfig must not be null", e.getMessage());
    }

    @Test
    public void testSetFilterConfigDefaults() throws FilterConfigurationException{
        DefaultClientIpDeterminator instance = new DefaultClientIpDeterminator();
        instance.setFilterConfig(filterConfig);
    }
    
    @Test
    public void testDetermineClientIpFromServletRemoteAddr() throws ClientIpDeterminationException{
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        DefaultClientIpDeterminator instance = new DefaultClientIpDeterminator();
        assertEquals("127.0.0.1", instance.determineClientIp(request));
        
    }
    
    @Test
    public void testDetermineClientIpFromHeader() throws FilterConfigurationException, ClientIpDeterminationException{
        when(filterConfig.getInitParameter(DefaultClientIpDeterminator.LEGACY_PARAM_HEADER_NAME)).thenReturn("testHeader");
        when(request.getHeader("testHeader")).thenReturn("127.0.0.2");
        
        DefaultClientIpDeterminator instance = new DefaultClientIpDeterminator();
        instance.setFilterConfig(filterConfig);
        assertEquals("127.0.0.2", instance.determineClientIp(request));
    }
    
    @Test
    public void testDetermineClientIpFromHeaderProxied() throws FilterConfigurationException, ClientIpDeterminationException{
        when(filterConfig.getInitParameter(DefaultClientIpDeterminator.LEGACY_PARAM_HEADER_NAME)).thenReturn("testHeader");
        when(filterConfig.getInitParameter(DefaultClientIpDeterminator.PARAM_SPLIT_HEADER_VALUE)).thenReturn("true");
        when(request.getHeader("testHeader")).thenReturn("127.0.0.2 , 127.0.0.3");
        
        DefaultClientIpDeterminator instance = new DefaultClientIpDeterminator();
        instance.setFilterConfig(filterConfig);
        assertEquals("127.0.0.2", instance.determineClientIp(request));
    }
    
}
