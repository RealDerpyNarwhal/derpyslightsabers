package net.derpy.mod.item;

import net.derpy.mod.Derpyslightsabers;
import com.vicmatskiv.pointblank.item.GunItem;
import net.derpy.mod.api.compat.pointblank.DerpyGunRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup WEAPONS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Derpyslightsabers.MOD_ID, "weapons"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.weapons"))
                    .icon(() -> new ItemStack(ModItems.ANAKINS_LIGHTSABER)).entries((displayContext, entries) -> {
                        //add weapon items here like anakins to add them to the group
                        entries.add(ModItems.ANAKINS_LIGHTSABER);



                    }).build());

    public static void registerItemGroups(){
        Derpyslightsabers.LOGGER.info("Registering Item Groups for" + Derpyslightsabers.MOD_ID);
    }
}
