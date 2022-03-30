package org.webcastellum;

import java.util.regex.Pattern;

public final class IncomingProtectionExcludeDefinition extends RequestDefinition {
    private static final long serialVersionUID = 1L;
    
    private boolean excludeForceEntranceProtection, excludeParameterAndFormProtection, excludeSelectboxFieldProtection, excludeCheckboxFieldProtection, excludeRadiobuttonFieldProtection, excludeReferrerProtection, excludeSecretTokenProtection, excludeSessionToHeaderBindingProtection, excludeExtraSessionTimeoutHandling;
 
    public IncomingProtectionExcludeDefinition(final boolean enabled, final String identification, final String description,    final WordDictionary servletPathPrefilter, final Pattern servletPathPattern, final boolean servletPathPatternNegated) {
        super(enabled, identification, description, servletPathPrefilter, servletPathPattern, servletPathPatternNegated);
    }
    public IncomingProtectionExcludeDefinition(final boolean enabled, final String identification, final String description,    final CustomRequestMatcher customRequestMatcher) {
        super(enabled, identification, description, customRequestMatcher);
    }

    
    
    public boolean isExcludeForceEntranceProtection() {
        return excludeForceEntranceProtection;
    }
    public void setExcludeForceEntranceProtection(boolean excludeForceEntranceProtection) {
        this.excludeForceEntranceProtection = excludeForceEntranceProtection;
    }

    public boolean isExcludeParameterAndFormProtection() {
        return excludeParameterAndFormProtection;
    }
    public void setExcludeParameterAndFormProtection(boolean excludeParameterAndFormProtection) {
        this.excludeParameterAndFormProtection = excludeParameterAndFormProtection;
    }

    public boolean isExcludeSelectboxFieldProtection() {
        return excludeSelectboxFieldProtection;
    }
    public void setExcludeSelectboxFieldProtection(boolean excludeSelectboxFieldProtection) {
        this.excludeSelectboxFieldProtection = excludeSelectboxFieldProtection;
    }

    public boolean isExcludeCheckboxFieldProtection() {
        return excludeCheckboxFieldProtection;
    }
    public void setExcludeCheckboxFieldProtection(boolean excludeCheckboxFieldProtection) {
        this.excludeCheckboxFieldProtection = excludeCheckboxFieldProtection;
    }

    public boolean isExcludeRadiobuttonFieldProtection() {
        return excludeRadiobuttonFieldProtection;
    }
    public void setExcludeRadiobuttonFieldProtection(boolean excludeRadiobuttonFieldProtection) {
        this.excludeRadiobuttonFieldProtection = excludeRadiobuttonFieldProtection;
    }

    
    

    public boolean isExcludeReferrerProtection() {
        return excludeReferrerProtection;
    }
    public void setExcludeReferrerProtection(boolean excludeReferrerProtection) {
        this.excludeReferrerProtection = excludeReferrerProtection;
    }

    public boolean isExcludeSecretTokenProtection() {
        return excludeSecretTokenProtection;
    }
    public void setExcludeSecretTokenProtection(boolean excludeSecretTokenProtection) {
        this.excludeSecretTokenProtection = excludeSecretTokenProtection;
    }

    
    
    public boolean isExcludeSessionToHeaderBindingProtection() {
        return excludeSessionToHeaderBindingProtection;
    }
    public void setExcludeSessionToHeaderBindingProtection(boolean excludeSessionToHeaderBindingProtection) {
        this.excludeSessionToHeaderBindingProtection = excludeSessionToHeaderBindingProtection;
    }
    
    /**
	 * @return the excludeExtraSessionTimeoutHandling
	 */
	public boolean isExcludeExtraSessionTimeoutHandling() {
		return excludeExtraSessionTimeoutHandling;
	}
	/**
	 * @param excludeExtraSessionTimeoutHandling the excludeExtraSessionTimeoutHandling to set
	 */
	public void setExcludeExtraSessionTimeoutHandling(
			boolean excludeExtraSessionTimeoutHandling) {
		this.excludeExtraSessionTimeoutHandling = excludeExtraSessionTimeoutHandling;
	}
    
    
}
