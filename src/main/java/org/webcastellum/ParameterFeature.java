package org.webcastellum;

public final class ParameterFeature {
    
    private final String name;
    
    private boolean havingDigits;
    private boolean havingLetters;
    private boolean havingPunctation;
    private boolean havingQuotes;
    private boolean havingSingleQuotes;
    private boolean havingBraces;
    private boolean havingMail;
    private boolean havingTags;
    private boolean havingAmpersands;
    private boolean havingSpecialChars;
    private boolean havingWhitespace;
    private boolean havingMathSymbols;
    private boolean havingMultipleValues;
    private int maximumSize;
    
    
    public ParameterFeature(final String name) {
        if (name == null) throw new NullPointerException("name must not be null");
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }
    
    public boolean isHavingSingleQuotes() {
        return this.havingSingleQuotes;
    }

    public void setHavingSingleQuotes(boolean havingSingleQuotes) {
        this.havingSingleQuotes = havingSingleQuotes;
    }

    public boolean isHavingMail() {
        return this.havingMail;
    }

    public void setHavingMail(boolean havingMail) {
        this.havingMail = havingMail;
    }

    public boolean isHavingAmpersands() {
        return this.havingAmpersands;
    }

    public void setHavingAmpersands(boolean havingAmpersands) {
        this.havingAmpersands = havingAmpersands;
    }

    public boolean isHavingBraces() {
        return this.havingBraces;
    }

    public void setHavingBraces(boolean havingBraces) {
        this.havingBraces = havingBraces;
    }

    public boolean isHavingDigits() {
        return this.havingDigits;
    }

    public void setHavingDigits(boolean havingDigits) {
        this.havingDigits = havingDigits;
    }

    public boolean isHavingLetters() {
        return this.havingLetters;
    }

    public void setHavingLetters(boolean havingLetters) {
        this.havingLetters = havingLetters;
    }

    public boolean isHavingMathSymbols() {
        return this.havingMathSymbols;
    }

    public void setHavingMathSymbols(boolean havingMathSymbols) {
        this.havingMathSymbols = havingMathSymbols;
    }

    public boolean isHavingPunctation() {
        return havingPunctation;
    }

    public void setHavingPunctation(boolean havingPunctation) {
        this.havingPunctation = havingPunctation;
    }

    public boolean isHavingQuotes() {
        return havingQuotes;
    }

    public void setHavingQuotes(boolean havingQuotes) {
        this.havingQuotes = havingQuotes;
    }

    public boolean isHavingTags() {
        return havingTags;
    }

    public void setHavingTags(boolean havingTags) {
        this.havingTags = havingTags;
    }

    public boolean isHavingSpecialChars() {
        return havingSpecialChars;
    }

    public void setHavingSpecialChars(boolean havingSpecialChars) {
        this.havingSpecialChars = havingSpecialChars;
    }

    public boolean isHavingWhitespace() {
        return havingWhitespace;
    }

    public void setHavingWhitespace(boolean havingWhitespace) {
        this.havingWhitespace = havingWhitespace;
    }

    public boolean isHavingMultipleValues() {
        return havingMultipleValues;
    }

    public void setHavingMultipleValues(boolean havingMultipleValues) {
        this.havingMultipleValues = havingMultipleValues;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }
    
    @Override
    public String toString() {
        return this.name+"("+this.maximumSize+")"+(this.havingMultipleValues?"*":"1");
    }

}
