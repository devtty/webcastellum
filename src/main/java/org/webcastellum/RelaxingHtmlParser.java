package org.webcastellum;

import java.io.IOException;
import java.util.regex.Pattern;




public interface RelaxingHtmlParser {

    

    
    
    
    
    // TODO: vor den haeufig aufgerufenen reg-exps hier evtl. eine word-list checken (aho ?) wie der @prefilter in rule-files
    
    
    // es werden (zumindest beim IE Browser) URL-Parameter der Formular-Action verschluckt (ignoriert), wenn das Formular ein GET-Formular ist... Daher dieses Verschlucken hier auch abbilden...
    public static final boolean IGNORE_URL_PARAMETERS_ON_FORM_ACTION_WITH_METHOD_GET = true;

    public static final Pattern PATTERN_FORM_METHOD_POST = Pattern.compile("(?i)(?s)method\\s*=\\s*[\"']?POST[\"']?");
    
    public static final Pattern PATTERN_REQUIRED_INPUT_FORM_FIELD_EXCLUDING_HIDDEN_FIELDS = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?(text|password)[\"']?"); // TODO: hier auch weitere ? 
    public static final Pattern PATTERN_REQUIRED_INPUT_FORM_FIELD = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?(text|password|hidden)[\"']?"); // TODO: hier auch weitere ? 

    public static final Pattern PATTERN_HIDDEN_FORM_FIELD = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?hidden[\"']?");

    //public static final Pattern PATTERN_SUBMIT_BUTTON = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?submit[\"']?");

    public static final Pattern PATTERN_CHECKBOX = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?checkbox[\"']?");
    public static final Pattern PATTERN_RADIOBUTTON = Pattern.compile("(?i)(?s)type\\s*=\\s*[\"']?radio[\"']?");
    
    
    
    
    public static final boolean USE_DIRECT_ARRAY_LOOKUPS_INSTEAD_OF_STARTS_WITH = true;
    
    
    public static final char SLASH = '/';
    public static final char TAG_START = '<';
    public static final char TAG_END = '>';
    // NOTE: a comment is treated as a single tag (including the commented content !)
    public static final String COMMENT_START = "<!--";
    public static final String COMMENT_END = "-->";
    public static final int[] COMMENT_END__ARRAY = new int[]{'-','-','>'};

    
    
    void handleTag(final String tag) throws IOException;
    void handleTagClose(final String tag) throws IOException;
    void handlePseudoTagRestart(final char[] stuff) throws IOException;
    void handleText(final int character) throws IOException;
    void handleText(final String text) throws IOException;

    void writeToUnderlyingSink(final String string) throws IOException;
    void writeToUnderlyingSink(final char[] chars, final int start, final int count) throws IOException;
    void writeToUnderlyingSink(final int i) throws IOException;
    
}
