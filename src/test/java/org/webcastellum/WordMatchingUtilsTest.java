package org.webcastellum;

import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class WordMatchingUtilsTest {
       
    @Test
    public void testMatchesWord() {
        assertTrue(WordMatchingUtils.matchesWord(null, "text", 0));
        
        WordDictionary wordDictionary = new WordDictionary(Collections.emptyList());
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, "text", 0));
        
        wordDictionary = WordDictionary.createInstance("test1,test22");
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, null, 0));
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, "", 0));
        
        //text too short
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, "test", 0));
        
        //brute force check
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, "false", 0));
        assertTrue(WordMatchingUtils.matchesWord(wordDictionary, "test1", 0));
        assertTrue(WordMatchingUtils.matchesWord(wordDictionary, "Test1", 0));
        
        //tree matching
        assertFalse(WordMatchingUtils.matchesWord(wordDictionary, "false", 1));
        assertTrue(WordMatchingUtils.matchesWord(wordDictionary, "test1", 1));
        assertTrue(WordMatchingUtils.matchesWord(wordDictionary, "Test1", 1));
    }
    

    @Test
    public void testDetermineMinimumLength() {
        assertEquals(0, WordMatchingUtils.determineMinimumLength(null));
        assertEquals(0, WordMatchingUtils.determineMinimumLength(new String[0]));
        
        assertEquals(3, WordMatchingUtils.determineMinimumLength(new String[]{
            "one", "two", "three", "four", "five", "six", "seven"}));
    }

    @Test
    public void testDeduplicate() {
        assertNull(WordMatchingUtils.deduplicate(null));
        assertEquals(0, WordMatchingUtils.deduplicate(new String[]{}).length);
        
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.deduplicate(new String[]{"a","b","c"}));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.deduplicate(new String[]{"a","b", "b","c"}));
        assertArrayEquals(new String[]{"A","b","B","c"}, WordMatchingUtils.deduplicate(new String[]{"A","b", "b", "B","c"}));
    }

    @Test
    public void testTrimLowercaseAndDeduplicate() {
        assertNull(WordMatchingUtils.trimLowercaseAndDeduplicate(null));
        assertEquals(0, WordMatchingUtils.trimLowercaseAndDeduplicate(new String[]{}).length);
        
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.trimLowercaseAndDeduplicate(new String[]{"a","b","c"}));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.trimLowercaseAndDeduplicate(new String[]{"a","b", "b","c"}));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.trimLowercaseAndDeduplicate(new String[]{"A","b", "b", "B","c"}));

    }

    @Test
    public void testSplit() {
        assertNull(WordMatchingUtils.split(null));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.split("a b c"));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.split("a,b,c"));
        assertArrayEquals(new String[]{"a","b","c"}, WordMatchingUtils.split("a b,c"));
    }
    
}
