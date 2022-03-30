package org.webcastellum;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class FormFieldMaskingExcludeDefinitionContainer extends SimpleDefinitionContainer {

    private static final String KEY_FORM_NAME_PREFILTER = "formNamePattern@prefilter"; // = prefilter of form name
    private static final String KEY_FORM_NAME_PATTERN = "formNamePattern"; // = pattern of form name

    private static final String KEY_FIELD_NAME_PREFILTER = "fieldNamePattern@prefilter"; // = prefilter of field name
    private static final String KEY_FIELD_NAME_PATTERN = "fieldNamePattern"; // = pattern of field name

    
    public FormFieldMaskingExcludeDefinitionContainer(final RuleFileLoader ruleFileLoader) {
        super(ruleFileLoader);
    }


    protected SimpleDefinition doCreateSimpleDefinition(final boolean enabled, final String name, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        return new FormFieldMaskingExcludeDefinition(enabled, name, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    protected void doParseSimpleDefinitionDetailsAndRemoveKeys(final SimpleDefinition definition, final Properties properties) throws PatternSyntaxException, IllegalRuleDefinitionFormatException {
        final FormFieldMaskingExcludeDefinition formFieldMaskingExcludeDefinition = (FormFieldMaskingExcludeDefinition) definition;
        
        // load custom properties
       {
            final String value = properties.getProperty(KEY_FORM_NAME_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing form-field-masking-exclude specific value: "+KEY_FORM_NAME_PATTERN+" for rule: "+formFieldMaskingExcludeDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                formFieldMaskingExcludeDefinition.setFormNamePattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_FORM_NAME_PATTERN+" for rule: "+formFieldMaskingExcludeDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_FORM_NAME_PREFILTER);
            if (prefilter != null) formFieldMaskingExcludeDefinition.setFormNamePrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_FORM_NAME_PATTERN);
            properties.remove(KEY_FORM_NAME_PREFILTER);
        }
       {
            final String value = properties.getProperty(KEY_FIELD_NAME_PATTERN);
            if (value == null) throw new IllegalRuleDefinitionFormatException("Missing form-field-masking-exclude specific value: "+KEY_FIELD_NAME_PATTERN+" for rule: "+formFieldMaskingExcludeDefinition.getIdentification());
            if (value.trim().length() > 0) try {
                final Pattern pattern = Pattern.compile(value);
                formFieldMaskingExcludeDefinition.setFieldNamePattern(pattern);
            } catch (PatternSyntaxException ex) {
                throw new IllegalRuleDefinitionFormatException("Unable to compile regular expression pattern for "+KEY_FIELD_NAME_PATTERN+" for rule: "+formFieldMaskingExcludeDefinition.getIdentification()+": "+ex.getMessage());
            }
            // optional PREFILTER of this pattern
            final String prefilter = properties.getProperty(KEY_FIELD_NAME_PREFILTER);
            if (prefilter != null) formFieldMaskingExcludeDefinition.setFieldNamePrefilter( new WordDictionary(prefilter) );
            // don't forget to remove the used values
            properties.remove(KEY_FIELD_NAME_PATTERN);
            properties.remove(KEY_FIELD_NAME_PREFILTER);
        }
    }

    


    
    public final FormFieldMaskingExcludeDefinition[] getAllMatchingFormFieldMaskingExcludeDefinitions(final String servletPath, final String requestURI) {
        final SimpleDefinition[] matchingDefinitions = super.getAllMatchingSimpleDefinitions(servletPath, requestURI);
        final FormFieldMaskingExcludeDefinition[] results = new FormFieldMaskingExcludeDefinition[matchingDefinitions.length];
        for (int i=0; i<matchingDefinitions.length; i++) {
            final SimpleDefinition definition = matchingDefinitions[i];
            results[i] = (FormFieldMaskingExcludeDefinition) definition;
        }
        return results;
    }
        
    

}
