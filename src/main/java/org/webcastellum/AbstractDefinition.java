package org.webcastellum;

import java.io.Serializable;

public abstract class AbstractDefinition implements Serializable, Comparable<AbstractDefinition> {
    private static final long serialVersionUID = 1L;
    
    protected final boolean enabled;
    protected final String identification;
    protected final String description;

    protected AbstractDefinition(final boolean enabled, final String identification, final String description) {
        if (identification == null) throw new NullPointerException("identification must not be null");
        if (description == null) throw new NullPointerException("description must not be null");
        this.enabled = enabled;
        this.identification = identification;
        this.description = description;
    }
    
    public final boolean isEnabled() {
        return this.enabled;
    }
    
    public final String getIdentification() {
        return identification;
    }
    
    public final String getDescription() {
        return description;
    }
    
    @Override
    public final String toString() {
        final StringBuilder result = new StringBuilder("Definition:");
        result.append(" identification=").append(identification);
        result.append(" description=").append(description);
        return result.toString();
    }
    
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.identification != null ? this.identification.hashCode() : 0);
        return hash;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if ((obj == null) || (obj.getClass() != this.getClass())) return false;
        AbstractDefinition other = (AbstractDefinition)obj;
        // short-cut to compare by object-identity first (before using equals then)
        return (identification == other.identification || (identification != null && identification.equals(other.identification)));
    }    
    
    @Override
    public final int compareTo(AbstractDefinition obj) {
        // here we enforce a natural sort order that uses the "identification" of this request definition,
        // so that the user can name (identify) the files using numbers for example (00_xxx, 01_xxx, 02_xxxx) 
        // to have the sorting one desires....
        final AbstractDefinition other = obj;
        final String identLeft = this.identification;
        final String identRight = other.identification;
        if (identLeft != null) return identRight == null ? -1 : identLeft.compareTo(identRight);
        return identRight == null ? 0 : 1;
    }    
 
    
}
