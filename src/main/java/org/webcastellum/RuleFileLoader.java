package org.webcastellum;


public interface RuleFileLoader extends Configurable {
    
    // path is here the logical name to use as category
    // TODO: rename path to category
    void setPath(String path);
    String getPath();
    RuleFile[] loadRuleFiles() throws RuleLoadingException;
    
}
