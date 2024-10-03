package org.webcastellum;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractRelaxingHtmlParserStream extends FilterOutputStream implements RelaxingHtmlParser {

    
    private final boolean useTunedBlockParser;
    
    private final int[] LAST_FEW = new int[4]; // 4 since <!-- is the longest value to check and that has 4 standard ascii chars/bytes
    private final ByteArrayOutputStream currentTag = new ByteArrayOutputStream();
    protected final String encoding;
    
    private boolean isWithinTag = false;
    private boolean isWithinComment = false;
    
    
    
    
    
    protected AbstractRelaxingHtmlParserStream(final OutputStream delegate, final String encoding, final boolean useTunedBlockParser) {
        super(delegate);
        if (encoding == null) throw new NullPointerException("encoding must not be null");
        this.encoding = encoding;
        this.useTunedBlockParser = useTunedBlockParser;
    }
    
    
    
    

    public final void writeToUnderlyingSink(final String string) throws IOException {
        out.write( string.getBytes(this.encoding) );
    }
    public final void writeToUnderlyingSink(final char[] chars, final int start, final int count) throws IOException {
        final String value = new String(chars, start, count);
        writeToUnderlyingSink(value);
    }
    public final void writeToUnderlyingSink(final int b) throws IOException {
        out.write(b);
        // TODO oder so (langsamer?) writeToUnderlyingSink(""+character); lieber nicht
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
    public void handleText(final int b) throws IOException {
        writeToUnderlyingSink(b);
    }
    // can be overwritten:
    public void handleText(final String text) throws IOException {
        writeToUnderlyingSink(text);
    }
        
    

    
    
    
    
    
    
    
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
                    handlePseudoTagRestart( this.currentTag.toString(this.encoding).toCharArray() );
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
    // OLD                if ( this.currentTag.toString(this.encoding).trim().endsWith(COMMENT_END) ) {
                    if ( LAST_FEW[1]=='-' && LAST_FEW[2]=='-' && LAST_FEW[3]=='>' ) {
                        this.isWithinComment = false;
                        this.isWithinTag = false;
                        // remove within tag flag - delayed
                        tagEnd = true;
                    }
                }
//OLD            } else if ( this.currentTag.toString(this.encoding).trim().equals(COMMENT_START) ) {
            } else if ( LAST_FEW[1]=='!' && LAST_FEW[2]=='-' && LAST_FEW[3]=='-' && LAST_FEW[0]=='<' /*the '<'-check here at index 0 put at the end since this condition-part is mostly true so that we better fail faster by having the index 1 thing checked at first*/) {
                this.isWithinComment = true;
            }
        } else writeChar = true;
        if (tagEnd) {
            // fetch tag content
            final String tag = this.currentTag.toString(this.encoding).trim();
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
    
    

    
    private final void handleNonTagRelevantContentChunk(final byte[] bytesWithoutAnyTagRelevantChars, final int startPosInclusive, final int endPosExclusive) throws IOException {
        final int count = endPosExclusive - startPosInclusive;
        if (this.isWithinTag) {
            // track last few chars/bytes
            if (count >= 4) {
                LAST_FEW[0] = bytesWithoutAnyTagRelevantChars[endPosExclusive-4]; 
                LAST_FEW[1] = bytesWithoutAnyTagRelevantChars[endPosExclusive-3]; 
                LAST_FEW[2] = bytesWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = bytesWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 3) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = bytesWithoutAnyTagRelevantChars[endPosExclusive-3]; 
                LAST_FEW[2] = bytesWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = bytesWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 2) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = bytesWithoutAnyTagRelevantChars[endPosExclusive-2];
                LAST_FEW[3] = bytesWithoutAnyTagRelevantChars[endPosExclusive-1];
            } else if (count == 1) {
                LAST_FEW[0] = LAST_FEW[1]; 
                LAST_FEW[1] = LAST_FEW[2]; 
                LAST_FEW[2] = LAST_FEW[3];
                LAST_FEW[3] = bytesWithoutAnyTagRelevantChars[endPosExclusive-1];
            }
            // continue
            this.currentTag.write(bytesWithoutAnyTagRelevantChars, startPosInclusive, count);
        } else handleText(new String(bytesWithoutAnyTagRelevantChars, startPosInclusive, count, encoding));
    }
    
    
    
    //1.5@Override
    public final void write(byte[] bbuf, int off, int len) throws IOException {
        final int end = off + len;
         if (this.useTunedBlockParser) {
             int pos = off;
             for (int i=off; i<end; i++) {
                 if (bbuf[i] == '<' || bbuf[i] == '>' || bbuf[i] == '-') {
                    // write non-tag-relevant chunk
                     if (i>pos) handleNonTagRelevantContentChunk(bbuf, pos, i);
                    // write the tag-relevant char (to have all tag-relevant checks happen)
                     write(bbuf[i]);
                    // position "pos" at the next char
                     pos = i+1;
                 }
             }
             if (pos < end) {
                // write non-tag-relevant last chunk (in case there is a last chunk)
                 handleNonTagRelevantContentChunk(bbuf, pos, end);
             }
         } else { // old slower byte by byte variant
            for (int i=off; i<end; i++) {
                write(bbuf[i]);
            }
         }
    }

    
    //1.5@Override
    public final void write(byte[] bbuf) throws IOException {
        write(bbuf, 0, bbuf.length);
    }

    
    
    

    
    
    
    
    
}
    
