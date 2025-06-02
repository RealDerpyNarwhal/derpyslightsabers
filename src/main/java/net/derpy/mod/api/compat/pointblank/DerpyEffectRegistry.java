package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.Config;
import com.vicmatskiv.pointblank.client.SegmentsProviders;
import com.vicmatskiv.pointblank.client.effect.AttachedProjectileEffect;
import com.vicmatskiv.pointblank.client.effect.DetachedProjectileEffect;
import com.vicmatskiv.pointblank.client.effect.EffectBuilder;
import com.vicmatskiv.pointblank.client.effect.ImpactEffect;
import com.vicmatskiv.pointblank.client.effect.MuzzleFlashEffect;
import com.vicmatskiv.pointblank.client.effect.TrailEffect;
import com.vicmatskiv.pointblank.client.effect.AbstractEffect.SpriteAnimationType;
import com.vicmatskiv.pointblank.client.effect.Effect.BlendMode;
import com.vicmatskiv.pointblank.util.Interpolators;
import com.vicmatskiv.pointblank.util.ParticleValueProviders;
import com.vicmatskiv.pointblank.util.VelocityProviders;
import com.vicmatskiv.pointblank.util.VelocityProviders.Distribution;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DerpyEffectRegistry {
    private static Map<String, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> suppliers = new HashMap();
    private static Map<UUID, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> effectSuppliersById = new HashMap();
    private static Map<Class<? extends Entity>, List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>>> entityDeathEffects = new HashMap();
    private static Map<Class<? extends Entity>, List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>>> entityHitEffects = new HashMap();
    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_FLASH_RED = register("blaster_flash_red", () -> {
        return ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) ((MuzzleFlashEffect.Builder) (new MuzzleFlashEffect.Builder()).withName("blaster_flash_red")).withTexture("textures/effect/blaster_flash_red.png")).withDuration(50L)).withBrightness(1)).withSprites(1, 9, 1, SpriteAnimationType.RANDOM)).withGlow(true)).withWidthProvider(new Interpolators.ConstantFloatProvider(1.25F))).withAlphaProvider(new Interpolators.EaseOutFloatProvider(0.6F))).withInitialRollProvider(() -> {
            return 0.0F;
        });
    });

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_LASER_RED = register("blaster_laser_red", () -> {
        return ((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)((DetachedProjectileEffect.Builder)(new DetachedProjectileEffect.Builder()).withName("blaster_laser_red")).withTexture("textures/effect/blaster_laser_red.png")).withBlendMode(BlendMode.ADDITIVE)).withDepthTest(true)).withDuration(400L)).withBlades(2, 0.0F, 0.75F).withFace(0.75F, 1.0F).withBladeBrightness(2).withFaceBrightness(2).withRotations(3.0)).withGlow(true)).withSegmentsProvider(new SegmentsProviders.MovingSegmentsProvider(100.0F, 0.0F)).withBladeWidthProvider(new Interpolators.ConstantFloatProvider(0.2F)).withFaceWidthProvider(new Interpolators.ConstantFloatProvider(0.1F));
    });

    public DerpyEffectRegistry() {
    }

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> register(String name, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier) {
        if (suppliers.put(name, supplier) != null) {
            throw new IllegalArgumentException("Duplicate effect: " + name);
        } else {
            UUID effectId = getEffectId(name);
            Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> existingEffectSupplier = (Supplier) effectSuppliersById.put(effectId, supplier);
            if (existingEffectSupplier != null) {
                throw new IllegalArgumentException("Effect id collision for effect '" + name + "'. Try assigning a different name");
            } else {
                return supplier;
            }
        }
    }

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> getEffectBuilderSupplier(UUID effectId) {
        Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier = (Supplier) effectSuppliersById.get(effectId);
        if (supplier == null) {
            throw new IllegalArgumentException("Effect with id " + effectId + " not found");
        } else {
            return supplier;
        }
    }

    public static UUID getEffectId(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes());
    }

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> getEffectBuilderSupplier(String name) {
        Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier = (Supplier) suppliers.get(name);
        if (supplier == null) {
            throw new IllegalArgumentException("Effect '" + name + "' not found");
        } else {
            return supplier;
        }
    }

    private static List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> getEntityEffects(Entity entity, Map<Class<? extends Entity>, List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>>> effects) {
        if (!Config.goreEnabled) {
            return Collections.emptyList();
        } else {
            List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> exactMatchResults = null;
            List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> baseMatchResults = null;
            Iterator var4 = effects.entrySet().iterator();

            while (var4.hasNext()) {
                Map.Entry<Class<? extends Entity>, List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>>> e = (Map.Entry) var4.next();
                if (e.getKey() == entity.getClass()) {
                    exactMatchResults = (List) e.getValue();
                    break;
                }

                if (baseMatchResults == null && ((Class) e.getKey()).isAssignableFrom(entity.getClass())) {
                    baseMatchResults = (List) e.getValue();
                }
            }

            if (exactMatchResults != null) {
                return exactMatchResults;
            } else {
                return baseMatchResults != null ? baseMatchResults : Collections.emptyList();
            }
        }
    }
}