package org.webcastellum;

import java.util.regex.Pattern;

public final class SizeLimitDefinition extends SimpleDefinition {
    
    private int maxHeaderCount=Integer.MAX_VALUE, maxCookieCount=Integer.MAX_VALUE, maxRequestParamCount=Integer.MAX_VALUE;
    private int maxQueryStringLength=Integer.MAX_VALUE, maxHeaderNameLength=Integer.MAX_VALUE, maxHeaderValueLength=Integer.MAX_VALUE, maxCookieNameLength=Integer.MAX_VALUE, maxCookieValueLength=Integer.MAX_VALUE, maxRequestParamNameLength=Integer.MAX_VALUE, maxRequestParamValueLength=Integer.MAX_VALUE;
    private int maxTotalHeaderSize=Integer.MAX_VALUE, maxTotalCookieSize=Integer.MAX_VALUE, maxTotalRequestParamSize=Integer.MAX_VALUE;

        
    public SizeLimitDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    
    

    public int getMaxTotalCookieSize() {
        return maxTotalCookieSize;
    }
    public void setMaxTotalCookieSize(int maxTotalCookieSize) {
        this.maxTotalCookieSize = maxTotalCookieSize;
    }

    public int getMaxTotalHeaderSize() {
        return maxTotalHeaderSize;
    }
    public void setMaxTotalHeaderSize(int maxTotalHeaderSize) {
        this.maxTotalHeaderSize = maxTotalHeaderSize;
    }

    public int getMaxTotalRequestParamSize() {
        return maxTotalRequestParamSize;
    }
    public void setMaxTotalRequestParamSize(int maxTotalRequestParamSize) {
        this.maxTotalRequestParamSize = maxTotalRequestParamSize;
    }

    public int getMaxCookieCount() {
        return maxCookieCount;
    }
    public void setMaxCookieCount(int maxCookieCount) {
        this.maxCookieCount = maxCookieCount;
    }

    public int getMaxCookieNameLength() {
        return maxCookieNameLength;
    }
    public void setMaxCookieNameLength(int maxCookieNameLength) {
        this.maxCookieNameLength = maxCookieNameLength;
    }

    public int getMaxCookieValueLength() {
        return maxCookieValueLength;
    }
    public void setMaxCookieValueLength(int maxCookieValueLength) {
        this.maxCookieValueLength = maxCookieValueLength;
    }

    public int getMaxHeaderCount() {
        return maxHeaderCount;
    }
    public void setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }

    public int getMaxHeaderNameLength() {
        return maxHeaderNameLength;
    }
    public void setMaxHeaderNameLength(int maxHeaderNameLength) {
        this.maxHeaderNameLength = maxHeaderNameLength;
    }

    public int getMaxHeaderValueLength() {
        return maxHeaderValueLength;
    }
    public void setMaxHeaderValueLength(int maxHeaderValueLength) {
        this.maxHeaderValueLength = maxHeaderValueLength;
    }

    public int getMaxQueryStringLength() {
        return maxQueryStringLength;
    }
    public void setMaxQueryStringLength(int maxQueryStringLength) {
        this.maxQueryStringLength = maxQueryStringLength;
    }

    public int getMaxRequestParamCount() {
        return maxRequestParamCount;
    }
    public void setMaxRequestParamCount(int maxRequestParamCount) {
        this.maxRequestParamCount = maxRequestParamCount;
    }

    public int getMaxRequestParamNameLength() {
        return maxRequestParamNameLength;
    }
    public void setMaxRequestParamNameLength(int maxRequestParamNameLength) {
        this.maxRequestParamNameLength = maxRequestParamNameLength;
    }

    public int getMaxRequestParamValueLength() {
        return maxRequestParamValueLength;
    }
    public void setMaxRequestParamValueLength(int maxRequestParamValueLength) {
        this.maxRequestParamValueLength = maxRequestParamValueLength;
    }
    
    
}
