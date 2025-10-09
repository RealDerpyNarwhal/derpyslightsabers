package net.derpy.mod.collection;


import bond.thematic.api.registries.armors.Collection;
import bond.thematic.api.registries.armors.ability.AbilityRegistry;
import bond.thematic.api.registries.armors.armor.ArmorRegistry;
import bond.thematic.api.registries.armors.armor.ThematicArmor;
import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.collection.ability.*;
import net.derpy.mod.collection.armor.DerpyArmor;

public class LightsaberCollection extends Collection {
    private static boolean registered = false;

    public LightsaberCollection() {
        super(Derpyslightsabers.MOD_ID);
    }

    @Override
    public void initServer() {
        super.initServer();

        if (!registered) {
            ThematicArmor derpyArmor = new DerpyArmor(this, "derpy_armor");
            ArmorRegistry.registerArmor(derpyArmor);
            AbilityRegistry.registerAbility(new AbilityThrowSaber("throw_saber"));
            AbilityRegistry.registerAbility(new AbilityForceChoke("force_choke"));
            AbilityRegistry.registerAbility(new AbilityForcePush("force_push"));
            AbilityRegistry.registerAbility(new AbilityForceSlam("force_slam"));
            AbilityRegistry.registerAbility(new AbilityWebSwing("web_swing"));
        }
        registered = true;

    }
}
