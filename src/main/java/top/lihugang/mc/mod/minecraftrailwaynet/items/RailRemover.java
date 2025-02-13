package top.lihugang.mc.mod.minecraftrailwaynet.items;

import org.jetbrains.annotations.NotNull;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

public class RailRemover extends RailOperator {
    public RailRemover(Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull String getOperatorId() {
        return "rail_remover";
    }

    @Override
    public void doAction(String dimensionKey, Coord from, Coord to) {
        RailwayNetStorage.getInstance(dimensionKey).remove(from, to);
    }
}
