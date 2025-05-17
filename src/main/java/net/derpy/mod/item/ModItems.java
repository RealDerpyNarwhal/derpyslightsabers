package net.derpy.mod.item;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.item.custom.AnakinsLightsaber;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final AnakinsLightsaber ANAKINSLIGHTSABER = registerItem("anakinslightsaber", new AnakinsLightsaber(new FabricItemSettings()));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(ANAKINSLIGHTSABER);
    }

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier(Derpyslightsabers.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Derpyslightsabers.LOGGER.info("Registering Mod Items for " + Derpyslightsabers.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
