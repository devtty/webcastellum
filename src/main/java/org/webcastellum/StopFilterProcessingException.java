package org.webcastellum;

/**
 * Signals a desired stop in processing the filter
 */
public class StopFilterProcessingException extends java.lang.Exception {
    
    public StopFilterProcessingException() {
    }
    public StopFilterProcessingException(String msg) {
        super(msg);
    }
    public StopFilterProcessingException(Throwable cause) {
        super(cause);
    }
    public StopFilterProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
