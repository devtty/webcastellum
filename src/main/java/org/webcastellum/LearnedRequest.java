package org.webcastellum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



    
public final class LearnedRequest {

    // TODO: hier noch Umlaute dazu und auch Waehrungssymbole (Dollar, Euro, etc.)
    
    // isHavingDigits
    private static final String CHECK_DIGITS = "0-9";
    private static final Pattern PATTERN_DIGITS = Pattern.compile("["+CHECK_DIGITS+"]");
    // isHavingLetters
    private static final String CHECK_LETTERS = "A-Za-z";
    private static final Pattern PATTERN_LETTERS = Pattern.compile("["+CHECK_LETTERS+"]");
    // isHavingPunctation
    private static final String CHECK_PUNCTATION = "/!\\?\\.,:;";
    private static final Pattern PATTERN_PUNCTATION = Pattern.compile("["+CHECK_PUNCTATION+"]");
    // isHavingQuotes
    private static final String CHECK_QUOTES = "\"`´";
    private static final Pattern PATTERN_QUOTES = Pattern.compile("["+CHECK_QUOTES+"]");
    // isHavingSigleQuotes
    private static final String CHECK_SINGLE_QUOTES = "'";
    private static final Pattern PATTERN_SINGLE_QUOTES = Pattern.compile("["+CHECK_SINGLE_QUOTES+"]");
    // isHavingBraces
    private static final String CHECK_BRACES = "\\(\\)\\{\\}\\[\\]";
    private static final Pattern PATTERN_BRACES = Pattern.compile("["+CHECK_BRACES+"]");
    // isHavingTags
    private static final String CHECK_TAGS = "<>";
    private static final Pattern PATTERN_TAGS = Pattern.compile("["+CHECK_TAGS+"]");
    // isHavingAmpersands
    private static final String CHECK_AMPERSANDS = "&";
    private static final Pattern PATTERN_AMPERSANDS = Pattern.compile("["+CHECK_AMPERSANDS+"]");
    // isHavingMathSymbols
    private static final String CHECK_MATH = "%=\\+\\-\\*\\\\";
    private static final Pattern PATTERN_MATH = Pattern.compile("["+CHECK_MATH+"]");
    // isHavingMail
    private static final String CHECK_MAIL = "@_";
    private static final Pattern PATTERN_MAIL = Pattern.compile("["+CHECK_MAIL+"]");
    // isHavingWhitespace
    private static final String CHECK_WHITESPACE = "\\s";
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("["+CHECK_WHITESPACE+"]");
    // isHavingSpecialChars
    private static final String CHECK_SPECIAL = "#\\$°^|~";
    private static final Pattern PATTERN_SPECIAL = Pattern.compile("["+CHECK_SPECIAL+"]");

        
    private final String servletPath, method, name;
    private final Map/*<String,ParameterFeature>*/ parameterFeatures = new HashMap();

    public LearnedRequest(final String servletPath, final String method) {
        if (servletPath == null) throw new NullPointerException("servletPath must not be null");
        if (method == null) throw new NullPointerException("method must not be null");
        // mimeType is nullable
        this.servletPath = servletPath;
        this.method = method;
        this.name = createName(this.servletPath, this.method);
    }

    public String getServletPath() {
        return this.servletPath;
    }
    public Pattern createServletPathPattern() {
        return Pattern.compile("\\A\\Q"+this.servletPath+"\\E\\z");
    }

    
    
    public String getMethod() {
        return this.method;
    }
    public Pattern createMethodPattern() {
        return Pattern.compile("\\A\\Q"+this.method+"\\E\\z");
    }
    
    


    
    public ParameterFeature getParameterFeature(final String parameterName) {
        return (ParameterFeature) this.parameterFeatures.get(parameterName);
    }
    
    public void removeParameterFeature(final String parameterName) {
        this.parameterFeatures.remove(parameterName);
    }
    
    
    public void addParameterOccurence(final String name, final List/*<String>*/ values) {
        if (name == null || values == null) return;
        ParameterFeature feature = (ParameterFeature) this.parameterFeatures.get(name);
        if (feature == null) {
            feature = new ParameterFeature(name);
            this.parameterFeatures.put(name, feature);
        }
        // counts
        if (values.size() > 1) feature.setHavingMultipleValues(true);
        // for each value:
        Matcher matcherDigits=null, matcherLetters=null, matcherPunctation=null, matcherQuotes=null, matcherSingleQuotes=null,
                matcherBraces=null, matcherTags=null, matcherAmpersands=null, matcherMath=null, matcherMail=null, 
                matcherWhitespace=null, matcherSpecial=null;
        for (final Iterator iter = values.iterator(); iter.hasNext();) {
            String value = (String) iter.next();
            if (value == null) value = "";
            // size stuff
            final int length = value.length();
            final int maxSizeAlreadyDefined = feature.getMaximumSize();
            if (length > maxSizeAlreadyDefined) {
                feature.setMaximumSize( length>10?length*2:20 );
            }
            // isHavingDigits
            if (!feature.isHavingDigits()) {
                if (matcherDigits == null) matcherDigits = PATTERN_DIGITS.matcher(value);
                else matcherDigits.reset(value);
                if(matcherDigits.find()) feature.setHavingDigits(true);
            }                
            // isHavingLetters
            if (!feature.isHavingLetters()) {
                if (matcherLetters == null) matcherLetters = PATTERN_LETTERS.matcher(value);
                else matcherLetters.reset(value);
                if(matcherLetters.find()) feature.setHavingLetters(true);
            }                
            // isHavingPunctation
            if (!feature.isHavingPunctation()) {
                if (matcherPunctation == null) matcherPunctation = PATTERN_PUNCTATION.matcher(value);
                else matcherPunctation.reset(value);
                if(matcherPunctation.find()) feature.setHavingPunctation(true);
            }                
            // isHavingQuotes
            if (!feature.isHavingQuotes()) {
                if (matcherQuotes == null) matcherQuotes = PATTERN_QUOTES.matcher(value);
                else matcherQuotes.reset(value);
                if(matcherQuotes.find()) feature.setHavingQuotes(true);
            }                
            // isHavingSingleQuotes
            if (!feature.isHavingSingleQuotes()) {
                if (matcherSingleQuotes == null) matcherSingleQuotes = PATTERN_SINGLE_QUOTES.matcher(value);
                else matcherSingleQuotes.reset(value);
                if(matcherSingleQuotes.find()) feature.setHavingSingleQuotes(true);
            }                
            // isHavingBraces
            if (!feature.isHavingBraces()) {
                if (matcherBraces == null) matcherBraces = PATTERN_BRACES.matcher(value);
                else matcherBraces.reset(value);
                if(matcherBraces.find()) feature.setHavingBraces(true);
            }                
            // isHavingTags
            if (!feature.isHavingTags()) {
                if (matcherTags == null) matcherTags = PATTERN_TAGS.matcher(value);
                else matcherTags.reset(value);
                if(matcherTags.find()) feature.setHavingTags(true);
            }                
            // isHavingAmpersands
            if (!feature.isHavingAmpersands()) {
                if (matcherAmpersands == null) matcherAmpersands = PATTERN_AMPERSANDS.matcher(value);
                else matcherAmpersands.reset(value);
                if(matcherAmpersands.find()) feature.setHavingAmpersands(true);
            }                
            // isHavingMathSymbols
            if (!feature.isHavingMathSymbols()) {
                if (matcherMath == null) matcherMath = PATTERN_MATH.matcher(value);
                else matcherMath.reset(value);
                if(matcherMath.find()) feature.setHavingMathSymbols(true);
            }                
            // isHavingMail
            if (!feature.isHavingMail()) {
                if (matcherMail == null) matcherMail = PATTERN_MAIL.matcher(value);
                else matcherMail.reset(value);
                if(matcherMail.find()) feature.setHavingMail(true);
            }                
            // isHavingWhitespace
            if (!feature.isHavingWhitespace()) {
                if (matcherWhitespace == null) matcherWhitespace = PATTERN_WHITESPACE.matcher(value);
                else matcherWhitespace.reset(value);
                if(matcherWhitespace.find()) feature.setHavingWhitespace(true);
            }                
            // isHavingSpecialChars
            if (!feature.isHavingSpecialChars()) {
                if (matcherSpecial == null) matcherSpecial = PATTERN_SPECIAL.matcher(value);
                else matcherSpecial.reset(value);
                if(matcherSpecial.find()) feature.setHavingSpecialChars(true);
            }                
        }
    }
    
    public static final String createRegularExpressionValue(final ParameterFeature feature) {
        final StringBuilder result = new StringBuilder("\\A[");
        boolean hasPattern = false;
        
        // isHavingDigits
        if (feature.isHavingDigits()) { result.append(CHECK_DIGITS); hasPattern = true; }
        // isHavingLetters
        if (feature.isHavingLetters()) { result.append(CHECK_LETTERS); hasPattern = true; }
        // isHavingPunctation
        if (feature.isHavingPunctation()) { result.append(CHECK_PUNCTATION); hasPattern = true; }
        // isHavingQuotes
        if (feature.isHavingQuotes()) { result.append(CHECK_QUOTES); hasPattern = true; }
        // isHavingSingleQuotes
        if (feature.isHavingSingleQuotes()) { result.append(CHECK_SINGLE_QUOTES); hasPattern = true; }
        // isHavingBraces
        if (feature.isHavingBraces()) { result.append(CHECK_BRACES); hasPattern = true; }
        // isHavingTags
        if (feature.isHavingTags()) { result.append(CHECK_TAGS); hasPattern = true; }
        // isHavingAmpersands
        if (feature.isHavingAmpersands()) { result.append(CHECK_AMPERSANDS); hasPattern = true; }
        // isHavingMathSymbols
        if (feature.isHavingMathSymbols()) { result.append(CHECK_MATH); hasPattern = true; }
        // isHavingMail
        if (feature.isHavingMail()) { result.append(CHECK_MAIL); hasPattern = true; }
        // isHavingWhitespace
        if (feature.isHavingWhitespace()) { result.append(CHECK_WHITESPACE); hasPattern = true; }
        // isHavingSpecialChars
        if (feature.isHavingSpecialChars()) { result.append(CHECK_SPECIAL); hasPattern = true; }

        if (!hasPattern) return "\\A\\z"; // meaning empty value
        
        result.append("]{0,").append(feature.getMaximumSize()).append("}\\z");
        return result.toString();
    }
    

    //1.5@Override
    public String toString() {
        return this.name;
        /* old
        final StringBuilder result = new StringBuilder(this.method);
        result.append(": ").append(this.servletPath).append(": <");
        for (final Iterator iter = this.parameterFeatures.values().iterator(); iter.hasNext();) {
            final ParameterFeature feature = (ParameterFeature) iter.next();
            result.append(feature.getParameterName()).append("{ ");
            result.append(createRegularExpressionValue(feature)).append(" }");
            result.append(feature.isHavingMultipleValues()?"*":"1");
            if (iter.hasNext()) result.append(", ");
        }
        result.append(">");
        return result.toString();
     */
    }

    
    
    private static String createName(final String servletPath, final String method) {
        final StringBuilder name = new StringBuilder(method);
        name.append("_");
        name.append( makeFilenameSafe(servletPath) );
        return name.toString();
    }
    private static String makeFilenameSafe(String value) {
        value = value.trim().toUpperCase();
        if (value.startsWith("/")) value = value.substring(1);
        final StringBuilder safe = new StringBuilder(value.length());
        for (int i=0; i<value.length(); i++) {
            final char c = value.charAt(i);
            if ( c=='A'||c=='B'||c=='C'||c=='D'||c=='E'||c=='F'||c=='G'||c=='H'||c=='I'||c=='J'||c=='K'||c=='L'||c=='M'||c=='N'||c=='O'||c=='P'||c=='Q'||c=='R'||c=='S'||c=='T'||c=='U'||c=='V'||c=='W'||c=='X'||c=='Y'||c=='Z'||c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9' ) safe.append(c); else safe.append("_");
        }
        return safe.toString();
    }
    
}
    
    
    
