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
import top.lihugang.mc.mod.minecraftrailwaynet.utils.FetchDimensionIdentifier;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

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
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            // Because this code runs on Server Side
            // So we can force cast it to net.minecraft.world.World
            String key = FetchDimensionIdentifier.fetch((World) world);

            RailwayNetStorage.getInstance(key).destroyNode(new Coord(pos));
        }
    }
}
