package top.lihugang.mc.mod.minecraftrailwaynet.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lihugang.mc.mod.minecraftrailwaynet.client.renderers.RailRenderer;

public class MinecraftRailwayNetClient implements ClientModInitializer {

    public static final String MOD_ID = "mrn";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        RailRenderer.initialize();
    }
}
