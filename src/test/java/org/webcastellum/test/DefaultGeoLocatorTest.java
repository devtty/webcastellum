package org.webcastellum.test;

import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.webcastellum.DefaultGeoLocator;
import static org.junit.Assert.*;
import org.junit.Before;

public class DefaultGeoLocatorTest {
    
    FilterConfig filterConfig;
    
    public DefaultGeoLocatorTest() {
    }

    @Before
    public void setUp(){
         filterConfig = new FilterConfig(){
            @Override
            public String getFilterName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public ServletContext getServletContext() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String getInitParameter(String string) {
                return null;
                
            }

            @Override
            public Enumeration getInitParameterNames() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
         };
    }

    @Test
    public void testGetCountryCode() throws Exception {
        String ip = "212.85.96.95";
        DefaultGeoLocator locator = new DefaultGeoLocator();
        locator.setFilterConfig(filterConfig);
        String expResult = "PL";
        String result = locator.getCountryCode(ip);
        assertEquals(expResult, result);
    }
    
}
