package org.webcastellum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public final class CaptchaPointDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
 
    private String captchaPageContent, captchaImageHTML, captchaFormHTML;

    private transient String htmlContentLoaded;
    
            
    public CaptchaPointDefinition(final boolean enabled, final String identification, final String description,    final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public CaptchaPointDefinition(final boolean enabled, final String identification, final String description,    final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }

    
    
    
    
    
    
    public String getCaptchaFormHTML() {
        return captchaFormHTML;
    }
    public void setCaptchaFormHTML(String captchaFormHTML) {
        this.captchaFormHTML = captchaFormHTML;
    }

    
    public String getCaptchaImageHTML() {
        return captchaImageHTML;
    }
    public void setCaptchaImageHTML(String captchaImageHTML) {
        this.captchaImageHTML = captchaImageHTML;
    }

    
    
    
    
    
    public String getCaptchaPageContent() {
        return captchaPageContent;
    }
    public void setCaptchaPageContent(String captchaPageContent) {
        this.captchaPageContent = captchaPageContent;
    }
    
    
    public synchronized String getHtmlContentLoaded() {
        if (this.captchaPageContent == null) throw new IllegalStateException("captchaPageContent must be set first");
        if (this.htmlContentLoaded == null) {
            final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(this.captchaPageContent);
            if (input == null) throw new IllegalStateException("Unable to locate a resource in classpath with name: "+this.captchaPageContent);
            BufferedReader buffer = null;
            try {
                buffer = new BufferedReader( new InputStreamReader(input) );
                final StringBuilder content = new StringBuilder();
                String line;
                while ( (line=buffer.readLine()) != null ) {
                    content.append(line).append("\n");
                }
                this.htmlContentLoaded = content.toString().trim();
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to load content from the specified resource in classpath with name: "+this.captchaPageContent);
            } finally {
                if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
            }
        }
        return this.htmlContentLoaded;
    }
    
}
