package top.lihugang.mc.mod.minecraftrailwaynet.utils.minecraftItemFactory;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static top.lihugang.mc.mod.minecraftrailwaynet.MinecraftRailwayNet.MOD_ID;

public class ItemGroupFactory {
    static public ItemGroupFactory register(String id, ItemConvertible icon) {
        return new ItemGroupFactory(id, icon);
    }


    String id;
    ItemGroup.Builder builder;
    List<ItemConvertible> items;

    private ItemGroupFactory(String id, ItemConvertible icon) {
        this.builder = FabricItemGroup.builder()
            .icon(() -> new ItemStack(icon))
            .displayName(Text.translatable("itemGroup." + MOD_ID + "." + id.split("/")[1] /* itemGroup/xxx */));
        this.items = new ArrayList<>();
        this.id = id;
    }

    public ItemGroupFactory add(ItemConvertible item) {
        this.items.add(item);
        return this;
    }

    public ItemGroup build() {
        ItemGroup itemGroup = this.builder.entries((context, entries) -> {
            for (ItemConvertible item : this.items) {
                entries.add(item);
            }
        }).build();
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, this.id.toLowerCase() /* capitalized characters was not allowed in the id of item groups*/), itemGroup);
        return itemGroup;
    }
}
