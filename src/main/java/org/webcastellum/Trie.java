package org.webcastellum;

import java.io.Serializable;

public final class Trie implements Serializable {
	private static final long serialVersionUID = 1L;


	
	public static final Trie createTrie(String[] words) {
		final IdentifierSequence countingID = new IdentifierSequence();
		final Trie trie = new Trie(countingID);
		for (int i=0; i<words.length; i++) {
            trie.addString(words[i], countingID);
        }
		return trie;
	}

	
	
	
	
	private final Node rootNode;

	
	
	public Trie(final IdentifierSequence countingID) {
		rootNode = Node.createEmptyNode(null, countingID);
	}

	
	public Node getRootNode() {
		return this.rootNode;
	}
	
	
	
	public void addString(final String str, final IdentifierSequence countingID) {
		addString(rootNode.getFirstChild(), str, str.length(), 0, countingID);
	}
	private void addString(final Node ptrFirstChild, final String str, final int strLen, final int i, final IdentifierSequence countingID) {
		Node node = null;
		for (node = ptrFirstChild; !node.isEmpty(); node = node.getBrother()) {
			if (node.getChar() == str.charAt(i)) {
				if (i == strLen - 1) {
					node.mark();
					return;
				} else {
					// recurse
					addString(node.getFirstChild(), str, strLen, i + 1,countingID);
					return;
				}
			}
		}
		node.setChar( str.charAt(i) );
		node.setBrother( Node.createEmptyNode(node.getParent(),countingID) );
		node.setFirstChild( Node.createEmptyNode(node, countingID) );

		if (i == strLen - 1) {
			node.mark();
			return;
		} else {
			addString(node.getFirstChild(), str, strLen, i + 1, countingID);
		}
	}


}
