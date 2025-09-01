package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.feature.AimingFeature;
import com.vicmatskiv.pointblank.feature.MuzzleFlashFeature;
import com.vicmatskiv.pointblank.feature.PartVisibilityFeature;
import com.vicmatskiv.pointblank.feature.SoundFeature;
import com.vicmatskiv.pointblank.item.FireMode;
import com.vicmatskiv.pointblank.item.GunItem;
import com.vicmatskiv.pointblank.item.GunItem.FirePhase;
import com.vicmatskiv.pointblank.item.GunItem.ReloadPhase;
import com.vicmatskiv.pointblank.registry.AmmoRegistry;
import com.vicmatskiv.pointblank.registry.ItemRegistry;
import com.vicmatskiv.pointblank.util.Conditions;
import com.vicmatskiv.pointblank.util.TimeUnit;
import net.minecraft.item.ItemConvertible;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class DerpyGunRegistry {
    public static final Supplier<GunItem> WESTAR34;
    public static final Supplier<GunItem> EE3CARBINE;
    public static final Supplier<GunItem> DC15S;
    public static final Supplier<GunItem> PEACEMAKERDEAGLE;

    public DerpyGunRegistry() {}

    public static void registerTabItems(Consumer<ItemConvertible> entries) {
        entries.accept(WESTAR34.get());
        entries.accept(EE3CARBINE.get());
        entries.accept(DC15S.get());
        entries.accept(PEACEMAKERDEAGLE.get());
    }

    public static void init() {}

    static {
        int WESTAR34_CAPACITY = 6;
        int EE3CARBINE_CAPACITY = 50;
        int DC15S_CAPACITY = 160;
        int PEACEMAKERDEAGLE_CAPACITY = 8;

        WESTAR34 = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("westar34")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(WESTAR34_CAPACITY)
                        .withDamage(15.0F)
                        .withRpm(89)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.WESTAR34FIRE)
                        .withReloadSound(DerpySoundRegistry.WESTARSTEAM)
                        .withDrawCooldownDuration(1200, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(4516, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(0.8)
                        .withShakeRecoilAmplitude(0.35)
                        .withShakeRecoilSpeed(3.0)
                        .withViewRecoilAmplitude(3.0)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_RED)
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_RED)
                                .withCondition(Conditions.doesNotHaveAttachmentGroup("smg_suppressors")))
                        .withFeature(new AimingFeature.Builder()
                                .withCondition(Conditions.doesNotHaveAttachmentGroup("hg_sights"))
                                .withZoom(0.25))
                        .withFeature(new PartVisibilityFeature.Builder()
                                .withShownPart("sightmount", Conditions.hasAttachmentGroup("hg_sights")))
                        .withFeature(new SoundFeature.Builder()
                                .withCondition(Conditions.hasAttachmentGroup("smg_suppressors"))
                                .withFireSound(DerpySoundRegistry.M1911_SILENCED, 1.0))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
                        .withCompatibleAttachmentGroup("m1911a1_skins")
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reloadempty", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3))))
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reload", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))));

        EE3CARBINE = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("ee3carbine")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(EE3CARBINE_CAPACITY)
                        .withDamage(15.0F)
                        .withRpm(500)
                        .withFireModes(FireMode.BURST, FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.WESTAR34FIRE)
                        .withReloadSound(DerpySoundRegistry.WESTARSTEAM)
                        .withDrawCooldownDuration(1200, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(6267, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(0.8)
                        .withShakeRecoilAmplitude(0.35)
                        .withShakeRecoilSpeed(3.0)
                        .withViewRecoilAmplitude(3.0)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_RED)
                        .withScopeOverlay("textures/gui/scope.png")
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_RED)
                                .withCondition(Conditions.doesNotHaveAttachmentGroup("smg_suppressors")))
                        .withFeature(new AimingFeature.Builder()
                                .withCondition(Conditions.doesNotHaveAttachmentGroup("hg_sights"))
                                .withZoom(0.25))
                        .withFeature(new PartVisibilityFeature.Builder()
                                .withShownPart("sightmount", Conditions.hasAttachmentGroup("hg_sights")))
                        .withFeature(new SoundFeature.Builder()
                                .withCondition(Conditions.hasAttachmentGroup("smg_suppressors"))
                                .withFireSound(DerpySoundRegistry.M1911_SILENCED, 1.0))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
                        .withCompatibleAttachmentGroup("m1911a1_skins")
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reloadempty", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3))))
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reload", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))));

        DC15S = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("dc15s")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(DC15S_CAPACITY)
                        .withDamage(18.0F)
                        .withRpm(300)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.DC15S_FIRE)
                        .withReloadSound(DerpySoundRegistry.WESTARSTEAM)
                        .withDrawCooldownDuration(1000, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(4000, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(0.9)
                        .withShakeRecoilAmplitude(0.4)
                        .withShakeRecoilSpeed(3.2)
                        .withViewRecoilAmplitude(3.5)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_BLUE)
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_BLUE))
                        .withFeature(new AimingFeature.Builder()
                                .withZoom(0.3))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle"));

        PEACEMAKERDEAGLE = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("peacemakerdeagle")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(PEACEMAKERDEAGLE_CAPACITY)
                        .withDamage(22.0F)
                        .withRpm(120)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.PEACEMAKERDEAGLE_FIRE)
                        .withReloadSound(DerpySoundRegistry.PEACEMAKERDEAGLE_RELOAD)
                        .withDrawCooldownDuration(1000, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(4000, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(1.0)
                        .withShakeRecoilAmplitude(0.5)
                        .withShakeRecoilSpeed(3.5)
                        .withViewRecoilAmplitude(4.0)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_YELLOW)
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_YELLOW))
                        .withFeature(new AimingFeature.Builder()
                                .withZoom(0.25))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle"));
    }
}
