package top.lihugang.mc.mod.minecraftrailwaynet.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Rail extends Block {
    public static final IntProperty DIRECTION = IntProperty.of("direction", 0, 7);
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
        Vec3d facing = placer.getFacing().getDoubleVector();
        System.out.println("" + facing.x + " " + facing.z);
    }
}
