package org.webcastellum;

import javax.servlet.FilterConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class DefaultCaptchaGeneratorTest {
    
    private FilterConfig filterConfig;
    
    @Before
    public void setUp(){
        filterConfig = Mockito.mock(FilterConfig.class);
    }

    @Test
    public void testSetFilterConfigWithNull(){
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
    
    @Test
    public void testGenerateCaptchaComplexity_1() throws FilterConfigurationException, CaptchaGenerationException {
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_COMPLEXITY)).thenReturn("1");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        instance.setFilterConfig(filterConfig);
        Captcha captcha = instance.generateCaptcha();
        assertNotNull(captcha.getValue());
    }
    
    @Test
    public void testGenerateCaptchaComplexity_2() throws FilterConfigurationException, CaptchaGenerationException {
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_COMPLEXITY)).thenReturn("2");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        instance.setFilterConfig(filterConfig);
        Captcha captcha = instance.generateCaptcha();
        assertNotNull(captcha.getValue());
    }

    @Test
    public void testGenerateCaptchaComplexity_5() throws FilterConfigurationException, CaptchaGenerationException {
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_COMPLEXITY)).thenReturn("5");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        instance.setFilterConfig(filterConfig);
        Captcha captcha = instance.generateCaptcha();
        assertNotNull(captcha.getValue());
    }
    
    @Test
    public void testGenerateCaptchaComplexity_6(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_COMPLEXITY)).thenReturn("6");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Complexity must be between 1 (easy) and 5 (complex)", e.getMessage());
    }
    
    @Test
    public void testGenerateCaptchaWithNegativeWidth(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_IMAGE_WIDTH)).thenReturn("-240");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Width must be positive", e.getMessage());
    }
    
    @Test
    public void testGenerateCaptchaWithNegativeHeight(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_IMAGE_HEIGHT)).thenReturn("-60");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Height must be positive", e.getMessage());
    }
    
    @Test
    public void testGenerateCaptchaWithNegativeLength(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_LENGTH)).thenReturn("-6");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertEquals("Length must be positive", e.getMessage());
    }
    
    @Test
    public void testGenerateCaptchaWithNotParsableWidth(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_IMAGE_WIDTH)).thenReturn("test");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertTrue(e.getMessage().startsWith("Unable to parse value into short:"));
    }
    
    @Test
    public void testGenerateCaptchaWithNotParsableHeight(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_IMAGE_HEIGHT)).thenReturn("test");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertTrue(e.getMessage().startsWith("Unable to parse value into short:"));
    }
    
    @Test
    public void testGenerateCaptchaWithNotParsableLength(){
        when(filterConfig.getInitParameter(DefaultCaptchaGenerator.PARAM_LENGTH)).thenReturn("test");
        DefaultCaptchaGenerator instance = new DefaultCaptchaGenerator();
        FilterConfigurationException e = assertThrows(FilterConfigurationException.class, () -> instance.setFilterConfig(filterConfig));
        assertTrue(e.getMessage().startsWith("Unable to parse value into byte:"));
    }

}
