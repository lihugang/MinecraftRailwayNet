package top.lihugang.mc.mod.minecraftrailwaynet.items;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.FetchDimensionIdentifier;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

import java.util.List;

import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.MOD_ID;

public abstract class RailOperator extends Item {
    // The common part of RailConnector and RailRemover
    public RailOperator(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemstack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(itemstack, context, tooltip, type);

        NbtCompound nbt = itemstack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("firstNodeRecorded") && nbt.getBoolean("firstNodeRecorded"))
            tooltip.add(Text.translatable("item.mrn." + getOperatorId() + ".tooltip.recording", nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")));
        else tooltip.add(Text.translatable("item.mrn." + getOperatorId() + ".tooltip.unrecorded"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        Block target = world.getBlockState(pos).getBlock();
        Block RailBlock = Registries.BLOCK.get(Identifier.of(MOD_ID, "block/rail"));
        if (target != RailBlock) return ActionResult.FAIL;

        NbtCompound nbt = context.getStack().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("firstNodeRecorded") && nbt.getBoolean("firstNodeRecorded")) {
            nbt.putBoolean("firstNodeRecorded", false);

            if (!world.isClient) {
                String key = FetchDimensionIdentifier.fetch(world);

                Coord from = new Coord(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                Coord to = new Coord(pos);

                if (!from.equals(to)) doAction(key, from, to);
            }
        } else {
            nbt.putBoolean("firstNodeRecorded", true);
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
        }

        context.getStack().set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return ActionResult.SUCCESS;
    }

    public abstract String getOperatorId();

    public abstract void doAction(String dimensionName, Coord from, Coord to);
}
