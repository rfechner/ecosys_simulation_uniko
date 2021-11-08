package de.uniko.ecosystem.util;

import de.uniko.ecosystem.model.trees.Tree;

import java.util.Objects;

public class DistPair {
    public Tree other;
    public double distance;


    public DistPair(Tree other, double distance){
        this.distance = distance;
        this.other = other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistPair distPair = (DistPair) o;
        return Objects.equals(other, distPair.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(other);
    }
}
