package org.webcastellum;

public final class IdentifierSequence {

	private int counter = 0;
	
	public int nextValue() {
		return counter++;
	}
	
}
