package top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms;

import java.io.Serializable;
import java.util.Objects;

public class Triplet<A, B, C> implements Serializable {
    public A first;
    public B second;
    public C third;

    public Triplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return Objects.equals(first, ((Triplet<?, ?, ?>) o).first) && Objects.equals(second, ((Triplet<?, ?, ?>) o).second) && Objects.equals(third, ((Triplet<?, ?, ?>) o).third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
