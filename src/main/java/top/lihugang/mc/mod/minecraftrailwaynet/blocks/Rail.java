package top.lihugang.mc.mod.minecraftrailwaynet.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.ConvertWorldAccessToWorld;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.FetchDimensionIdentifier;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

import java.util.Objects;

import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.logger;

public class Rail extends Block {
    public static final IntProperty DIRECTION = IntProperty.of("direction", 0, 7); // pi/8

    public Rail(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(DIRECTION, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        assert placer != null;
        float yaw = placer.getYaw();
        int direction = (int) (((yaw + 11.25) / 22.5) % 8);
        if (direction < 0) direction = 7 + direction;

        world.setBlockState(pos, state.with(DIRECTION, direction));

        if (!world.isClient) {
            String key = FetchDimensionIdentifier.fetch(world);

            RailwayNetStorage.getInstance(key).addNode(new Coord(pos), direction);
        }
    }

    @Override
    public void onBroken(WorldAccess worldAccess, BlockPos pos, BlockState state) {
        // why this function provides WorldAccess!!!
        // that means that I can't get level name
        if (!worldAccess.isClient()) {
            World world = ConvertWorldAccessToWorld.convert(worldAccess);
            if (Objects.isNull(world)) {
                logger.warn("Failed to remove rail node ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
                return;
            }
            String key = FetchDimensionIdentifier.fetch(world);

            RailwayNetStorage.getInstance(key).destroyNode(new Coord(pos));
        }
    }
}
