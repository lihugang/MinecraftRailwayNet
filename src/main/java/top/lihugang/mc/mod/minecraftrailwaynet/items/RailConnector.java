package top.lihugang.mc.mod.minecraftrailwaynet.items;

import org.jetbrains.annotations.NotNull;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

public class RailConnector extends RailOperator {
    public RailConnector(Settings settings) {
        super(settings);
    }

    @Override
    public @NotNull String getOperatorId() {
        return "rail_connector";
    }

    @Override
    public void doAction(String dimensionKey, Coord from, Coord to) {
        RailwayNetStorage.getInstance(dimensionKey).connect(from, to);
    }
}
