package top.lihugang.mc.mod.minecraftrailwaynet;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import top.lihugang.mc.mod.minecraftrailwaynet.blocks.Rail;
import top.lihugang.mc.mod.minecraftrailwaynet.items.RailConnector;
import top.lihugang.mc.mod.minecraftrailwaynet.items.RailRemover;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory.BlockFactory;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory.ItemFactory;
import top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory.ItemGroupFactory;

public class Register {
    public static void register() {
        // keep this function calling to prevent import statement was optimized which will cause items cannot be registered.
    }

    /* Items */
    public static Item RailConnectorItem = ItemFactory.register("item/rail_connector", RailConnector::new, new Item.Settings().maxCount(1));
    public static Item RailRemoverItem = ItemFactory.register("item/rail_remover", RailRemover::new, new Item.Settings().maxCount(1));

    /* Blocks */
    public static Block RailBlock = BlockFactory.register("block/rail", Rail::new, AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).noCollision());

    public static ItemGroup coreGroup = ItemGroupFactory.register("itemGroup/core", RailBlock)
        .add(RailBlock)
        .add(RailConnectorItem)
        .add(RailRemoverItem)
        .build();
    /* Item Groups */
}
