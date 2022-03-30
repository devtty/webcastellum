package org.webcastellum;

import java.io.CharArrayWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class AbstractRelaxingHtmlParserWriter extends FilterWriter implements RelaxingHtmlParser {

    
    private final boolean useTunedBlockParser;
    
    private final int[] LAST_FEW = new int[4]; // 4 since <!-- is the longest value to check and that has 4 standard ascii chars/bytes
    private final CharArrayWriter currentTag = new CharArrayWriter();    
    
    private boolean isWithinTag = false;
    private boolean isWithinComment = false;
    
    
    
    
    
    protected AbstractRelaxingHtmlParserWriter(final Writer delegate, final boolean useTunedBlockParser) {
        super(delegate);
        this.useTunedBlockParser = useTunedBlockParser;
    }
    
    
    
    

    public final void writeToUnderlyingSink(final String string) throws IOException {
        out.write(string);
    }
    public final void writeToUnderlyingSink(final char[] chars, final int start, final int count) throws IOException {
        out.write(chars, start, count);
    }
    public final void writeToUnderlyingSink(final int character) throws IOException {
        out.write(character);
    }
    
    


    // can be overwritten:
    public void handleTag(final String tag) throws IOException {
        writeToUnderlyingSink(tag);
    }
    // can be overwritten:
    public void handleTagClose(final String tag) throws IOException {
        writeToUnderlyingSink(tag);
    }
    // can be overwritten:
    public void handlePseudoTagRestart(final char[] stuff) throws IOException {
        writeToUnderlyingSink(stuff, 0, stuff.length);
    }
    // can be overwritten:
    public void handleText(final int character) throws IOException {
        writeToUnderlyingSink(character);
    }
    // can be overwritten:
    public void handleText(final String text) throws IOException {
        writeToUnderlyingSink(text);
    }
        
    

    
    
    
    
    
    //@Override
//    public final Writer append(final char c) throws IOException {
//        write(c);
//        return this;
//    }
    
    
    /**
     * Writes a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    //1.5@Override
    public final void write(final int original) throws IOException {
        // System.out.println("  zustand:"+((char)original)+":"+this.isWithinComment+":"+this.isWithinTag+":"+this.currentTag);        
        boolean tagEnd = false;
        boolean writeChar = false;
        if (!this.isWithinComment) {
            if (original == TAG_START) {
                // write content out filled up to here, as it is a tag-restart like "<x<xxx>"
                if (this.currentTag.size() > 0) {
                    handlePseudoTagRestart( this.currentTag.toCharArray() );
                }
                // reset tag content + "last few" array aside
                this.currentTag.reset();
                LAST_FEW[0]=0; LAST_FEW[1]=0; LAST_FEW[2]=0; LAST_FEW[3]=0;
                // set within tag flag
                this.isWithinTag = true;
            } else if (original == TAG_END) {
                // remove within tag flag - delayed
                tagEnd = true;
            }
        }
        if (this.isWithinTag) {
            // track last few chars/bytes
            LAST_FEW[0]=LAST_FEW[1]; LAST_FEW[1]=LAST_FEW[2]; LAST_FEW[2]=LAST_FEW[3]; // direct assignment is faster than System.arrayCopy for under about 50 chars/bytes (and LAST_FEW_LENGTH is even just under 10 chars/bytes)
            LAST_FEW[3] = original;
            // continue
            this.currentTag.write(original);
            if (this.isWithinComment) {
                if (original == TAG_END) {
                    // chance to have an end-tag for the colsing of the comment (at least that should be possible)
    //OLD                if ( this.currentTag.toString().trim().endsWith(COMMENT_END) ) {
                    if ( LAST_FEW[1]=='-' && LAST_FEW[2]=='-' && LAST_FEW[3]=='>' ) {
                        this.isWithinComment = false;
                        this.isWithinTag = false;
                        // remove within tag flag - delayed
                        tagEnd = true;
                    }
                }
//OLD            } else if ( this.currentTag.toString().trim().equals(COMMENT_START) ) {
            } else if ( LAST_FEW[1]=='!' && LAST_FEW[2]=='-' && LAST_FEW[3]=='-' && LAST_FEW[0]=='<' /*the '<'-check here at index 0 put at the end since this condition-part is mostly true so that we better fail faster by having the index 1 thing checked at first*/) {
                this.isWithinComment = true;
            }
        } else writeChar = true;
        if (tagEnd) {
            // fetch tag content
            final String tag = this.currentTag.toString().trim();
            final boolean closingTag = tag.length()>1 && tag.charAt(1) == SLASH;
            if (closingTag) handleTagClose(tag);
            else if (tag.length() > 0) handleTag(tag);
            this.isWithinTag = false;
            // reset tag content + "last few" array aside
            this.currentTag.reset();
            LAST_FEW[0]=0; LAST_FEW[1]=0; LAST_FEW[2]=0; LAST_FEW[3]=0;
        }
        if (writeChar) handleText(original);
    }
    
    

    
    
    private final void handleNonTagRelevantContentChunk(final char[] charsWithoutAnyTagRelevantChars, final int startPosInclusive, final int endPosExclusive) throws IOException {
        final int count = endPosExclusive - startPosInclusive;
        if (this.isWithinTag) {
            // track last few chars/bytes
            if (count >= 4) {
                LAST_FEW[0] = charsWithoutAnyTagRelevantChars[endPosExclusive-4]; 
                LAST_FEW[1] = charsWithoutAnyTagRelevantChars[endPosExclusive-3]; 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 3) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = charsWithoutAnyTagRelevantChars[endPosExclusive-3]; 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 2) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 1) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = LAST_FEW[3];
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars[endPosExclusive-1];
            }
            // continue
            this.currentTag.write(charsWithoutAnyTagRelevantChars, startPosInclusive, count);
        } else handleText(new String(charsWithoutAnyTagRelevantChars, startPosInclusive, count));
    }
    private final void handleNonTagRelevantContentChunk(final String charsWithoutAnyTagRelevantChars, final int startPosInclusive, final int endPosExclusive) throws IOException {
        final int count = endPosExclusive - startPosInclusive;
        if (this.isWithinTag) {
            // track last few chars/bytes
            if (count >= 4) {
                LAST_FEW[0] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-4); 
                LAST_FEW[1] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-3); 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-2);
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-1);
            } else if (count == 3) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-3); 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-2);
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-1);
            } else if (count == 2) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-2);
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-1);
            } else if (count == 1) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = LAST_FEW[3];
                LAST_FEW[3] = charsWithoutAnyTagRelevantChars.charAt(endPosExclusive-1);
            }
            // continue
            this.currentTag.write(charsWithoutAnyTagRelevantChars, startPosInclusive, count);
        } else handleText(charsWithoutAnyTagRelevantChars.substring(startPosInclusive, endPosExclusive));
    }
            
            
    
    
    /**
     * Writes a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    //1.5@Override
    public final void write(char[] cbuf, int off, int len) throws IOException {
        final int end = off + len;
         if (this.useTunedBlockParser) {
             int pos = off;
             for (int i=off; i<end; i++) {
                 if (cbuf[i] == '<' || cbuf[i] == '>' || cbuf[i] == '-') {
                    // write non-tag-relevant chunk
                     if (i>pos) handleNonTagRelevantContentChunk(cbuf, pos, i);
                    // write the tag-relevant char (to have all tag-relevant checks happen)
                     write(cbuf[i]);
                    // position "pos" at the next char
                     pos = i+1;
                 }
             }
             if (pos < end) {
                // write non-tag-relevant last chunk (in case there is a last chunk)
                 handleNonTagRelevantContentChunk(cbuf, pos, end);
             }
         } else { // old slower char by char variant
            for (int i=off; i<end; i++) {
                write(cbuf[i]);
            }
         }
    }
    

    /**
     * Writes a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    //1.5@Override
    public final void write(String str, int off, int len) throws IOException {
        final int end = off + len;
         if (this.useTunedBlockParser) {
             int pos = off;
             for (int i=off; i<end; i++) {
                 if (str.charAt(i) == '<' || str.charAt(i) == '>' || str.charAt(i) == '-') {
                    // write non-tag-relevant chunk
                     if (i>pos) handleNonTagRelevantContentChunk(str, pos, i);
                    // write the tag-relevant char (to have all tag-relevant checks happen)
                     write(str.charAt(i));
                    // position "pos" at the next char
                     pos = i+1;
                 }
             }
             if (pos < end) {
                // write non-tag-relevant last chunk (in case there is a last chunk)
                 handleNonTagRelevantContentChunk(str, pos, end);
             }
         } else { // old slower char by char variant
            for (int i=off; i<end; i++) {
                write(str.charAt(i));
            }
         }
    }    
    
    
    
    
    
}
    
