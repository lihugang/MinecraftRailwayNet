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
import top.lihugang.mc.mod.minecraftrailwaynet.utils.FetchDimensionIdentifier;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.RailwayNetStorage;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.algorithms.Coord;

import java.util.List;

import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.MOD_ID;

public class RailRemover extends Item {
    public RailRemover(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack itemstack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(itemstack, context, tooltip, type);

        NbtCompound nbt = itemstack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("removing") && nbt.getBoolean("removing"))
            tooltip.add(Text.translatable("item.mrn.rail_remover.tooltip.removing", nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        Block target = world.getBlockState(pos).getBlock();
        Block RailBlock = Registries.BLOCK.get(Identifier.of(MOD_ID, "block/rail"));
        if (target != RailBlock) return ActionResult.FAIL;

        NbtCompound nbt = context.getStack().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (nbt.contains("removing") && nbt.getBoolean("removing")) {
            nbt.putBoolean("removing", false);

            if (!world.isClient) {
                String key = FetchDimensionIdentifier.fetch(world);

                Coord from = new Coord(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
                Coord to = new Coord(pos.getX(), pos.getY(), pos.getZ());

                if (!from.equals(to))
                    RailwayNetStorage.getInstance(key).remove(from, to);
            }
        } else {
            nbt.putBoolean("removing", true);
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
        }

        context.getStack().set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return ActionResult.SUCCESS;
    }
}
