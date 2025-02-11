package top.lihugang.mc.mod.minecraftrailwaynet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;

import java.io.File;
import java.nio.file.Path;

public class MinecraftRailwayNet implements ModInitializer {

    public static final String MOD_ID = "mrn";
    public static final Logger logger = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        logger.info("Initializing");

        Register.doRegister();

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.BEFORE_SAVE.register(this::onSave);
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        logger.info("Server started");
        File dataSavingDirectory = Path.of(WorldSavePath.ROOT.getRelativePath(), MOD_ID).toFile();
        if (!dataSavingDirectory.exists()) {
            dataSavingDirectory.mkdir();
            logger.info("Creating directory {}", dataSavingDirectory.getPath());
        }
    }

    private void onSave(MinecraftServer minecraftServer, boolean b, boolean b1) {
        logger.info("Server saved");
        for (RailwayNetStorage instance : RailwayNetStorage.getInstances()) {
            instance.save();
        }
    }
}
