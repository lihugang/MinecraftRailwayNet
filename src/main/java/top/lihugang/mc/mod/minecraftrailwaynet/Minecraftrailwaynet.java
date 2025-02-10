package top.lihugang.mc.mod.minecraftrailwaynet;

import net.fabricmc.api.ModInitializer;

public class Minecraftrailwaynet implements ModInitializer {

    public static final String MOD_ID = "mrn";

    @Override
    public void onInitialize() {
        register.doRegister();
    }
}
