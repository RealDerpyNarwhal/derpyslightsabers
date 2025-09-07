package net.derpy.mod.api.compat.pointblank;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class DerpySoundRegistry extends SoundEvents {

    public static SoundEvent WESTAR34_FIRE = register("westar34_fire");
    public static SoundEvent DC15S_FIRE = register("dc15sfire");
    public static SoundEvent PEACEMAKERDEAGLE_FIRE = register("peacemakerdeagle_fire");
    public static SoundEvent PEACEMAKERDEAGLE_RELOAD = register("peacemakerdeagle_reload");
    public static SoundEvent PENGUINUMBRELLA_FIRE = register("penguinumbrella_fire");
    public static SoundEvent PENGUINUMBRELLA_RELOAD = register("penguinumbrella_reload");
    public static SoundEvent JOKER_REVOLVER_FIRE = register("joker_revolver_fire");
    public static SoundEvent JOKER_REVOLVER_RELOAD = register("joker_revolver_reload");

    public DerpySoundRegistry() {}

    private static SoundEvent register(String id) {
        SoundEvent sound = SoundEvent.of(Identifier.of("derpyslightsabers", id));
        return Registry.register(Registries.SOUND_EVENT, Identifier.of("derpyslightsabers", id), sound);
        }
    }
