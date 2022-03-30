package org.webcastellum;

import java.util.regex.Pattern;

/**
 * Those form fields that match won't get removed or masked by the following features:
 *    - hidden form field protection
 *    - selectbox protection
 *    - radiobutton protection
 *    - checkbox protection
 */
public final class FormFieldMaskingExcludeDefinition extends SimpleDefinition {
    private static final long serialVersionUID = 1L;

    private WordDictionary formNamePrefilter, fieldNamePrefilter;
    private Pattern formNamePattern, fieldNamePattern;

    public FormFieldMaskingExcludeDefinition(final boolean enabled, final String identification, final String description, final WordDictionary servletPathOrRequestURIPrefilter, final Pattern servletPathOrRequestURIPattern) {
        super(enabled, identification, description, servletPathOrRequestURIPrefilter, servletPathOrRequestURIPattern);
    }

    public WordDictionary getFieldNamePrefilter() {
        return fieldNamePrefilter;
    }

    public void setFieldNamePrefilter(WordDictionary fieldNamePrefilter) {
        this.fieldNamePrefilter = fieldNamePrefilter;
    }

    public WordDictionary getFormNamePrefilter() {
        return formNamePrefilter;
    }

    public void setFormNamePrefilter(WordDictionary formNamePrefilter) {
        this.formNamePrefilter = formNamePrefilter;
    }
    
    
    
    public Pattern getFieldNamePattern() {
        return fieldNamePattern;
    }
    public void setFieldNamePattern(Pattern fieldNamePattern) {
        this.fieldNamePattern = fieldNamePattern;
    }

    public Pattern getFormNamePattern() {
        return formNamePattern;
    }
    public void setFormNamePattern(Pattern formNamePattern) {
        this.formNamePattern = formNamePattern;
    }

    
}
