package org.webcastellum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class SimpleDefinitionContainer extends AbstractDefinitionContainer {

    protected static final String KEY_SERVLET_PATH_OR_REQUEST_URI_PATTERN = "servletPathOrRequestURI";
    protected static final String KEY_SERVLET_PATH_OR_REQUEST_URI_PREFILTER = "servletPathOrRequestURI@prefilter";
    
    
    
    public SimpleDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }
    
    
    
    

    /** 
     * NOTE: The caller of this class already synchronizes the reloading and using of rules properly 
     * (see WebCastellumFilter.doFilter() and WebCastellumFilter.doBeforeProcessing()), so that synchronization
     * is not required here: the caller ensures that rule reloading and rule using is completely serialized
     *
     *@return message to log when loading is finished (to avoid logging while within synchronized block, since Tomcat for example has problems with stdout to a file after a hibernation when in synchronized block)
     */
    // TODO Java5 @Override
    public final String parseDefinitions() throws RuleLoadingException, IllegalRuleDefinitionFormatException {
        final RuleFile[] ruleFiles = this.ruleFileLoader.loadRuleFiles();
        final String message = "WebCastellum loaded "+(ruleFiles.length<10?" ":"")+ruleFiles.length+" security rule"+(ruleFiles.length==1?":  ":"s: ")+this.ruleFileLoader.getPath()+" (via "+this.ruleFileLoader.getClass().getName()+")"; // TODO: Java5 use StringBuilder
        final SortedSet newDefinitions = new TreeSet();
        boolean newHasEnabledDefinitions = false;
        for (int i=0; i<ruleFiles.length; i++) {
            final RuleFile ruleFile = ruleFiles[i];
            final Properties properties = ruleFile.getProperties();
            
            // extract request rules from rule file
            // "enabled" and "description" are the standard base properties
            final boolean enabled = (""+true).equals( properties.getProperty(KEY_ENABLED, "true").trim().toLowerCase() );
            if (enabled) newHasEnabledDefinitions = true;
            final String description = properties.getProperty(KEY_DESCRIPTION);
            if (description == null) throw new IllegalRuleDefinitionFormatException("Description property ("+KEY_DESCRIPTION+") not found in rule file: "+ruleFile);
            final String servletPathOrRequestURI = properties.getProperty(KEY_SERVLET_PATH_OR_REQUEST_URI_PATTERN);
            if (servletPathOrRequestURI == null) throw new IllegalRuleDefinitionFormatException("Servlet path or request URI property ("+KEY_SERVLET_PATH_OR_REQUEST_URI_PATTERN+") not found in rule file: "+ruleFile);
            final String prefilter = properties.getProperty(KEY_SERVLET_PATH_OR_REQUEST_URI_PREFILTER);
            
            try {
                final Pattern servletPathOrRequestURIPattern = Pattern.compile(servletPathOrRequestURI);
                final WordDictionary servletPathOrRequestURIPrefilter = prefilter == null ? null : new WordDictionary(prefilter);
                
                final SimpleDefinition definition = doCreateSimpleDefinition(enabled, ruleFile.getName(), description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern); // TODO: Java5 hier den konkreten Typ (z.B. TotalExcludeDefinition) als generischen Typ T hier rein reichen lassen !!
                doParseSimpleDefinitionDetailsAndRemoveKeys(definition, properties);

                // create a copy for live removal of worked on keys
                final Set/*<String>*/ copyOfKeys = properties.keySet();
                // remove special keys
                copyOfKeys.remove(KEY_DESCRIPTION);
                copyOfKeys.remove(KEY_SERVLET_PATH_OR_REQUEST_URI_PATTERN);
                copyOfKeys.remove(KEY_SERVLET_PATH_OR_REQUEST_URI_PREFILTER);
                copyOfKeys.remove(KEY_ENABLED);
                // check for any unknown keys
                if (!copyOfKeys.isEmpty()) throw new IllegalRuleDefinitionFormatException("Unknown keys ("+copyOfKeys+") found in rule file: "+ruleFile);
                // add Definition to sorted set
                newDefinitions.add(definition);
            } catch (PatternSyntaxException e) {
                throw new IllegalRuleDefinitionFormatException("Invalid regular expression syntax in rule file: "+ruleFile, e);
            }
            
        }
        // now overwrite the previous values/rules: in order to make it as quick and atomic as possible
        this.definitions = newDefinitions;
        this.hasEnabledDefinitions = newHasEnabledDefinitions;
        return message;
    }
    
    
    



    
    protected abstract void doParseSimpleDefinitionDetailsAndRemoveKeys(SimpleDefinition definition, Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException;
    protected abstract SimpleDefinition doCreateSimpleDefinition(boolean enabled, String name, String description, WordDictionary servletPathOrRequestURIPrefilter, Pattern servletPathOrRequestURIPattern);            
    
    
    
    
    
    protected final SimpleDefinition[] getAllMatchingSimpleDefinitions(final String servletPath, final String requestURI) {
        return checkMatchingSimpleDefinitions(false, servletPath, requestURI);
    }

    protected final SimpleDefinition getMatchingSimpleDefinition(final String servletPath, final String requestURI) {
        final SimpleDefinition[] results = checkMatchingSimpleDefinitions(true, servletPath, requestURI);
        if (results.length == 0) return null;
        assert results.length == 1; // since only the first match should be returned here
        return results[0];        
    }

    
    
    private SimpleDefinition[] checkMatchingSimpleDefinitions(final boolean returnOnlyTheFirstMatchingDefinition, final String servletPath, final String requestURI) {
        // shortcuts
        if (!this.hasEnabledDefinitions) return new SimpleDefinition[0]; // empty array = no match
        
        // check
        String resourceAccessed = servletPath!=null && servletPath.trim().length()>0 ? servletPath : ServerUtils.decodeBrokenValueUrlEncodingOnly(requestURI);
        if (resourceAccessed == null) return new SimpleDefinition[0]; // empty array = no match
        resourceAccessed = resourceAccessed.trim();
        if (resourceAccessed.length() == 0) return new SimpleDefinition[0]; // empty array = no match
        
        // loop over definitions and collect matches
        final List/*<SimpleDefinition>*/ results = new ArrayList();
        for (final Iterator iter = this.definitions.iterator(); iter.hasNext();) {
            final SimpleDefinition simpleDefinition = (SimpleDefinition) iter.next();
            if (!simpleDefinition.isEnabled()) {
                continue; // short-circuit to ignore this disabled rule: continue with next definition to check
            }
            
            // check prefilter
            final WordDictionary prefilter = simpleDefinition.getServletPathOrRequestURIPrefilter();
            if (prefilter != null) {
                if (!WordMatchingUtils.matchesWord(prefilter, resourceAccessed, WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) continue; // = continue with next rule, cancel this one
            }
            // match pattern
            final Pattern servletPathOrRequestURIPattern = simpleDefinition.getServletPathOrRequestURIPattern();
            if (servletPathOrRequestURIPattern.matcher(resourceAccessed).find()) {
                results.add(simpleDefinition);
                if (returnOnlyTheFirstMatchingDefinition) return (SimpleDefinition[]) results.toArray(new SimpleDefinition[0]);
            }
        }
        
        return (SimpleDefinition[]) results.toArray(new SimpleDefinition[0]);
    }
    

}
