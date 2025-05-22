package net.derpy.mod.collection;


import bond.thematic.api.registries.armors.Collection;
import bond.thematic.api.registries.armors.ability.AbilityRegistry;
import bond.thematic.api.registries.armors.armor.ArmorRegistry;
import bond.thematic.api.registries.armors.armor.ThematicArmor;
import bond.thematic.api.registries.item.ItemRegistry;
import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.collection.ability.AbilityThrowSaber;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.registry.Registries;

public class LightsaberCollection extends Collection {
    public LightsaberCollection() {
        super(Derpyslightsabers.MOD_ID);
    }

    @Override
    public void initServer() {
        super.initServer();

        ThematicArmor derpyArmor = new DerpyArmor(this, "derpy_armor");

        if (!Registries.ITEM.containsId(derpyArmor.getIdentifier())) {
            ArmorRegistry.registerArmor(derpyArmor);
            AbilityRegistry.registerAbility(new AbilityThrowSaber("throw_saber"));
        }

    }
}
