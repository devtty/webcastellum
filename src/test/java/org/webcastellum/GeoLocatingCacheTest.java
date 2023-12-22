package org.webcastellum;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeoLocatingCacheTest {
    
    private static final String IP_ADDRESS = "212.85.96.95";
    private static final String COUNTRY_CODE = "PL";
    
    GeoLocator locator;

    @Before
    public void setUp() throws GeoLocatingException{
        locator = Mockito.mock(GeoLocator.class);
        when(locator.getCountryCode(IP_ADDRESS)).thenReturn(COUNTRY_CODE);
    }
    
    @Test
    public void testGetCountryCodeWithNull() throws GeoLocatingException {
        GeoLocatingCache cache =  new GeoLocatingCache(locator, 3*60*1000L);
        assertNull(cache.getCountryCode(null));
    }
    
    @Test
    public void testGetCountryCodeWithoutLocator() throws GeoLocatingException {
        GeoLocatingCache cache =  new GeoLocatingCache(null, 3*60*1000L);
        assertNull(cache.getCountryCode(IP_ADDRESS));
    }
    
    @Test
    public void testGetCountryCodeWithDisabledLocator() throws GeoLocatingException {
        when(locator.isEnabled()).thenReturn(false);
        
        GeoLocatingCache cache =  new GeoLocatingCache(null, 3*60*1000L);
        assertNull(cache.getCountryCode(IP_ADDRESS));
    }
    
    @Test
    public void testGetCountryCode() throws GeoLocatingException {
        when(locator.isEnabled()).thenReturn(true);
        
        GeoLocatingCache cache =  new GeoLocatingCache(locator, 3*60*1000L);
        assertEquals(COUNTRY_CODE, cache.getCountryCode(IP_ADDRESS));
        
        verify(locator, times(1)).getCountryCode(IP_ADDRESS);
        Mockito.reset(locator);
        
        when(locator.isEnabled()).thenReturn(true);
        assertEquals(COUNTRY_CODE, cache.getCountryCode(IP_ADDRESS));
        verify(locator, never()).getCountryCode(IP_ADDRESS);
    }    
    
}
