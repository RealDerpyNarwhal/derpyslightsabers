package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.Platform;
import com.vicmatskiv.pointblank.RegistryService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class DerpySoundRegistry {
    private static RegistryService<SoundEvent> soundRegistry = Platform.getInstance().getSoundRegistry();
    private static Map<String, Supplier<SoundEvent>> registeredSoundEvents = new HashMap<>();
    public static Supplier<SoundEvent> WESTAR34FIRE = register("westar34fire");
    public static Supplier<SoundEvent> WESTARSTEAM = register("westarsteam");
    public static Supplier<SoundEvent> M1911_SILENCED = register("m1911_silenced");
    public static Supplier<SoundEvent> DC15S_FIRE = register("dc15sfire");

    public DerpySoundRegistry() {}

    public static final Supplier<SoundEvent> register(String sound) {
        Supplier<SoundEvent> registeredSound = soundRegistry.register(sound, () -> SoundEvent.of(new Identifier("pointblank", sound)));
        registeredSoundEvents.putIfAbsent(sound, registeredSound);
        return registeredSound;
    }

    public static final SoundEvent getSoundEvent(String sound) {
        Supplier<SoundEvent> registeredSoundEvent = registeredSoundEvents.get(sound);
        return registeredSoundEvent != null ? registeredSoundEvent.get() : null;
    }

    public static void init() {
    }
}
