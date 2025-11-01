package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityPsychicVortex extends ThematicAbility {

    private final Map<UUID, Integer> activeDurations = new HashMap<>();
    private final int DURATION_TICKS = 100; // 5 seconds
    private final int PULL_RADIUS = 5; // radius in blocks
    private final float DAMAGE_PER_SECOND = 2.5f;
    private final double SPHERE_RADIUS = 4.0; // radius of the particle sphere
    private final int PARTICLE_POINTS = 100; // number of particles on the sphere

    public AbilityPsychicVortex(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register((livingEntity) -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;
            if (!isActive(player, this.getId())) return;

            UUID id = player.getUuid();
            int duration = activeDurations.getOrDefault(id, 0);
            if (duration <= 0) {
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(id);
                return;
            } else {
                activeDurations.put(id, duration - 1);
            }


            for (Entity entity : player.getWorld().getEntitiesByClass(LivingEntity.class,
                    player.getBoundingBox().expand(PULL_RADIUS),
                    e -> e != player && e.isAlive())) {

                Vec3d direction = new Vec3d(
                        player.getX() - entity.getX(),
                        player.getY() - entity.getY(),
                        player.getZ() - entity.getZ()
                ).normalize().multiply(0.2);

                entity.addVelocity(direction.x, direction.y * 0.1, direction.z);
                if (entity instanceof LivingEntity living) {
                    living.velocityModified = true;

                    if (activeDurations.get(id) % 20 == 0) {
                        living.damage(living.getDamageSources().magic(), DAMAGE_PER_SECOND);
                        living.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 40, 1
                        ));
                    }
                }
            }

            for (int i = 0; i < PARTICLE_POINTS; i++) {
                double theta = Math.random() * 2 * Math.PI;
                double phi = Math.acos(2 * Math.random() - 1);
                double x = SPHERE_RADIUS * Math.sin(phi) * Math.cos(theta);
                double y = SPHERE_RADIUS * Math.sin(phi) * Math.sin(theta);
                double z = SPHERE_RADIUS * Math.cos(phi);

                // Randomly choose purple or red
                if (Math.random() < 0.5) {
                    player.getWorld().addParticle(ParticleTypes.END_ROD,
                            player.getX() + x, player.getY() + y, player.getZ() + z,
                            0, 0, 0);
                } else {
                    player.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT,
                            player.getX() + x, player.getY() + y, player.getZ() + z,
                            1, 0, 1); // RGB for purple-ish
                }
            }

            if (duration % 20 == 0) {
                player.getWorld().playSound(null, player.getBlockPos(),
                        SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 0.5f, 1.2f);
            }
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        if (isBlocked(player) || this.getCooldown(player) > 0) return;

        activeDurations.put(player.getUuid(), DURATION_TICKS);
        setActive(player, this.getId(), cooldown(player), true);
    }

    @Override
    public boolean isBlocked(LivingEntity user) {
        ItemStack armorStack = user.getEquippedStack(EquipmentSlot.CHEST);
        return armorStack.isEmpty();
    }
}
