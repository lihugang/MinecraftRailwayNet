package top.lihugang.mc.mod.minecraftrailwaynet.utils;

import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class ConvertWorldAccessToWorld { // brute foreach
    static public World convert(WorldAccess worldAccess) {
        DimensionType dimension = worldAccess.getDimension();
        if (dimension.bedWorks()) {
            //如果床能用，那么必定是主世界
            return Objects.requireNonNull(worldAccess.getServer()).getOverworld();
        } else if (dimension.hasCeiling()) {
            //如果有天花板，那么必定是下界
            return Objects.requireNonNull(worldAccess.getServer()).getWorld(World.NETHER);
        } else return Objects.requireNonNull(worldAccess.getServer()).getWorld(World.END); //两个条件都不符合，只能是末地
    }
}
