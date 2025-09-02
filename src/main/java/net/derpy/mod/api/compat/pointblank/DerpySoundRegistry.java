package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.Platform;
import com.vicmatskiv.pointblank.RegistryService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class DerpySoundRegistry {
    private static final RegistryService<SoundEvent> soundRegistry = Platform.getInstance().getSoundRegistry();
    private static final Map<String, Supplier<SoundEvent>> registeredSoundEvents = new HashMap<>();

    public static Supplier<SoundEvent> WESTAR34FIRE = register("westar34fire");
    public static Supplier<SoundEvent> WESTARSTEAM = register("westarsteam");
    public static Supplier<SoundEvent> DC15S_FIRE = register("dc15sfire");
    public static Supplier<SoundEvent> PEACEMAKERDEAGLE_FIRE = register("peacemakerdeagle_fire");
    public static Supplier<SoundEvent> PEACEMAKERDEAGLE_RELOAD = register("peacemakerdeagle_reload");
    public static final Supplier<SoundEvent> PENGUINUMBRELLA_FIRE = register("penguinumbrella_fire");
    public static final Supplier<SoundEvent> PENGUINUMBRELLA_RELOAD = register("penguinumbrella_reload");

    public DerpySoundRegistry() {}

    public static Supplier<SoundEvent> register(String sound) {
        Supplier<SoundEvent> registeredSound = soundRegistry.register(
                sound,
                () -> SoundEvent.of(new Identifier("pointblank", sound))
        );
        registeredSoundEvents.putIfAbsent(sound, registeredSound);
        return registeredSound;
    }

    public static SoundEvent getSoundEvent(String sound) {
        Supplier<SoundEvent> registeredSoundEvent = registeredSoundEvents.get(sound);
        return registeredSoundEvent != null ? registeredSoundEvent.get() : null;
    }

    public static void init() {
        for (Supplier<SoundEvent> soundSupplier : registeredSoundEvents.values()) {
            soundSupplier.get();
        }
    }
}
