package org.webcastellum;

import java.io.Serializable;

public final class Node implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int EMPTY_CHAR = -1;

	

	

	public static Node createEmptyNode(final Node parent, final int id4Node, final int id4FirstChild, final int id4Brother) {
		Node node = new Node(new Integer(id4Node));
		node.firstChild = new Node(new Integer(id4FirstChild));
		node.brother = new Node(new Integer(id4Brother));
		node.parent = parent;
		node.firstChild.parent = node;
		node.brother.parent = node.parent;
		return node;
	}
	public static Node createEmptyNode(final Node parent, final IdentifierSequence countingID) {
		return createEmptyNode(parent, countingID.nextValue(), countingID.nextValue(), countingID.nextValue());
	}

	
	
	
	
	
	


	private final Integer ID;

	private Node parent;
	private Node brother;
	private Node firstChild;
	
	private boolean marked = false; // TODO wirklich noetig? leafs??
	private int ch = EMPTY_CHAR; // = das aktuelle Zeichen

	
	
	private Node(final Integer ID) {
		this.ID = ID;
	}
	
	public Integer getID() {
		return this.ID;
	}

	
	
	public boolean isEmpty() {
		return (this.ch == EMPTY_CHAR);
	}
	public char getChar() {
		return (char) this.ch;
	}
	public void setChar(final char c) {
		this.ch = c;
	}
	
	
	
	public Node getParent() {
		return this.parent;
	}
	public void setParent(final Node node) {
		this.parent = node;
	}


	public Node getBrother() {
		return this.brother;
	}
	public void setBrother(final Node node) {
		this.brother = node;
	}
	
	
	public Node getFirstChild() {
		return this.firstChild;
	}
	public void setFirstChild(final Node node) {
		this.firstChild = node;
	}
	
	
	public boolean isMarked() {
		return this.marked;
	}
	public void mark() {
		this.marked = true;
	}
	
	
	
	
	
	
	
	
	
	/*// not used
	String word;

	public static Node getNodeByID(final int id) {
		return instances.get(id);
	}

	public String toString() {
		return this.toString(0);
	}

	public String toString(final int i) {
		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < i; k++) {
			sb.append(' ');
		}
		sb.append(this.ch);
		if (this.word != null) {
			sb.append("   ->|" + this.word + "|");
		}
		sb.append("\n");
		Node p = null;
		for (p = this.firstChild; !p.isEmpty(); p = p.brother) {
			sb.append(p.toString(i + 1));
		}
		return sb.toString();
	}
	*/
}
