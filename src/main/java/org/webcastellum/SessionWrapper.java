package org.webcastellum;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public final class SessionWrapper implements HttpSession, Serializable {
    private static final long serialVersionUID = 1L;
    
//    private static final boolean DEBUG = false;
    
    private static final ThreadLocal/*<RequestWrapper>*/ currentPendingRequest = new ThreadLocal();
    
    private final boolean hideInternalSessionAttributes;
    private final HttpSession delegate;
    private final Map/*<String,Object>*/ archivedProtectiveContent = new HashMap();
    private final ContentInjectionHelper contentInjectionHelper;
    private final SessionCreationTracker sessionCreationTracker;
    private final String client;
   
    
    
    // required for special serialization handling
    private Object writeReplace() throws ObjectStreamException {
        return this.delegate;
    }
    
    
    /**
     * Flag if on invalidation of this session the WebCastellum protective content should be archived for further use
     */
    private boolean isArchiveProtectiveSessionContentOnInvalidate = false;
    
    
    public SessionWrapper(final HttpSession delegate, final ContentInjectionHelper contentInjectionHelper, final SessionCreationTracker sessionCreationTracker, final String client, final RequestWrapper requestForAttackHandling, final boolean hideInternalSessionAttributes) {
        if (delegate == null) throw new NullPointerException("delegate must not be null");
        if (contentInjectionHelper == null) throw new NullPointerException("contentInjectionHelper must not be null");
        if (sessionCreationTracker == null) throw new NullPointerException("sessionCreationTracker must not be null");
        if (delegate instanceof SessionWrapper) throw new IllegalArgumentException("delegate must not be of type SessionWrapper (to prevent multiple wrappings)");
        if (client == null) throw new NullPointerException("client must not be null");
        this.delegate = delegate;
        this.contentInjectionHelper = contentInjectionHelper;
        this.sessionCreationTracker = sessionCreationTracker;
        this.client = client;
        this.hideInternalSessionAttributes = hideInternalSessionAttributes;
        // track session creation
        if (delegate.isNew()) this.sessionCreationTracker.trackSessionCreation(this.client, requestForAttackHandling);
    }

    public boolean isUsingDelegateSession(final HttpSession delegate) {
        if (delegate == null) return false;
        return delegate == this.delegate; // = check object identity
    }
    
    
    public long getCreationTime() {
        return this.delegate.getCreationTime();
    }

    public String getId() {
        return this.delegate.getId();
    }

    public long getLastAccessedTime() {
        return this.delegate.getLastAccessedTime();
    }

    public ServletContext getServletContext() {
        return this.delegate.getServletContext();
    }

    public void setMaxInactiveInterval(int interval) {
        this.delegate.setMaxInactiveInterval(interval);
    }

    public int getMaxInactiveInterval() {
        return this.delegate.getMaxInactiveInterval();
    }

    public HttpSessionContext getSessionContext() {
        return this.delegate.getSessionContext();
    }

    public void setAttribute(final String key, final Object object) {
        this.delegate.setAttribute(key,object);
    }

    public void putValue(final String key, final Object object) {
        this.delegate.putValue(key,object);
    }

    public void removeAttribute(final String key) {
        this.delegate.removeAttribute(key);
    }

    public void removeValue(final String key) {
        this.delegate.removeValue(key);
    }

    public boolean isNew() {
        return this.delegate.isNew();
    }

    
    
    
    
    
    
    // here we offer alternative methods that also show the WebCastellum internal session contents,
    // while the regular session methods hide that WebCastellum internal session contents

    public Object getAttribute(final String key) {
        if (this.hideInternalSessionAttributes && isInternal(key)) {
            System.err.println("Internal session attribute name is about to be accessed from outside world: "+key);
            return null;
        }
        return this.delegate.getAttribute(key);
    }
    Object getAttributeIncludingInternal(final String key) {
        return this.delegate.getAttribute(key);
    }
    
    
    public Object getValue(final String key) {
        if (this.hideInternalSessionAttributes && isInternal(key)) {
            System.err.println("Internal session attribute name is about to be accessed from outside world: "+key);
            return null;
        }
        return this.delegate.getValue(key);
    }
    Object getValueIncludingInternal(final String key) {
        return this.delegate.getValue(key);
    }
    
    
    public Enumeration getAttributeNames() {
        return this.hideInternalSessionAttributes ? hideInternal(this.delegate.getAttributeNames()) : this.delegate.getAttributeNames();
    }
    Enumeration getAttributeNamesIncludingInternal() {
        return this.delegate.getAttributeNames();
    }
    
    
    public String[] getValueNames() {
        return this.hideInternalSessionAttributes ? hideInternal(this.delegate.getValueNames()) : this.delegate.getValueNames();
    }
    String[] getValueNamesIncludingInternal() {
        return this.delegate.getValueNames();
    }

    
    
    private boolean isInternal(final String key) {
        if (key == null) return false;
        return key.startsWith(WebCastellumFilter.INTERNAL_CONTENT_PREFIX);
    }
    private Enumeration hideInternal(final Enumeration names) {
        if (names == null) return null;
        final Vector namesFiltered = new Vector();
        while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            if ( !isInternal(name) ) namesFiltered.add(name);
        }
        return namesFiltered.elements();
    }
    private String[] hideInternal(final String[] names) {
        if (names == null) return null;
        final Vector namesFiltered = new Vector();
        for (String name : names) {
            if ( !isInternal(name) ) namesFiltered.add(name);
        }
        return (String[]) namesFiltered.toArray(new String[0]);
    }

        
        
    
    
    
    
    
    private final Object mutex = new Object();
    
    protected boolean isArchiveProtectiveSessionContentOnInvalidate() {
        return this.isArchiveProtectiveSessionContentOnInvalidate;
    }
    protected void setArchiveProtectiveSessionContentOnInvalidate(final RequestWrapper currentRequest) {
        synchronized (mutex) {
            this.isArchiveProtectiveSessionContentOnInvalidate = currentRequest != null;
            if (currentRequest == null) SessionWrapper.currentPendingRequest.set(null); // TODO: Java5-specific   1.4 .set(null)   1.5 .remove()
            else SessionWrapper.currentPendingRequest.set(currentRequest);
        }
    }
    
    public void invalidate() {
        // track session invalidation
        this.sessionCreationTracker.trackSessionInvalidation(this.client);
        synchronized (mutex) {
            // archive the protective content before invalidating
            if (this.isArchiveProtectiveSessionContentOnInvalidate && this.archivedProtectiveContent.isEmpty()) {
                for (final Enumeration/*<String>*/ keys = this.delegate.getAttributeNames(); keys.hasMoreElements();) {
                    final String key = (String) keys.nextElement();
                    if (key.startsWith(WebCastellumFilter.INTERNAL_CONTENT_PREFIX) && !key.equals(WebCastellumFilter.SESSION_SESSION_WRAPPER_REFERENCE)) {
                        this.archivedProtectiveContent.put(key, this.delegate.getAttribute(key));
                    }
                }
    //            if (DEBUG) System.out.println("Archived protective content for potential further use: "+this.archivedProtectiveContent);
            }
            this.delegate.invalidate();
    //        if (DEBUG) System.out.println("Invalidated session");
            // AUTO-CREATE new session when required to have one
            if (this.isArchiveProtectiveSessionContentOnInvalidate && !this.archivedProtectiveContent.isEmpty()) {
                if (this.contentInjectionHelper.isEncryptQueryStringInLinks() || this.contentInjectionHelper.isInjectSecretTokenIntoLinks() || this.contentInjectionHelper.isProtectParametersAndForms()) {
                    final RequestWrapper currentRequest = (RequestWrapper) SessionWrapper.currentPendingRequest.get();
                    if (currentRequest != null) try { 
                            currentRequest.getSession();
                        } catch (RuntimeException e) {
                            System.err.println("Unable to auto-create session after application-specific invalidation: "+e.getMessage()); // TODO: better logging
                    }
                }
            }
        }
    }
    
    /**
     * Returns the map of archived WebCastellum protective content
     */
    public Map/*<String,Object>*/ getArchivedProtectiveContent() {
        synchronized (mutex) {
            return new HashMap(this.archivedProtectiveContent);
        }
    }



    
}
