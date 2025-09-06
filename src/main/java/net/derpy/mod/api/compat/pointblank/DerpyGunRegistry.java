package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.feature.AimingFeature;
import com.vicmatskiv.pointblank.feature.MuzzleFlashFeature;
import com.vicmatskiv.pointblank.feature.PartVisibilityFeature;
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
    public static final Supplier<GunItem> PENGUINUMBRELLA;
    public static final Supplier<GunItem> JOKER_REVOLVER;

    public DerpyGunRegistry() {}

    public static void registerTabItems(Consumer<ItemConvertible> entries) {
        entries.accept(WESTAR34.get());
        entries.accept(EE3CARBINE.get());
        entries.accept(DC15S.get());
        entries.accept(PEACEMAKERDEAGLE.get());
        entries.accept(PENGUINUMBRELLA.get());
        entries.accept(JOKER_REVOLVER.get());
    }

    public static void init() {}

    static {
        WESTAR34 = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("westar34")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(6)
                        .withDamage(15.0F)
                        .withRpm(89)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.WESTAR34_FIRE)
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
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3))))
        );

        EE3CARBINE = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("ee3carbine")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(50)
                        .withDamage(15.0F)
                        .withRpm(500)
                        .withFireModes(FireMode.BURST, FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.WESTAR34_FIRE)
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
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3))))
        );

        DC15S = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("dc15s")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(160)
                        .withDamage(18.0F)
                        .withRpm(300)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.DC15S_FIRE)
                        .withDrawCooldownDuration(1000, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(4000, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(0.9)
                        .withShakeRecoilAmplitude(0.4)
                        .withShakeRecoilSpeed(3.2)
                        .withViewRecoilAmplitude(3.5)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_BLUE)
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_BLUE))
                        .withFeature(new AimingFeature.Builder().withZoom(0.3))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
        );

        PEACEMAKERDEAGLE = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("peacemakerdeagle")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(8)
                        .withDamage(10.0F)
                        .withRpm(500)
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
                        .withFeature(new AimingFeature.Builder().withZoom(0.25))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reloadempty", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 300L, 0.2, 0.3))))
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2866L,
                                new GunItem.ReloadAnimation("animation.model.reload", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4),
                                        new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5),
                                        new GunItem.ReloadShakeEffect(730L, 300L, 0.2, 0.3))))
        );

        PENGUINUMBRELLA = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("penguinumbrella")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(60)
                        .withDamage(3.50F)
                        .withRpm(800)
                        .withFireModes(FireMode.AUTOMATIC)
                        .withFireSound(DerpySoundRegistry.PENGUINUMBRELLA_FIRE)
                        .withReloadSound(DerpySoundRegistry.PENGUINUMBRELLA_RELOAD)
                        .withDrawCooldownDuration(800, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(3000, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(0.6)
                        .withShakeRecoilAmplitude(0.25)
                        .withShakeRecoilSpeed(2.5)
                        .withViewRecoilAmplitude(2.0)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_YELLOW)
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_YELLOW))
                        .withFeature(new AimingFeature.Builder().withZoom(0.2))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2000L,
                                new GunItem.ReloadAnimation("animation.model.penguinumbrella_reloadempty", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2000L, 0.15, 0.7),
                                        new GunItem.ReloadShakeEffect(100L, 800L, 0.25, 0.3))))
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2000L,
                                new GunItem.ReloadAnimation("animation.model.penguinumbrella_reload", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2000L, 0.15, 0.7),
                                        new GunItem.ReloadShakeEffect(100L, 800L, 0.25, 0.3))))

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
                                        new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3))))
        );

        JOKER_REVOLVER = ItemRegistry.ITEMS.register(
                new GunItem.Builder()
                        .withName("joker_revolver")
                        .withCompatibleAmmo(AmmoRegistry.AMMOCREATIVE)
                        .withMaxAmmoCapacity(6)
                        .withDamage(25.0F)
                        .withRpm(80)
                        .withFireModes(FireMode.SINGLE)
                        .withFireSound(DerpySoundRegistry.JOKER_REVOLVER_FIRE)
                        .withReloadSound(DerpySoundRegistry.JOKER_REVOLVER_RELOAD)
                        .withDrawCooldownDuration(1200, TimeUnit.MILLISECOND)
                        .withInspectCooldownDuration(4000, TimeUnit.MILLISECOND)
                        .withGunRecoilInitialAmplitude(1.0)
                        .withShakeRecoilAmplitude(0.5)
                        .withShakeRecoilSpeed(3.5)
                        .withViewRecoilAmplitude(4.0)
                        .withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_YELLOW) // yellow bullet
                        .withFeature(new MuzzleFlashFeature.Builder()
                                .withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_YELLOW)) // yellow muzzle
                        .withFeature(new AimingFeature.Builder().withZoom(0.25))
                        .withCompatibleAttachmentGroup("hg_sights")
                        .withCompatibleAttachmentGroup("smg_muzzle")
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2500L,
                                new GunItem.ReloadAnimation("animation.model.joker_revolver_reloadempty", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2500L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(100L, 1200L, 0.3, 0.4))))
                        .withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2500L,
                                new GunItem.ReloadAnimation("animation.model.joker_revolver_reload", List.of(
                                        new GunItem.ReloadShakeEffect(0L, 2500L, 0.2, 0.8),
                                        new GunItem.ReloadShakeEffect(100L, 1200L, 0.3, 0.4))))
        );
    }
}
