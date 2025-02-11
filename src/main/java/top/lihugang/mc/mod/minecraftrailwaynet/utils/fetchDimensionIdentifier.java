package top.lihugang.mc.mod.minecraftrailwaynet.utils;

import net.minecraft.world.World;

import java.util.Objects;

public class fetchDimensionIdentifier {
    static public String fetch(World world) {
        return Objects.requireNonNull(world.getServer()).getSaveProperties().getLevelName() // save name in single player mode
                + "." +
                world.getRegistryKey().getValue().getPath(); // dimension name
    }
}
