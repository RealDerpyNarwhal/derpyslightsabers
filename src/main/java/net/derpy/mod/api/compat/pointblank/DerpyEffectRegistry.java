package net.derpy.mod.api.compat.pointblank;

import com.vicmatskiv.pointblank.client.SegmentsProviders;
import com.vicmatskiv.pointblank.client.effect.*;
import com.vicmatskiv.pointblank.client.effect.AbstractEffect.SpriteAnimationType;
import com.vicmatskiv.pointblank.client.effect.Effect.BlendMode;
import com.vicmatskiv.pointblank.util.Interpolators;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;

public class DerpyEffectRegistry {
    private static Map<String, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> suppliers = new HashMap<>();
    private static Map<UUID, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> effectSuppliersById = new HashMap<>();

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_FLASH_RED = register("blaster_flash_red", () ->
            new MuzzleFlashEffect.Builder()
                    .withName("blaster_flash_red")
                    .withTexture("textures/effect/blaster_flash_red.png")
                    .withDuration(50L)
                    .withBrightness(1)
                    .withSprites(1, 9, 1, SpriteAnimationType.RANDOM)
                    .withGlow(true)
                    .withWidthProvider(new Interpolators.ConstantFloatProvider(1.25F))
                    .withAlphaProvider(new Interpolators.EaseOutFloatProvider(0.6F))
                    .withInitialRollProvider(() -> 0.0F)
    );

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_LASER_RED = register("blaster_laser_red", () ->
            new DetachedProjectileEffect.Builder()
                    .withName("blaster_laser_red")
                    .withTexture("textures/effect/blaster_laser_red.png")
                    .withBlendMode(BlendMode.ADDITIVE)
                    .withDepthTest(true)
                    .withDuration(400L)
                    .withBlades(2, 0.0F, 0.75F)
                    .withFace(0.75F, 1.0F)
                    .withBladeBrightness(2)
                    .withFaceBrightness(2)
                    .withRotations(3.0)
                    .withGlow(true)
                    .withSegmentsProvider(new SegmentsProviders.MovingSegmentsProvider(100.0F, 0.0F))
                    .withBladeWidthProvider(new Interpolators.ConstantFloatProvider(0.2F))
                    .withFaceWidthProvider(new Interpolators.ConstantFloatProvider(0.1F))
    );

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_FLASH_BLUE = register("blaster_flash_blue", () ->
            new MuzzleFlashEffect.Builder()
                    .withName("blaster_flash_blue")
                    .withTexture("textures/effect/blaster_flash_blue.png")
                    .withDuration(50L)
                    .withBrightness(1)
                    .withSprites(1, 9, 1, SpriteAnimationType.RANDOM)
                    .withGlow(true)
                    .withWidthProvider(new Interpolators.ConstantFloatProvider(1.25F))
                    .withAlphaProvider(new Interpolators.EaseOutFloatProvider(0.6F))
                    .withInitialRollProvider(() -> 0.0F)
    );

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_LASER_BLUE = register("blaster_laser_blue", () ->
            new DetachedProjectileEffect.Builder()
                    .withName("blaster_laser_blue")
                    .withTexture("textures/effect/blaster_laser_blue.png")
                    .withBlendMode(BlendMode.ADDITIVE)
                    .withDepthTest(true)
                    .withDuration(400L)
                    .withBlades(2, 0.0F, 0.75F)
                    .withFace(0.75F, 1.0F)
                    .withBladeBrightness(2)
                    .withFaceBrightness(2)
                    .withRotations(3.0)
                    .withGlow(true)
                    .withSegmentsProvider(new SegmentsProviders.MovingSegmentsProvider(100.0F, 0.0F))
                    .withBladeWidthProvider(new Interpolators.ConstantFloatProvider(0.2F))
                    .withFaceWidthProvider(new Interpolators.ConstantFloatProvider(0.1F))
    );

    // PeacemakerDeagle yellow effects
    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_FLASH_YELLOW = register("blaster_flash_yellow", () ->
            new MuzzleFlashEffect.Builder()
                    .withName("blaster_flash_yellow")
                    .withTexture("textures/effect/muzzle_yellow.png")
                    .withDuration(50L)
                    .withBrightness(1)
                    .withSprites(1, 9, 1, SpriteAnimationType.RANDOM)
                    .withGlow(true)
                    .withWidthProvider(new Interpolators.ConstantFloatProvider(1.25F))
                    .withAlphaProvider(new Interpolators.EaseOutFloatProvider(0.6F))
                    .withInitialRollProvider(() -> 0.0F)
    );

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> BLASTER_LASER_YELLOW = register("blaster_laser_yellow", () ->
            new DetachedProjectileEffect.Builder()
                    .withName("blaster_laser_yellow")
                    .withTexture("textures/effect/bullet_yellow.png")
                    .withBlendMode(BlendMode.ADDITIVE)
                    .withDepthTest(true)
                    .withDuration(400L)
                    .withBlades(2, 0.0F, 0.75F)
                    .withFace(0.75F, 1.0F)
                    .withBladeBrightness(2)
                    .withFaceBrightness(2)
                    .withRotations(3.0)
                    .withGlow(true)
                    .withSegmentsProvider(new SegmentsProviders.MovingSegmentsProvider(100.0F, 0.0F))
                    .withBladeWidthProvider(new Interpolators.ConstantFloatProvider(0.2F))
                    .withFaceWidthProvider(new Interpolators.ConstantFloatProvider(0.1F))
    );

    public DerpyEffectRegistry() {}

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> register(String name, Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier) {
        if (suppliers.put(name, supplier) != null) {
            throw new IllegalArgumentException("Duplicate effect: " + name);
        } else {
            UUID effectId = getEffectId(name);
            Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> existingEffectSupplier = effectSuppliersById.put(effectId, supplier);
            if (existingEffectSupplier != null) {
                throw new IllegalArgumentException("Effect id collision for effect '" + name + "'. Try assigning a different name");
            } else {
                return supplier;
            }
        }
    }

    public static Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> getEffectBuilderSupplier(UUID effectId) {
        Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier = effectSuppliersById.get(effectId);
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
        Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>> supplier = suppliers.get(name);
        if (supplier == null) {
            throw new IllegalArgumentException("Effect '" + name + "' not found");
        } else {
            return supplier;
        }
    }

    private static List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>> getEntityEffects(Entity entity, Map<Class<? extends Entity>, List<Supplier<EffectBuilder<? extends EffectBuilder<?, ?>, ?>>>> effects) {
        return Collections.emptyList(); // simplified, gore disabled for now
    }
}
