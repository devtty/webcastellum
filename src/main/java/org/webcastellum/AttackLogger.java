package org.webcastellum;



public interface AttackLogger extends Configurable {

    void init(final String application, final boolean isProductionMode, final boolean logVerboseForDevelopmentMode);
    void log(final boolean warning, final String message) throws AttackLoggingException;
    void destroy();
    int getPrePostCount() ;
    
}
