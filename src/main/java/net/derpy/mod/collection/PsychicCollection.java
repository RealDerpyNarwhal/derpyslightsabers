package net.derpy.mod.collection;

import bond.thematic.api.registries.armors.Collection;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import bond.thematic.api.registries.armors.ability.AbilityRegistry;
import bond.thematic.api.registries.armors.armor.ArmorRegistry;
import bond.thematic.api.registries.armors.armor.ThematicArmor;
import net.derpy.mod.collection.ability.AbilityMindCrush;
import net.derpy.mod.collection.ability.AbilityPsychicRift;
import net.derpy.mod.collection.ability.AbilityPsychicVortex;
import net.derpy.mod.collection.ability.AbilityDroneSwarm;
import net.derpy.mod.collection.armor.PsychicArmor;

import java.util.List;

public class PsychicCollection extends Collection {
    private static boolean registered = false;

    public PsychicCollection() {
        super("psychic_suit"); // Unique collection ID
    }

    public void initServer() {
        if (!registered) {

            // Register the armor
            ThematicArmor psychicArmor = new PsychicArmor(this, "psychic_suit");
            ArmorRegistry.registerArmor(psychicArmor);

            // Register all abilities
            AbilityRegistry.registerAbility(new AbilityMindCrush("mind_crush"));
            AbilityRegistry.registerAbility(new AbilityPsychicRift("psychic_rift"));
            AbilityRegistry.registerAbility(new AbilityPsychicVortex("psychic_vortex"));
            AbilityRegistry.registerAbility(new AbilityDroneSwarm("drone_swarm"));

            registered = true;
        }
    }

    public List<ThematicAbility> getAbilities() {
        return List.of(
                new AbilityMindCrush("mind_crush"),
                new AbilityPsychicRift("psychic_rift"),
                new AbilityDroneSwarm("drone_swarm")
        );
    }
}
