package org.webcastellum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AhoCorasickAutomaton {

	static final Node EMPTY_SET_NODE = Node.createEmptyNode(null, -3, -2, -1);

	
	private final Trie trie;
	private final Map<Integer, Node> failureCache;
        private final Map<Integer, Node> transitionCache;
	private final List<Integer> retrievalCacheNegatives;
	
	public AhoCorasickAutomaton(final Trie trie) {
            this.failureCache = new HashMap<>();
            this.transitionCache = new HashMap<>();
            this.retrievalCacheNegatives = new ArrayList<>();
            this.trie = trie;
	}

	public Node fail(final Node node) {
		if (node == this.trie.getRootNode()) return node;
		if (node.getParent() == this.trie.getRootNode()) return node.getParent();

		Node result = (Node)this.failureCache.get(node.getID());
		if (result != null) return result;

		if (node == this.trie.getRootNode()) {
			this.failureCache.put(node.getID(), node);
			return node;
		}

		if (node.getParent() == this.trie.getRootNode()) {
			this.failureCache.put(node.getID(), node.getParent());
			return node.getParent();
		}

		final char character = node.getChar();
		Node test = fail(node.getParent());
		while (transition(test, character) == EMPTY_SET_NODE) {
			test = fail(test);
		}

		result = transition(test, character);
		this.failureCache.put(node.getID(), result);
		return result;
	}

	
	
	
	
	public Node transition(final Node node, final char character) {
		final Integer combinedKeyCharacterAndID = new Integer(((node.getID().intValue() * 1024) + ((int) character)));

		final Node cachedResult = (Node)this.transitionCache.get(combinedKeyCharacterAndID);
		if (cachedResult != null) return cachedResult;

		Node test = null;
		for (test = node.getFirstChild(); !test.isEmpty(); test = test.getBrother()) {
			if (test.getChar() == character) {
				this.transitionCache.put(combinedKeyCharacterAndID, test);
				return test;
			}
		}

		if (node == this.trie.getRootNode()) {
			this.transitionCache.put(combinedKeyCharacterAndID, node);
			return node;
		} else {
			this.transitionCache.put(combinedKeyCharacterAndID, EMPTY_SET_NODE);
			return EMPTY_SET_NODE;
		}
	}

	
	
	
	
	
	

	public boolean isMatching(final Node node) {
		if (this.retrievalCacheNegatives.contains(node.getID())) {
			return false;
		}
		
		if (node == this.trie.getRootNode()) {
			this.retrievalCacheNegatives.add(node.getID());
			return false;
		} 

		final Node nodeFail = fail(node);
		if (nodeFail != node && isMatching(nodeFail)) return true;

		if (node.isMarked()) return true;
		
		this.retrievalCacheNegatives.add(node.getID());
		return false;
	}	

	

	
}



