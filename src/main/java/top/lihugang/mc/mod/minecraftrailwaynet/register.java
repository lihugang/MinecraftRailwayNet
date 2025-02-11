package top.lihugang.mc.mod.minecraftrailwaynet;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import top.lihugang.mc.mod.minecraftrailwaynet.blocks.Rail;
import top.lihugang.mc.mod.minecraftrailwaynet.items.RailConnector;
import top.lihugang.mc.mod.minecraftrailwaynet.items.RailRemover;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory.BlockFactory;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory.ItemFactory;

import static top.lihugang.mc.mod.minecraftrailwaynet.Minecraftrailwaynet.MOD_ID;

public class register {
    public static void doRegister() {
        Block RailBlock = BlockFactory.register("block/rail", Rail::new, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).noCollision());

        Item RailConnectorItem = ItemFactory.register("item/rail_connector", RailConnector::new, new Item.Settings().maxCount(1));
        Item RailRemoverItem = ItemFactory.register("item/rail_remover", RailRemover::new, new Item.Settings().maxCount(1));

        ItemGroup CoreGroup = FabricItemGroup.builder()
                .icon(() -> new ItemStack(RailBlock))
                .displayName(Text.translatable("itemGroup." + MOD_ID + ".core"))
                .entries((context, entries) -> {
                    entries.add(RailBlock);
                    entries.add(RailConnectorItem);
                    entries.add(RailRemoverItem);
                }).build();
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "core"), CoreGroup);
    }
}
