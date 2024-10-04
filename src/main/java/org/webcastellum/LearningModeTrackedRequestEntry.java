package org.webcastellum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    

public final class LearningModeTrackedRequestEntry {
    private String version;
    private String servletPath;
    private String method;
    private String mimeType;
    private String contentLength;
    private String referrer;
    private String encoding;
    private Map/*<String,List<String>>*/ parameters = new HashMap();

    public void addParameter(final String name, final String value) {
        List/*<String>*/ values = (List)this.parameters.get(name);
        if (values == null) {
            values = new ArrayList();
            this.parameters.put(name, values);
        }
        values.add(value);
    }
    public Map/*<String,List<String>>*/ getParameters() {
        return new HashMap(this.parameters);
    }

    public String getEncoding() {
        return encoding;
    }
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferrer() {
        return referrer;
    }
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    
    public String getMimeType() {
        return mimeType;
    }
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    

    public String getServletPath() {
        return servletPath;
    }
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    //1.5@Override
    public String toString() {
        return this.version+": "+this.method+": "+this.servletPath+": "+this.mimeType+": "+this.contentLength+": "+this.encoding+": "+this.referrer+": "+this.parameters;
    }
}
