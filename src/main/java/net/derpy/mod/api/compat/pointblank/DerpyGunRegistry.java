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
import com.vicmatskiv.pointblank.registry.EffectRegistry;
import com.vicmatskiv.pointblank.registry.ItemRegistry;
import com.vicmatskiv.pointblank.registry.SoundRegistry;
import com.vicmatskiv.pointblank.util.Conditions;
import com.vicmatskiv.pointblank.util.TimeUnit;
import net.derpy.mod.Derpyslightsabers;
import net.minecraft.item.ItemConvertible;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.entity.effect.StatusEffectInstance.INFINITE;


public final class DerpyGunRegistry {
    public static final Supplier<GunItem> WESTAR34;
    public static final Supplier<GunItem> EE3CARBINE;

    public DerpyGunRegistry() {
    }
    public static void registerTabItems(Consumer<ItemConvertible> entries) {
        entries.accept((ItemConvertible) WESTAR34.get());
        entries.accept((ItemConvertible) EE3CARBINE.get());
    }

    public static void init() {
    }

    static {
        int infinite = 0x7fffffff;
        WESTAR34 = ItemRegistry.ITEMS.register(((GunItem.Builder) (new GunItem.Builder()).withName("westar34").withCompatibleAmmo(new Supplier[]{AmmoRegistry.AMMOCREATIVE}).withMaxAmmoCapacity(infinite).withDamage(15.0F)).withRpm(89).withFireModes(new FireMode[]{FireMode.SINGLE}).withFireSound(DerpySoundRegistry.WESTAR34FIRE).withReloadSound(DerpySoundRegistry.WESTARSTEAM).withDrawCooldownDuration(1200, TimeUnit.MILLISECOND).withInspectCooldownDuration(4516, TimeUnit.MILLISECOND).withGunRecoilInitialAmplitude(0.8).withShakeRecoilAmplitude(0.35).withShakeRecoilSpeed(3.0).withViewRecoilAmplitude(3.0).withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_RED).withFeature((new MuzzleFlashFeature.Builder()).withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_RED).withCondition(Conditions.doesNotHaveAttachmentGroup("smg_suppressors"))).withFeature((new AimingFeature.Builder()).withCondition(Conditions.doesNotHaveAttachmentGroup("hg_sights")).withZoom(0.25)).withFeature((new PartVisibilityFeature.Builder()).withShownPart("sightmount", Conditions.hasAttachmentGroup("hg_sights"))).withFeature((new SoundFeature.Builder()).withCondition(Conditions.hasAttachmentGroup("smg_suppressors")).withFireSound(DerpySoundRegistry.M1911_SILENCED, 1.0)).withCompatibleAttachmentGroup(new String[]{"hg_sights"}).withCompatibleAttachmentGroup(new String[]{"smg_muzzle"}).withCompatibleAttachmentGroup(new String[]{"m1911a1_skins"}).withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2866L, new GunItem.ReloadAnimation("animation.model.reloadempty", List.of(new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8), new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4), new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5), new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))).withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2866L, new GunItem.ReloadAnimation("animation.model.reload", List.of(new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8), new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4), new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5), new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))));
        EE3CARBINE = ItemRegistry.ITEMS.register(((GunItem.Builder) (new GunItem.Builder()).withName("ee3carbine").withCompatibleAmmo(new Supplier[]{AmmoRegistry.AMMOCREATIVE}).withMaxAmmoCapacity(infinite).withDamage(15.0F)).withRpm(500).withFireModes(new FireMode[]{FireMode.BURST,FireMode.SINGLE}).withFireSound(DerpySoundRegistry.WESTAR34FIRE).withReloadSound(DerpySoundRegistry.WESTARSTEAM).withDrawCooldownDuration(1200, TimeUnit.MILLISECOND).withInspectCooldownDuration(6267, TimeUnit.MILLISECOND).withGunRecoilInitialAmplitude(0.8).withShakeRecoilAmplitude(0.35).withShakeRecoilSpeed(3.0).withViewRecoilAmplitude(3.0).withEffect(FirePhase.HIT_SCAN_ACQUIRED, DerpyEffectRegistry.BLASTER_LASER_RED).withScopeOverlay("textures/gui/scope.png").withFeature((new MuzzleFlashFeature.Builder()).withEffect(FirePhase.FIRING, DerpyEffectRegistry.BLASTER_FLASH_RED).withCondition(Conditions.doesNotHaveAttachmentGroup("smg_suppressors"))).withFeature((new AimingFeature.Builder()).withCondition(Conditions.doesNotHaveAttachmentGroup("hg_sights")).withZoom(0.25)).withFeature((new PartVisibilityFeature.Builder()).withShownPart("sightmount", Conditions.hasAttachmentGroup("hg_sights"))).withFeature((new SoundFeature.Builder()).withCondition(Conditions.hasAttachmentGroup("smg_suppressors")).withFireSound(DerpySoundRegistry.M1911_SILENCED, 1.0)).withCompatibleAttachmentGroup(new String[]{"hg_sights"}).withCompatibleAttachmentGroup(new String[]{"smg_muzzle"}).withCompatibleAttachmentGroup(new String[]{"m1911a1_skins"}).withPhasedReload(ReloadPhase.RELOADING, Conditions.onEmptyReload(), 2866L, new GunItem.ReloadAnimation("animation.model.reloadempty", List.of(new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8), new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4), new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5), new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))).withPhasedReload(ReloadPhase.RELOADING, Conditions.onNonEmptyReload(), 2866L, new GunItem.ReloadAnimation("animation.model.reload", List.of(new GunItem.ReloadShakeEffect(0L, 2866L, 0.2, 0.8), new GunItem.ReloadShakeEffect(170L, 1000L, 0.35, 0.4), new GunItem.ReloadShakeEffect(530L, 800L, 0.13, 0.5), new GunItem.ReloadShakeEffect(730L, 400L, 0.2, 0.3)))));
    }
}
