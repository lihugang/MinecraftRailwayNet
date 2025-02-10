package top.lihugang.mc.mod.minecraftrailwaynet.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static top.lihugang.mc.mod.minecraftrailwaynet.Minecraftrailwaynet.MOD_ID;

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
        float yaw = placer.getYaw();
        if (yaw < 0) yaw += 180;
        int direction = (int)(((yaw + 11.25) / 22.5) % 8);
        world.setBlockState(pos, state.with(DIRECTION, direction));
    }

    @Override
    public ActionResult onUseWithItem(ItemStack item, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (item.getItem() != Registries.ITEM.get(Identifier.of(MOD_ID, "item/rail_connector")))
            return ActionResult.FAIL;
        return ActionResult.SUCCESS;
    }
}
