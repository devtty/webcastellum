package org.webcastellum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 *  This request wrapper class extends the support class HttpServletRequestWrapper,
 *  which implements all the methods in the HttpServletRequest interface, as
 *  delegations to the wrapped request.
 *  You only need to override the methods that you need to change.
 *  You can get access to the wrapped request using the method getRequest()
 */
public final class RequestWrapper extends HttpServletRequestWrapper {
    
    private static final Logger LOGGER = Logger.getLogger(RequestWrapper.class.getName());
    
    private static final String HEADER_ACCEPT_ENCODING = "accept-encoding";
    private static final Pattern PATTERN_COMPRESSION_ENCODINGS = Pattern.compile("(?i)(gzip|deflate),?");
    
    private final ContentInjectionHelper contentInjectionHelper;
    private final SessionCreationTracker sessionCreationTracker;
    

    /**
     * the current session (the old one when a new one was created by the application) - tracked during a request to detect a session change withn a request
     */
    private SessionWrapper currentSessionOfRequest;
    /**
     * flag if during current request processing the protective session contents of an old session should be transferred to the new session
     */
    private boolean transferProtectiveSessionContentToNewSessionsDefinedByApplication = false;
    /**
     * flag if during current request processing the unsecure parameter value checks should be applied (to avoid applying them multiple times when WebCastellum itself asks for the parameter values)
     */
    private boolean applyUnsecureParameterValueChecks = false;
    
    private final String client;
    private final boolean hideInternalSessionAttributes;
    private final boolean transparentQuerystring;
    private final boolean transparentForwarding;
    
    
    private HttpServletRequest delegate;




    
    public RequestWrapper(HttpServletRequest request, final ContentInjectionHelper contentInjectionHelper, final SessionCreationTracker sessionCreationTracker, 
            final String client, final boolean hideInternalSessionAttributes, final boolean transparentQuerystring, final boolean transparentForwarding) {
        super(request);
        this.delegate = request;
        if (contentInjectionHelper == null) throw new NullPointerException("contentInjectionHelper must not be null");
        if (sessionCreationTracker == null) throw new NullPointerException("sessionCreationTracker must not be null");
        if (client == null) throw new NullPointerException("client must not be null");
        this.contentInjectionHelper = contentInjectionHelper;
        this.sessionCreationTracker = sessionCreationTracker;
        this.client = client;
        this.hideInternalSessionAttributes = hideInternalSessionAttributes;
        this.transparentQuerystring = transparentQuerystring;
        this.transparentForwarding = transparentForwarding;
    }

    @Override
    public String getQueryString() {
        String result = super.getQueryString();
        if (this.transparentQuerystring && result != null) {
            // Secret token key
            final String secretTokenKey = RequestUtils.retrieveRandomTokenFromSessionIfExisting(delegate, WebCastellumFilter.SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
            if (secretTokenKey != null) {
                result = RequestUtils.removeParameter(result, secretTokenKey);
            }
            // PAF token key
            final String pafTokenKey = RequestUtils.retrieveRandomTokenFromSessionIfExisting(delegate, WebCastellumFilter.SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY);
            if (pafTokenKey != null) {
                result = RequestUtils.removeParameter(result, pafTokenKey);
            }
        }
        LOGGER.log(Level.FINE, "getQueryString(): {0}", result);
        return result;
    }

    @Override
    public Object getAttribute(final String name) {
        /* The following is a list of special attribute names used in JavaEE environments
                javax.portlet.config
                javax.portlet.request
                javax.portlet.response

                javax.servlet.context.tempdir

                javax.servlet.error.request_uri

                javax.servlet.include.context_path
                javax.servlet.include.request_uri
                javax.servlet.include.query_string
                javax.servlet.include.servlet_path
                javax.servlet.include.path_info

                javax.servlet.forward.context_path
                javax.servlet.forward.request_uri
                javax.servlet.forward.query_string
                javax.servlet.forward.servlet_path
                javax.servlet.forward.path_info
         */
        LOGGER.log(Level.FINE, "getAttribute({0}) ==> ", name);
        if (this.transparentForwarding
                && this.contentInjectionHelper.isEncryptQueryStringInLinks()
                && isServletApiSpecificForwardAttribute(name)) {
            LOGGER.log(Level.FINE, "null (overwritten)");
            return null;
        }
        LOGGER.log(Level.FINE, "{0}", super.getAttribute(name));
        return super.getAttribute(name);
    }



    protected boolean isInChainCall() {
        return Boolean.TRUE.equals( super.getAttribute(WebCastellumFilter.REQUEST_NESTED_FORWARD_CALL) );
    }

    
    //private Hashtable/*<String,String[]>*/ overwrittenParams = new Hashtable();
    private HashMap<String, String[]> overwrittenParams = new HashMap();
    private Set<String> removedRequestParameters = new HashSet();
    
    protected void removeEncryptedQueryString(final String cryptoDetectionString) {
        if(getParameterNames()!=null)
            getParameterNames().asIterator().forEachRemaining(action -> {if(action.contains(cryptoDetectionString)){
                removeParameter(action);
            }});
    }
    
    protected void removeParameter(final String name) {
        if (name == null) 
            return;
        this.removedRequestParameters.add(name);
    }
    
    protected void setParameter(String name, String[] values, final boolean overwritePreviousValues) {
        if (!overwritePreviousValues) {
            final String[] oldValues = getParameterValues(name);
            values = ServerUtils.concatenateArrays(values, oldValues); // use the new values first, an let the old values be appended !
        }
        this.overwrittenParams.put(name, values);
        if (this.removedRequestParameters.contains(name)) this.removedRequestParameters.remove(name);
    }
    
    @Override
    public String getParameter(final String name) {
        final String result;
        if (this.removedRequestParameters.contains(name)) result = null;
        else if (!this.overwrittenParams.containsKey(name)) result = checkForUnsecureValue(getRequest().getParameter(name));
        else {
            final String[] values = (String[]) this.overwrittenParams.get(name);
            if (values == null || values.length == 0) result = null;
            else result = checkForUnsecureValue(values[0]);
        }
        LOGGER.log(Level.FINE, "getParameter({0})", name);
        return result;
    }
    
    @Override
    public String[] getParameterValues(String name) {
        final String[] result;
        if (this.removedRequestParameters.contains(name)) result = null;
        else if (!this.overwrittenParams.containsKey(name)) result = checkForUnsecureValues(getRequest().getParameterValues(name));
        else result = checkForUnsecureValues((String[])this.overwrittenParams.get(name));
        LOGGER.log(Level.FINE, "getParameterValues({0})", name);
        return result;
    }
    
    @Override
    public Enumeration<String> getParameterNames() { // TODO: ist laut Servlet-Spec es theoretisch moeglich, dass ein Caller auf die Enumeration ein .remove() aufruft und damit einen Wert entfernen moechte? Falls ja, Wrapper-Enumeration hier zurueckgeben
        LOGGER.log(Level.FINE, "getParameterNames()");
        ArrayList<String> names = new ArrayList();
        final Enumeration<String> rawNames = getRequest().getParameterNames();
        if (rawNames == null && this.overwrittenParams.isEmpty()) return null;
        if(rawNames!=null){
            while (rawNames.hasMoreElements()) {
                final String rawName = rawNames.nextElement();
                if (!this.removedRequestParameters.contains(rawName)) names.add(rawName);
            }
        }
        for (final Iterator<String> keys = this.overwrittenParams.keySet().iterator(); keys.hasNext();) {
            final String key = keys.next();
            if (!names.contains(key) && !this.removedRequestParameters.contains(key))
                names.add(key);
        }
        return Collections.enumeration(names);
    }
    
    @Override
    public Map<String,String[]> getParameterMap() { // TODO: ist laut Servlet-Spec es theoretisch moeglich, dass ein Caller auf die Map ein .remove() oder ein .put() aufruft und damit einen Wert entfernen oder setzen moechte? Falls ja, Wrapper-Map hier zurueckgeben
        Map<String,String[]> parameterMap = getRequest().getParameterMap();
        if (!this.overwrittenParams.isEmpty()) {
            if (parameterMap == null) 
                parameterMap = new HashMap<>();
            else 
                parameterMap = new HashMap<>(parameterMap); // defensive copy, since it will be modified locally
            parameterMap.putAll(this.overwrittenParams);
        }
        if (parameterMap == null) {
            LOGGER.log(Level.FINE, "getParameterMap() ==> null");
            return null;
        }
        final Map copy = new HashMap<String,String[]>( parameterMap.size() ); // defensive copy, since it will be modified locally
        for (final Iterator<Map.Entry<String,String[]>> entries = parameterMap.entrySet().iterator(); entries.hasNext();) {
            final Map.Entry<String,String[]> entry = (Entry) entries.next();
            final String key = (String) entry.getKey();
            if (!this.removedRequestParameters.contains(key)) {
                copy.put(key, checkForUnsecureValues((String[]) entry.getValue()));
            }
        }
        LOGGER.log(Level.FINE, "getParameterMap()");
        return copy;
    }

    // allow direct access to underlying delegate (used internally)
    Map<String, String[]> getOriginalParameterMap() {
        return this.delegate.getParameterMap();
    }
    
    String[] getOriginalParameterValues(String name) {
        return this.delegate.getParameterValues(name);
    }
        
    private SessionWrapper fetchOrCreateSessionWrapper(final HttpSession session, final boolean forceCreation) {
        SessionWrapper sessionWrapper = null;
        if (!forceCreation) {
            final SessionWrapperTransientBox box = (SessionWrapperTransientBox) session.getAttribute(WebCastellumFilter.SESSION_SESSION_WRAPPER_REFERENCE);
            if (box != null) sessionWrapper = box.getSessionWrapper();
        }
        if (sessionWrapper == null) {
            sessionWrapper = new SessionWrapper(session, this.contentInjectionHelper, this.sessionCreationTracker, this.client, this, this.hideInternalSessionAttributes);
            session.setAttribute(WebCastellumFilter.SESSION_SESSION_WRAPPER_REFERENCE, new SessionWrapperTransientBox(sessionWrapper));
        }
        return sessionWrapper;
    }
        
    @Override
    public /*synchronized*/ HttpSession getSession() {
        HttpSession session = super.getSession();
        // check if null + check if session has changed meanwhile
        if (session == null) return null;
        assert session != null;
        
        if (this.currentSessionOfRequest == null) {
            this.currentSessionOfRequest = fetchOrCreateSessionWrapper(session, false);
        } 
        if (!this.currentSessionOfRequest.isUsingDelegateSession(session)) {
            // aha, change in session
            if (this.transferProtectiveSessionContentToNewSessionsDefinedByApplication) {
                // transfer the protective session content from an existing old session to the freshly created (by the application) new session
                transferProtectiveSessionContent(this.currentSessionOfRequest, session);
            }
            this.currentSessionOfRequest = fetchOrCreateSessionWrapper(session, true);
        }
        assert this.currentSessionOfRequest != null;
        return this.currentSessionOfRequest;
    }
    
    @Override
    public /*synchronized*/ HttpSession getSession(final boolean create) {
        HttpSession session = super.getSession(create);
        // check if null + check if session has changed meanwhile
        if (session == null) return null;
        assert session != null;
        if (this.currentSessionOfRequest == null) {
            this.currentSessionOfRequest = fetchOrCreateSessionWrapper(session, false);
        }
        if (!this.currentSessionOfRequest.isUsingDelegateSession(session)) {
            // aha, change in session
            if (this.transferProtectiveSessionContentToNewSessionsDefinedByApplication) {
                // transfer the protective session content from an existing old session to the freshly created (by the application) new session
                transferProtectiveSessionContent(this.currentSessionOfRequest, session);
            }
            this.currentSessionOfRequest = fetchOrCreateSessionWrapper(session, true);
        }
        assert this.currentSessionOfRequest != null;
        return this.currentSessionOfRequest;
    }

    
    protected /*synchronized*/ boolean isTransferProtectiveSessionContentToNewSessionsDefinedByApplication() {
        return this.transferProtectiveSessionContentToNewSessionsDefinedByApplication;
    }
    
    protected /*synchronized*/ void setTransferProtectiveSessionContentToNewSessionsDefinedByApplication(boolean flag) {
        this.transferProtectiveSessionContentToNewSessionsDefinedByApplication = flag;
        if (this.currentSessionOfRequest != null) this.currentSessionOfRequest.setArchiveProtectiveSessionContentOnInvalidate(flag?this:null);
    }
    
    
    /**
     * Transfers the archived WebCastellum protective content from the old source session to the new target session
     */
    private static void transferProtectiveSessionContent(final SessionWrapper source, final HttpSession target) {
        for (final Iterator entries = source.getArchivedProtectiveContent().entrySet().iterator(); entries.hasNext();) {
            final Map.Entry entry = (Map.Entry) entries.next();
            target.setAttribute((String)entry.getKey(), entry.getValue());
        }
    }
    
    private String checkForUnsecureValue(final String value) {
        if (!this.applyUnsecureParameterValueChecks) return value;
        if (value == null) return null;
        // TODO: implementieren mit eigenen Rule-Files und im Angriffsfall: throw new ServerAttackException(...)
        return value;
    }
    private String[] checkForUnsecureValues(final String[] values) {
        if (!this.applyUnsecureParameterValueChecks) return values;
        if (values == null) return null;
        for (String value : values) {
            checkForUnsecureValue(value);
        }
        return values;
    }
    
    
    public /*synchronized*/ boolean isApplyUnsecureParameterValueChecks() {
        return this.applyUnsecureParameterValueChecks;
    }
    public /*synchronized*/ void setApplyUnsecureParameterValueChecks(boolean flag) {
        this.applyUnsecureParameterValueChecks = flag;
    }

    
    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> result = super.getHeaders(name);
        if (WebCastellumFilter.REMOVE_COMPRESSION_ACCEPT_ENCODING_HEADER_VALUES && (name != null && result != null && HEADER_ACCEPT_ENCODING.equalsIgnoreCase(name.trim()))) {
            final ArrayList<String> modified = new ArrayList(5);
            while (result.hasMoreElements()) {
                final String value = (String) result.nextElement();
                modified.add(removeCompressionEncodings(value));
            }
            result = Collections.enumeration(modified);
        }
        return result;
    }
    
    @Override
    public String getHeader(String name) {
        String result = super.getHeader(name);
        if (WebCastellumFilter.REMOVE_COMPRESSION_ACCEPT_ENCODING_HEADER_VALUES && (name != null && result != null && HEADER_ACCEPT_ENCODING.equalsIgnoreCase(name.trim()))) {
            result = removeCompressionEncodings(result);
        }
        return result;
    }

    private static String removeCompressionEncodings(final String value) {
        if (value == null) return value;
        return PATTERN_COMPRESSION_ENCODINGS.matcher(value).replaceAll("");
    }
    
    
    private boolean isServletApiSpecificForwardAttribute(String attributeName){
        return (attributeName != null && (
                attributeName.equals("javax.servlet.forward.context_path") || attributeName.equals("jakarta.servlet.forward.context_path") ||
                attributeName.equals("javax.servlet.forward.path_info") || attributeName.equals("jakarta.servlet.forward.path_info") ||
                attributeName.equals("javax.servlet.forward.query_string") || attributeName.equals("jakarta.servlet.forward.query_string") ||
                attributeName.equals("javax.servlet.forward.request_uri") || attributeName.equals("jakarta.servlet.forward.request_uri") ||
                attributeName.equals("javax.servlet.forward.servlet_path") || attributeName.equals("jakarta.servlet.forward.servlet_path") 
        ));
    }
    
    
}
