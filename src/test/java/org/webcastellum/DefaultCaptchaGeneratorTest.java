package org.webcastellum;

import javax.servlet.FilterConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class DefaultCaptchaGeneratorTest {
    
    private FilterConfig filterConfig;
    
    public DefaultCaptchaGeneratorTest() {
    }
    
    @Before
    public void setUp(){
        filterConfig = Mockito.mock(FilterConfig.class);
    }

    @Test
    public void testSetFilterConfigWithNull() throws Exception {
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(null));
        assertTrue(e.getMessage().contains("because \"filterConfig\" is null"));
    }
    
    @Test
    public void testGenerateCaptchaWithDefaults() throws FilterConfigurationException, CaptchaGenerationException{
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        instance.setFilterConfig(filterConfig);
        Captcha captcha = instance.generateCaptcha();
        assertNotNull(captcha.getValue());
        assertEquals("jpeg" , captcha.getImageFormat());
        assertEquals(60, captcha.getImageHeight());
        assertEquals(160, captcha.getImageWidth());
    }
    
    @Test
    public void testGenerateCaptchaWithParams() throws FilterConfigurationException, CaptchaGenerationException{
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_COMPLEXITY)).thenReturn("4");
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_IMAGE_WIDTH)).thenReturn("240");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        instance.setFilterConfig(filterConfig);
        Captcha captcha = instance.generateCaptcha();
        assertNotNull(captcha.getValue());
        assertEquals("jpeg" , captcha.getImageFormat());
        assertEquals(60, captcha.getImageHeight());
        assertEquals(240, captcha.getImageWidth());
    }

}
