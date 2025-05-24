package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityForceChoke extends ThematicAbility {

    // Track damage cooldown per player UUID (ticks until next damage)
    private final Map<UUID, Integer> damageCooldowns = new HashMap<>();
    // Track ability active duration per player UUID (ticks remaining)
    private final Map<UUID, Integer> activeDurations = new HashMap<>();

    public AbilityForceChoke(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register((livingEntity) -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return;

            // Only run effect if ability is active on player
            if (!isActive(player, this.getId())) {
                // Ability inactive: clear durations if any and return
                activeDurations.remove(player.getUuid());
                damageCooldowns.remove(player.getUuid());
                return;
            }

            UUID playerId = player.getUuid();

            // Decrement active duration
            int remainingDuration = activeDurations.getOrDefault(playerId, 0);
            if (remainingDuration <= 0) {
                // Duration ended — deactivate ability and clear cooldowns
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(playerId);
                damageCooldowns.remove(playerId);
                return;
            } else {
                activeDurations.put(playerId, remainingDuration - 1);
            }

            Entity target = getTarget(player);
            if (!(target instanceof LivingEntity livingTarget)) {
                // Target lost or invalid, deactivate ability
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(playerId);
                damageCooldowns.remove(playerId);
                return;
            }

            // Damage cooldown logic: damage every 40 ticks (2 seconds)
            int damageCd = damageCooldowns.getOrDefault(playerId, 0);
            if (damageCd <= 0) {
                // Damage 1 heart = 2.0f damage (total 10 damage over 10 hits)
                livingTarget.damage(livingTarget.getDamageSources().mobAttack(player), 1.0f);
                damageCooldowns.put(playerId, 40);
            } else {
                damageCooldowns.put(playerId, damageCd - 1);
            }

            // Calculate pull direction based on player look direction
            float headYaw = player.getYaw();
            float pitch = player.getPitch();
            double yawRad = Math.toRadians(headYaw);
            double pitchRad = Math.toRadians(pitch);

            double distance = range(player);

            double dirX = -Math.sin(yawRad) * Math.cos(pitchRad);
            double dirY = -Math.sin(pitchRad);
            double dirZ = Math.cos(yawRad) * Math.cos(pitchRad);

            double desiredX = player.getX() + dirX * distance;
            double desiredY = player.getY() + player.getEyeHeight(livingTarget.getPose()) + dirY * distance;
            double desiredZ = player.getZ() + dirZ * distance;

            // Smooth pull velocity factor
            double pullSpeed = 0.3;

            // Compute velocity vector towards desired position
            double velX = (desiredX - livingTarget.getX()) * pullSpeed;
            double velY = (desiredY - livingTarget.getY()) * pullSpeed;
            double velZ = (desiredZ - livingTarget.getZ()) * pullSpeed;

            // Apply velocity to gently pull target
            livingTarget.setVelocity(velX, velY, velZ);

            // Prevent fall damage by resetting fallDistance
            livingTarget.fallDistance = 0f;
        });
    }

    @Override
    public void press(PlayerEntity playerEntity, ItemStack armorStack) {
        super.press(playerEntity, armorStack);

        if (isBlocked(playerEntity)) return;
        if (this.getCooldown(playerEntity) > 0) return;

        Entity target = this.getTarget(playerEntity);
        if (!(target instanceof LivingEntity)) return;

        // Activate ability for 20 seconds (400 ticks)
        activeDurations.put(playerEntity.getUuid(), 400);
        damageCooldowns.put(playerEntity.getUuid(), 0); // reset damage cooldown
        setActive(playerEntity, this.getId(), this.getCooldown(playerEntity), true);
    }

    public void tick(PlayerEntity playerEntity, ItemStack armorStack, boolean tickCooldown) {
        if (!isActive(playerEntity, this.getId())) {
            decrementCooldown(playerEntity);
            return;
        }

        Entity target = getTarget(playerEntity);
        if (!(target instanceof LivingEntity livingTarget)) {
            setActive(playerEntity, this.getId(), cooldown(playerEntity), false);
            activeDurations.remove(playerEntity.getUuid());
            damageCooldowns.remove(playerEntity.getUuid());
            return;
        }

        if (livingTarget.isDead()) {
            setActive(playerEntity, this.getId(), cooldown(playerEntity), false);
            activeDurations.remove(playerEntity.getUuid());
            damageCooldowns.remove(playerEntity.getUuid());
            return;
        }

        setActive(playerEntity, this.getId(), this.getCooldown(playerEntity), true);
    }

    @Override
    public boolean isBlocked(LivingEntity livingEntity) {
        Entity target = this.getTarget(livingEntity);

        if (target == null) return true;
        if (target.distanceTo(livingEntity) > this.range(livingEntity)) return true;

        ItemStack armorStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return true;

        return super.isBlocked(livingEntity);
    }

    @Override
    public Entity getTarget(LivingEntity user) {
        double range = this.range(user);

        return user.getWorld().getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(range),
                        e -> e != user && e.isAlive() && user.canSee(e))
                .stream()
                .findFirst()
                .orElse(null);
    }
}
