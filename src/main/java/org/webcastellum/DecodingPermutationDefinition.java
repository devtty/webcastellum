package org.webcastellum;

import java.util.regex.Pattern;

public final class DecodingPermutationDefinition extends SimpleDefinition {
    
    private byte level;
  
        
    public DecodingPermutationDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    
}
