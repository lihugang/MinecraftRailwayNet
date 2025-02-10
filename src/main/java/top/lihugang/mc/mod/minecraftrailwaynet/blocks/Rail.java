package top.lihugang.mc.mod.minecraftrailwaynet.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        if (yaw < 0) yaw += 180;
        int direction = (int) (((yaw + 11.25) / 22.5) % 8);
        world.setBlockState(pos, state.with(DIRECTION, direction));
    }
}
