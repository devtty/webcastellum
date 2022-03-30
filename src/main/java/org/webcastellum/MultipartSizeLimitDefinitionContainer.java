package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class MultipartSizeLimitDefinitionContainer extends SimpleDefinitionContainer {
    
    private static final String KEY_MULTIPART_ALLOWED = "multipartAllowed";
    private static final String KEY_MAX_INPUT_STREAM_LENGTH = "maxInputStreamLength";
    private static final String KEY_MAX_FILE_UPLOAD_COUNT = "maxFileUploadCount";
    private static final String KEY_MAX_FILE_UPLOAD_SIZE = "maxFileUploadSize";
    private static final String KEY_MAX_FILE_NAME_LENGTH = "maxFileNameLength";    
    private static final String KEY_ZIP_BOMB_THRESHOLD_TOTAL = "zipBombThresholdTotalSize";
    private static final String KEY_ZIP_BOMB_THRESHOLD_COUNT = "zipBombThresholdFileCount";
    
    
    
    public MultipartSizeLimitDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }
    
    
    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new MultipartSizeLimitDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        MultipartSizeLimitDefinition multipartSizeLimitDefinition = (MultipartSizeLimitDefinition) definition; // TODO: Java5 infer type SizeLimitDefinition via Generics instead of downcast
        {
            final String value = properties.getProperty(KEY_MULTIPART_ALLOWED);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing multipart-size-limit specific value: "+KEY_MULTIPART_ALLOWED+" for rule: "+multipartSizeLimitDefinition.getIdentification());
            multipartSizeLimitDefinition.setMultipartAllowed( (""+true).equals( value.trim().toLowerCase() ) );
            // don't forget to remove the used values
            properties.remove(KEY_MULTIPART_ALLOWED);
        }
        multipartSizeLimitDefinition.setMaxInputStreamLength( getValueAndRemoveKey(properties,KEY_MAX_INPUT_STREAM_LENGTH) );
        multipartSizeLimitDefinition.setMaxFileUploadCount( getValueAndRemoveKey(properties,KEY_MAX_FILE_UPLOAD_COUNT) );
        multipartSizeLimitDefinition.setMaxFileUploadSize( getValueAndRemoveKey(properties,KEY_MAX_FILE_UPLOAD_SIZE) );
        multipartSizeLimitDefinition.setMaxFileNameLength( getValueAndRemoveKey(properties,KEY_MAX_FILE_NAME_LENGTH) );
        multipartSizeLimitDefinition.setZipBombThresholdTotalSize( getValueAndRemoveKey(properties,KEY_ZIP_BOMB_THRESHOLD_TOTAL) );
        multipartSizeLimitDefinition.setZipBombThresholdFileCount( getValueAndRemoveKey(properties,KEY_ZIP_BOMB_THRESHOLD_COUNT) );
    }
    
    
    // TODO: die folgende methode mit der identischen methode aus SizeLimitDefinition mergen
    private int getValueAndRemoveKey(final Properties properties, final String key) throws IllegalRuleDefinitionFormatException {
        final String value = properties.getProperty(key);
        if (value == null) return 0;
        try {
            final int parsed = Integer.parseInt(value);
            if (parsed < 0) throw new IllegalRuleDefinitionFormatException("Configured limit value must not be negative (use 0 for unlimited): "+parsed);
            properties.remove(key);
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalRuleDefinitionFormatException("Unable to number-parse configured limit value into an integer: "+value, e);
        }
    }
    
    
    public final MultipartSizeLimitDefinition getMatchingMultipartSizeLimitDefinition(final String servletPath, final String requestURI) {
        // As per definition it is enough to take the *first* matching rule file (according to the servletPathOrRequestURI) and check only this rule's limits 
        // (that way we've got some kind of overwrite mechanism depending on the alphabetical ordering of the rule-files, which is quite nice)
        return (MultipartSizeLimitDefinition) getMatchingSimpleDefinition(servletPath, requestURI); // TODO: Java5 infer type SizeLimitDefinition via Generics instead of downcast
    }

    
}
