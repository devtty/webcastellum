package org.webcastellum;

import java.util.regex.Pattern;

public final class RenewSessionAndTokenPointDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
 
    private boolean renewSession, renewSecretToken, renewParamAndFormToken, renewCryptoKey;
    
    public RenewSessionAndTokenPointDefinition(final boolean enabled, final String identification, final String description,    final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public RenewSessionAndTokenPointDefinition(final boolean enabled, final String identification, final String description,    final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }

    
    
    public boolean isRenewSession() {
        return renewSession;
    }
    public void setRenewSession(boolean renewSession) {
        this.renewSession = renewSession;
    }

    public boolean isRenewSecretToken() {
        return renewSecretToken;
    }
    public void setRenewSecretToken(boolean renewSecretToken) {
        this.renewSecretToken = renewSecretToken;
    }

    public boolean isRenewParamAndFormToken() {
        return renewParamAndFormToken;
    }
    public void setRenewParamAndFormToken(boolean renewParamAndFormToken) {
        this.renewParamAndFormToken = renewParamAndFormToken;
    }

    public boolean isRenewCryptoKey() {
        return renewCryptoKey;
    }
    public void setRenewCryptoKey(boolean renewCryptoKey) {
        this.renewCryptoKey = renewCryptoKey;
    }
    
}
