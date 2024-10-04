package org.webcastellum;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Permutation implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean sealed = false;
    // "Set" to make it unique
    
    // "Set" to make it unique
    private Set<String> standardPermutations = new HashSet();
    private Set<String> nonStandardPermutations = new HashSet();
    
    public Permutation() {
    }
    
    public void addStandardPermutation(final String permutation) {
        if (!this.nonStandardPermutations.isEmpty()) throw new IllegalStateException("Already additional non-standard permutations added, so no standard permutation is allowed");
        this.standardPermutations.add(permutation);
    }

    public void setNonStandardPermutations(Set additionalExtremePermutations) {
        this.nonStandardPermutations = additionalExtremePermutations;
    }
    
    public void addNonStandardPermutation(final String permutation) {
        if (!this.standardPermutations.contains(permutation)) this.nonStandardPermutations.add(permutation);
    }
    
    public int size() {
        return this.standardPermutations.size() + this.nonStandardPermutations.size();
    }
    
    public void seal() {
        this.sealed = true;
        this.standardPermutations = Collections.unmodifiableSet(this.standardPermutations);
        this.nonStandardPermutations = Collections.unmodifiableSet(this.nonStandardPermutations);
    }

    public Set<String> getStandardPermutations() {
        if (!this.sealed) return new HashSet(this.standardPermutations);
        return this.standardPermutations;
    }

    public Set<String> getNonStandardPermutations() {
        if (!this.sealed) return new HashSet(this.nonStandardPermutations);
        return this.nonStandardPermutations;
    }
    
    //1.5@Override
    public String toString() {
        // SP = Standard permutations   NSP = non-stndard permutations
        return "SP: "+
                this.standardPermutations+
                "\nNSP: "+
                this.nonStandardPermutations;
    }
    
}
