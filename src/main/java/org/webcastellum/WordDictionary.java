package org.webcastellum;

import java.io.Serializable;
import java.util.Collection;

public final class WordDictionary implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String[] words;
	private final int minLength;
	private final Trie trie;

	
	
        public static final WordDictionary createInstance(final String whitespaceOrCommaSeparatedWords) {
            if (whitespaceOrCommaSeparatedWords == null) return null;
            return new WordDictionary(whitespaceOrCommaSeparatedWords);
        }
        
        
	public WordDictionary(final String[] wordsOriginalUntrimmed) {
            this.words = WordMatchingUtils.trimLowercaseAndDeduplicate(wordsOriginalUntrimmed);
            this.minLength = WordMatchingUtils.determineMinimumLength(this.words);
            this.trie = Trie.createTrie(this.words);
	}
	public WordDictionary(final Collection/*<String>*/ wordsOriginalUntrimmed) {
            this( (String[])wordsOriginalUntrimmed.toArray(new String[0]) );
	}
	public WordDictionary(final String whitespaceOrCommaSeparatedWords) {
            this( WordMatchingUtils.split(whitespaceOrCommaSeparatedWords) );
        }

        
	public int getMinLength() {
		return minLength;
	}


	public String[] getWords() {
		return words;
	}


	public Trie getTrie() {
		return trie;
	}

	
	public int size() {
		return words.length;
	}
        


}
