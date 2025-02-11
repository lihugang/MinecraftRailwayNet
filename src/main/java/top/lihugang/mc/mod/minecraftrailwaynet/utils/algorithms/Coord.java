package top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms;

import net.minecraft.util.math.BlockPos;

public class Coord extends Triplet<Integer, Integer, Integer> {
    public Coord(Integer x, Integer y, Integer z) {
        super(x, y, z);
    }

    public Coord(BlockPos blockPos) {
        super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Integer getX() {
        return first;
    }

    public Integer getY() {
        return second;
    }

    public Integer getZ() {
        return third;
    }

    public double distanceTo(Coord coord) {
        return Math.sqrt(
                (getX() - coord.getX()) * (getX() - coord.getX())
                        + (getY() - coord.getY()) * (getY() - coord.getY())
                        + (getZ() - coord.getZ()) * (getZ() - coord.getZ())
        );
    }
}
