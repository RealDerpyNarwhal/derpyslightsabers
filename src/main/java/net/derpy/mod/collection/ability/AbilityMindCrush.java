package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.PsychicArmor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbilityMindCrush extends ThematicAbility {

    private final Map<UUID, Integer> activeDurations = new HashMap<>();
    private final Map<String, Integer> damageCooldowns = new HashMap<>();
    private final Map<UUID, Double> entityLiftHeights = new HashMap<>();

    private static final double RADIUS = 1.0;
    private static final double HEIGHT = 2.5;
    private static final int ROTATIONS = 3;
    private static final double SPEED = 0.1;

    public AbilityMindCrush(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register(livingEntity -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof PsychicArmor)) return;

            UUID playerId = player.getUuid();

            if (!isActive(player, this.getId())) {
                activeDurations.remove(playerId);
                damageCooldowns.keySet().removeIf(key -> key.startsWith(playerId.toString()));
                entityLiftHeights.clear();
                return;
            }

            int duration = activeDurations.getOrDefault(playerId, 0);
            if (duration <= 0) {
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(playerId);
                damageCooldowns.keySet().removeIf(key -> key.startsWith(playerId.toString()));
                entityLiftHeights.clear();
                return;
            }

            activeDurations.put(playerId, duration - 1);

            World world = player.getWorld();
            List<LivingEntity> targets = world.getEntitiesByClass(
                    LivingEntity.class,
                    new Box(player.getBlockPos()).expand(20),
                    e -> e.isAlive() && e != player && player.canSee(e)
            );

            for (LivingEntity target : targets) {
                UUID targetId = target.getUuid();

                double hoverHeight = 3.0;
                double fixedLiftY = entityLiftHeights.computeIfAbsent(targetId, id -> target.getY() + hoverHeight);

                double deltaY = fixedLiftY - target.getY();
                double liftSpeed = 0.3;
                double motionY = Math.min(Math.max(deltaY, -liftSpeed), liftSpeed);

                target.setVelocity(0, motionY, 0);
                target.velocityModified = true;
                target.setPos(target.getX(), target.getY(), target.getZ());

                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 5, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 3, false, false));

                String key = playerId + "-" + targetId;
                int cd = damageCooldowns.getOrDefault(key, 0);
                if (cd <= 0) {
                    target.damage(target.getDamageSources().magic(), 3.0f);
                    world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.0f, 1.6f);
                    damageCooldowns.put(key, 20);
                } else {
                    damageCooldowns.put(key, cd - 1);
                }

                if (world instanceof ServerWorld serverWorld) {
                    double time = target.age;
                    double baseY = target.getY();

                    for (int i = 0; i < 4; i++) {
                        double progress = ((time + i * 10) % 200) / 200.0;
                        double yUp = baseY + progress * HEIGHT;
                        double yDown = baseY + HEIGHT - progress * HEIGHT;

                        double angle = progress * ROTATIONS * 2 * Math.PI + time * SPEED;
                        double x = target.getX() + Math.cos(angle + i * Math.PI / 2) * RADIUS;
                        double z = target.getZ() + Math.sin(angle + i * Math.PI / 2) * RADIUS;

                        serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, x, yUp, z, 1, 0, 0, 0, 0);
                        serverWorld.spawnParticles(ParticleTypes.FLAME, x, yDown, z, 1, 0, 0, 0, 0);
                    }
                }
            }
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        if (isBlocked(player) || this.getCooldown(player) > 0) return;

        UUID playerId = player.getUuid();
        activeDurations.put(playerId, 240);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 1.4f, 0.6f);

        setActive(player, this.getId(), cooldown(player), true);
    }

    @Override
    public boolean isBlocked(LivingEntity user) {
        ItemStack armorStack = user.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof PsychicArmor)) return true;
        return super.isBlocked(user);
    }
}
