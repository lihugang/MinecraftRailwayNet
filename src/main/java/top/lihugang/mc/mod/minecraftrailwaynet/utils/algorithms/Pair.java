package top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms;

import java.io.Serializable;
import java.util.Objects;

public class Pair<A, B> implements Serializable {
    public A first;
    public B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        return Objects.equals(first, ((Pair<?, ?>) o).first) && Objects.equals(second, ((Pair<?, ?>) o).second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
