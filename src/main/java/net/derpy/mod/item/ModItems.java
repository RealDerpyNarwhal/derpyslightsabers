package net.derpy.mod.item;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.api.compat.pointblank.DerpyGunRegistry;
import net.derpy.mod.item.custom.AnakinsLightsaber;
import net.derpy.mod.item.custom.LukesLightsaber;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Lightsabers
    public static final AnakinsLightsaber ANAKINS_LIGHTSABER = registerItem(
            "anakins_lightsaber", new AnakinsLightsaber(new FabricItemSettings()));

    public static final LukesLightsaber LUKES_LIGHTSABER = registerItem(
            "lukes_lightsaber", new LukesLightsaber(new FabricItemSettings()));

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier(Derpyslightsabers.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Derpyslightsabers.LOGGER.info("Registering Mod Items for " + Derpyslightsabers.MOD_ID);
    }
}
