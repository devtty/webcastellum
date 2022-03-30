package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class DecodingPermutationDefinitionContainer extends SimpleDefinitionContainer {
    
    private static final String KEY_LEVEL = "level";
    
    public static final byte MAX_LEVEL = (byte)4;
    
    
    public DecodingPermutationDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }
    
    
    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new DecodingPermutationDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        DecodingPermutationDefinition decodingPermutationDefinition = (DecodingPermutationDefinition) definition; // TODO: Java5 infer type DecodingPermutationDefinition via Generics instead of downcast
        {
            final String value = properties.getProperty(KEY_LEVEL);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing level property");
            try {
                final byte parsed = Byte.parseByte(value);
                if (parsed < 0) throw new IllegalRuleDefinitionFormatException("Configured level value must not be negative: "+parsed);
                if (parsed > MAX_LEVEL) throw new IllegalRuleDefinitionFormatException("Configured level value must not be greater than "+MAX_LEVEL+": "+parsed);
                properties.remove(KEY_LEVEL);
                decodingPermutationDefinition.setLevel(parsed);
            } catch (NumberFormatException e) {
                throw new IllegalRuleDefinitionFormatException("Unable to number-parse configured level value into a byte: "+value, e);
            }
        }
    }

    
    public final DecodingPermutationDefinition getMatchingDecodingPermutationDefinition(final String servletPath, final String requestURI) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's limits 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        final DecodingPermutationDefinition matchingDecodingPermutationDefinition = (DecodingPermutationDefinition) getMatchingSimpleDefinition(servletPath, requestURI); // TODO: Java5 infer type DecodingPermutationDefinition via Generics instead of downcast
        return matchingDecodingPermutationDefinition;
    }


}
