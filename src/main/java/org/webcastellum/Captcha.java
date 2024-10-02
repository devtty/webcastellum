package org.webcastellum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class Captcha implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String value;
    private final String imageFormat;
    private final int width;
    private final int height;
    private final long expiration = System.currentTimeMillis()+180000L; // = now + 3 minutes

    private Map<String,String[]> originalParameterMap;
    private byte[] image;
    
    private String referenceId;
    
    public Captcha(final String value, final byte[] image, final int width, final int height, final String imageFormat) {
        if (value == null) throw new NullPointerException("value must not be null");
        if (image == null) throw new NullPointerException("image must not be null");
        if (imageFormat == null) throw new NullPointerException("imageFormat must not be null");
        this.value = value;
        this.image = image;
        this.width = width;
        this.height = height;
        this.imageFormat = imageFormat;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getImageFormat() {
        return this.imageFormat;
    }
    
    public int getImageWidth() {
        return this.width;
    }
    
    public int getImageHeight() {
        return this.height;
    }
    
    public byte[] getImage() {
        return this.image;
    }
    
    public synchronized boolean isMatching(String value) {
        if (this.image == null) return false;
        // destroy CAPTCHA in order to avoid being re-used with multiple guesses... a good CAPTCHA does not allow multipl solution attempts, instead a new one should be generated
        this.image = null;
        // check expiration
        if (isExpired()) return false;
        // check value
        if (value == null) return false;
        value = value.trim();
        if (value.length() == 0) return false;
        return this.value.trim().equalsIgnoreCase(value);
    }
    
    
    
    
    public void setOriginalParameterMap(final Map<String,String[]> originalParameterMap) {
        this.originalParameterMap = originalParameterMap == null ? null : new HashMap<>(originalParameterMap);
    }
    public Map<String,String[]> getOriginalParameterMap() {
        return this.originalParameterMap == null ? null : new HashMap<>(this.originalParameterMap);
    }
    public void clearOriginalParameterMap() {
        if (this.originalParameterMap != null) {
            this.originalParameterMap.clear();
            this.originalParameterMap = null;
        }
    }
    
    
    

    public String getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiration;
    }


    
}
