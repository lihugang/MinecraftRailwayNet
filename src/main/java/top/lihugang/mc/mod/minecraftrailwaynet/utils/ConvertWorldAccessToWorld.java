package top.lihugang.mc.mod.minecraftrailwaynet.utils;

import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class ConvertWorldAccessToWorld {
    static public World convert(WorldAccess worldAccess) {
        DimensionType dimension = worldAccess.getDimension();
        boolean bedWorks = dimension.bedWorks(),
                respawnAnchorWorks = dimension.respawnAnchorWorks(),
                hasCeiling = dimension.hasCeiling(),
                hasFixedTime = dimension.hasFixedTime(),
                natural = dimension.natural(),
                hasSkyLight = dimension.hasSkyLight(),
                piglinSafe = dimension.piglinSafe(),
                ultrawarm = dimension.ultrawarm();
        int height = dimension.height(),
                minY = dimension.minY(),
                monsterSpawnBlockLimit = dimension.monsterSpawnBlockLightLimit();
        float ambientLight = dimension.ambientLight();
        double coordinateScale = dimension.coordinateScale();

        for (World world : Objects.requireNonNull(worldAccess.getServer()).getWorlds()) {
            DimensionType currentDimension = world.getDimension();
            if (currentDimension.bedWorks() == bedWorks
                    && currentDimension.respawnAnchorWorks() == respawnAnchorWorks
                    && currentDimension.hasCeiling() == hasCeiling
                    && currentDimension.hasFixedTime() == hasFixedTime
                    && currentDimension.natural() == natural
                    && currentDimension.hasSkyLight() == hasSkyLight
                    && currentDimension.piglinSafe() == piglinSafe
                    && currentDimension.ultrawarm() == ultrawarm
                    && currentDimension.height() == height
                    && currentDimension.minY() == minY
                    && currentDimension.monsterSpawnBlockLightLimit() == monsterSpawnBlockLimit
                    && currentDimension.ambientLight() == ambientLight
                    && currentDimension.coordinateScale() == coordinateScale) return world;
        }
        return null;
    }
}
