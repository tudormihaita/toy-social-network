package application.toysocialnetwork.domain;

import java.util.Objects;

/**
 * Generic class representing a Tuple/Pair of 2 elements
 * @param <E1> Generic data type for the Tuple's elements
 */
public class Tuple<E1 extends Comparable<E1>> {
    private E1 e1;
    private E1 e2;

    /**
     * Initializes a tuple and its 2 components
     * @param e1    E1, first element
     * @param e2    E1, second element
     */
    public Tuple(E1 e1, E1 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * @return first element of the tuple
     */
    public E1 getFirst() {
        return e1;
    }

    /**
     * @return second element of the tuple
     */
    public E1 getSecond() {
        return e2;
    }

    /**
     * Setter for the first element
     * @param e1 E1, new first element
     */
    public void setFirst(E1 e1) {
        this.e1 = e1;
    }

    /**
     * Setter for the second element
     * @param e2 E1, new second element
     */
    public void setSecond(E1 e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "( "+ e1 + " , " + e2 + " )";
    }

    @Override
    public boolean equals(Object other) {
        return (this.getFirst().equals(((Tuple<?>) other).getFirst()) &&
                this.getSecond().equals(((Tuple<?>) other).getSecond())) ||
                (this.getFirst().equals(((Tuple<?>) other).getSecond()) &&
                        this.getSecond().equals(((Tuple<?>) other).getFirst()));
    }

    @Override
    public int hashCode() {
        if(e1.compareTo(e2) < 0)
            return Objects.hash(e1, e2);
        else{
            return Objects.hash(e2,e1);
        }
    }
}