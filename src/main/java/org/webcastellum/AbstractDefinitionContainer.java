package org.webcastellum;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class AbstractDefinitionContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected static final String KEY_DESCRIPTION = "description";
    protected static final String KEY_ENABLED = "enabled";

    
    
    protected final RuleFileLoader ruleFileLoader;
    
    protected SortedSet/*<AbstractDefinition>*/ definitions = new TreeSet();

    // shortcut
    protected boolean hasEnabledDefinitions = false;

    
    
    public AbstractDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        if (ruleFileLoader == null) throw new NullPointerException("ruleFileLoader must not be null");
        this.ruleFileLoader = ruleFileLoader;
    }
    
    

    
    /** 
     * NOTE: The caller of this class already synchronizes the reloading and using of rules properly 
     * (see WebCastellumFilter.doFilter() and WebCastellumFilter.doBeforeProcessing()), so that synchronization
     * is not required here: the caller ensures that rule reloading and rule using is completely serialized
     *
     *@return message to log when loading is finished (to avoid logging while within synchronized block, since Tomcat for example has problems with stdout to a file after a hibernation when in synchronized block)
     */
    public abstract String parseDefinitions() throws RuleLoadingException, IllegalRuleDefinitionFormatException;
    
/** 
     * NOTE: The caller of this class already synchronizes the reloading and using of rules properly 
     * (see WebCastellumFilter.doFilter() and WebCastellumFilter.doBeforeProcessing()), so that synchronization
     * is not required here: the caller ensures that rule reloading and rule using is completely serialized
     */
    public final boolean hasEnabledDefinitions() {
        return this.hasEnabledDefinitions;
    }
        
}
