package org.webcastellum;

public final class ParameterFeature {
    
    private final String name;
    
    private boolean isHavingDigits, isHavingLetters, isHavingPunctation, isHavingQuotes, isHavingSingleQuotes, isHavingBraces, isHavingMail,
            isHavingTags, isHavingAmpersands, isHavingSpecialChars, isHavingWhitespace, isHavingMathSymbols, isHavingMultipleValues;
    private int maximumSize;
    
    
    public ParameterFeature(final String name) {
        if (name == null) throw new NullPointerException("name must not be null");
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }

    
    
    
    public boolean isHavingSingleQuotes() {
        return isHavingSingleQuotes;
    }

    public void setHavingSingleQuotes(boolean isHavingSingleQuotes) {
        this.isHavingSingleQuotes = isHavingSingleQuotes;
    }

    
    public boolean isHavingMail() {
        return isHavingMail;
    }

    public void setHavingMail(boolean isHavingMail) {
        this.isHavingMail = isHavingMail;
    }


    public boolean isHavingAmpersands() {
        return isHavingAmpersands;
    }

    public void setHavingAmpersands(boolean isHavingAmpersands) {
        this.isHavingAmpersands = isHavingAmpersands;
    }

    public boolean isHavingBraces() {
        return isHavingBraces;
    }

    public void setHavingBraces(boolean isHavingBraces) {
        this.isHavingBraces = isHavingBraces;
    }

    public boolean isHavingDigits() {
        return isHavingDigits;
    }

    public void setHavingDigits(boolean isHavingDigits) {
        this.isHavingDigits = isHavingDigits;
    }

    public boolean isHavingLetters() {
        return isHavingLetters;
    }

    public void setHavingLetters(boolean isHavingLetters) {
        this.isHavingLetters = isHavingLetters;
    }

    public boolean isHavingMathSymbols() {
        return isHavingMathSymbols;
    }

    public void setHavingMathSymbols(boolean isHavingMathSymbols) {
        this.isHavingMathSymbols = isHavingMathSymbols;
    }

    public boolean isHavingMultipleValues() {
        return isHavingMultipleValues;
    }

    public void setHavingMultipleValues(boolean isHavingMultipleValues) {
        this.isHavingMultipleValues = isHavingMultipleValues;
    }

    public boolean isHavingPunctation() {
        return isHavingPunctation;
    }

    public void setHavingPunctation(boolean isHavingPunctation) {
        this.isHavingPunctation = isHavingPunctation;
    }

    public boolean isHavingQuotes() {
        return isHavingQuotes;
    }

    public void setHavingQuotes(boolean isHavingQuotes) {
        this.isHavingQuotes = isHavingQuotes;
    }

    public boolean isHavingSpecialChars() {
        return isHavingSpecialChars;
    }

    public void setHavingSpecialChars(boolean isHavingSpecialChars) {
        this.isHavingSpecialChars = isHavingSpecialChars;
    }

    public boolean isHavingTags() {
        return isHavingTags;
    }

    public void setHavingTags(boolean isHavingTags) {
        this.isHavingTags = isHavingTags;
    }

    public boolean isHavingWhitespace() {
        return isHavingWhitespace;
    }

    public void setHavingWhitespace(boolean isHavingWhitespace) {
        this.isHavingWhitespace = isHavingWhitespace;
    }

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }
    

    
    
    //1.5@Override
    public String toString() {
        return this.name+"("+this.maximumSize+")"+(this.isHavingMultipleValues?"*":"1");
    }
    
    
}
