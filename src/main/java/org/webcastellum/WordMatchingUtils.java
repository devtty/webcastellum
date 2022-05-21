package org.webcastellum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WordMatchingUtils {
	
	
	
	
	public static final boolean matchesWord(final WordDictionary wordDictionary, String text, final int trieMatchingThreshold) {
                if (wordDictionary == null) return true; // required !!
		if (wordDictionary.size() == 0 || text == null || text.length() == 0) return false;
		final String[] words = wordDictionary.getWords();
		final int minLength = wordDictionary.getMinLength();
		if (text.length() < minLength) return false;
                text = text.toLowerCase();
		if (trieMatchingThreshold >= 0 && words.length >= trieMatchingThreshold) {
			// advanced trie-matching check (Aho-Corasick automaton) - useful for checks with a large amount of search words
			final Trie trie = wordDictionary.getTrie();
			final AhoCorasickAutomaton automat = new AhoCorasickAutomaton(trie);
			Node currentState = trie.getRootNode();
			final List/*<Node>*/ visitedStates = new ArrayList();
			final int textLength = text.length();
			//CHAR_LOOP: 
			for (int j=0; j<textLength; j++) {
				while (automat.transition(currentState, text.charAt(j)) == AhoCorasickAutomaton.EMPTY_SET_NODE) {
					currentState = automat.fail(currentState);
				}
				currentState = automat.transition(currentState, text.charAt(j));
				if (!visitedStates.contains(currentState)) {
					if (automat.isMatching(currentState)) {
						return true;
						//break CHAR_LOOP;
					}
					visitedStates.add(currentState);
				}
			}
		} else {
                    // simple brute force check
                    for (String word : words) {
                        if (text.contains(word)) {
                            return true;
                        }
                    }
		}
		return false;
	}
	
	
	
	
	
	
	
	public static int determineMinimumLength(final String[] words) {
		if (words == null || words.length == 0) return 0;
		int minLength = Integer.MAX_VALUE;
            for (String word : words) {
                minLength = Math.min(minLength, word.length());
            }
		return minLength;
	}
	
	public static String[] deduplicate(final String[] patterns) {
		if (patterns == null || patterns.length == 0) return patterns;
		final Set/*<String>*/ searchWords = new HashSet(patterns.length);
            for (String pattern : patterns) {
                searchWords.add(pattern);
            }
		return (String[]) searchWords.toArray(new String[]{});
	}

	public static String[] trimLowercaseAndDeduplicate(final String[] patterns) {
		if (patterns == null || patterns.length == 0) return patterns;
		final Set/*<String>*/ searchWords = new HashSet(patterns.length);
            for (String pattern : patterns) {
                searchWords.add(pattern.trim().toLowerCase());
            }
		return (String[]) searchWords.toArray(new String[]{});
	}
	

	public static String[] split(final String commaOrWhitespaceSeparatedWords) {
            return commaOrWhitespaceSeparatedWords==null ? null : commaOrWhitespaceSeparatedWords.trim().split("\\s+|,");
        }
        
        
	
	private WordMatchingUtils() {}

}
