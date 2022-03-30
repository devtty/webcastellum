package org.webcastellum;

public interface CaptchaGenerator extends Configurable {

    Captcha generateCaptcha() throws CaptchaGenerationException;
    
}
