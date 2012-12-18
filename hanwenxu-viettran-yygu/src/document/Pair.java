package document;

import java.io.Serializable;

/**
 * Pair represents a pair of values (x,y). x and y MAY NOT be null.
 * 
 * @param <X>
 *            Type of the first object
 * @param <Y>
 *            Type of the second object
 */
public class Pair<X, Y> implements Serializable {

    private static final long serialVersionUID = -8784148340029363617L;

    /** First object with type X */
    public X first;
    
    /** second object with type Y */
    public Y second;

    // rep invariant: first, second != null

    /**
     * Constructor to make a pair.
     * 
     * @param first
     *            Requires first != null.
     * @param second
     *            Requires second != null.
     */
    public Pair(X first, Y second) {
        if (first == null || second == null)
            throw new NullPointerException();
        this.first = first;
        this.second = second;
    }

    /**
     * override equals method and makes the appropriate test for equality
     * @param o - check to see if Pair is equal to this object.
     * @return true if equality, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !(o instanceof Pair<?, ?>)) {
            return false;
        }

        Object otherFirst = ((Pair<?, ?>) o).first;
        Object otherSecond = ((Pair<?, ?>) o).second;
        return this.first.equals(otherFirst) && this.second.equals(otherSecond);
    }

    /**
     * Generates the correct hashcode for a given Pair instance
     * @return hashcode of the pair
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + first.hashCode();
        result = 37 * result + second.hashCode();
        return result;
    }


}
