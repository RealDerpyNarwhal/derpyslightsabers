package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityForceChoke extends ThematicAbility {

    // Map to keep track of damage cooldown per player UUID
    private final Map<UUID, Integer> damageCooldowns = new HashMap<>();

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
            if (!isActive(player, this.getId())) return;

            Entity target = getTarget(player);
            if (!(target instanceof LivingEntity livingTarget)) {
                // Target lost or invalid, deactivate ability
                setActive(player, this.getId(), cooldown(player), false);
                return;
            }

            // Damage cooldown logic: damage only every 10 ticks (0.5 sec)
            UUID playerId = player.getUuid();
            int cooldown = damageCooldowns.getOrDefault(playerId, 0);

            if (cooldown <= 0) {
                livingTarget.damage(livingTarget.getDamageSources().mobAttack(player), (float) this.damage(player));
                damageCooldowns.put(playerId, 10);
            } else {
                damageCooldowns.put(playerId, cooldown - 1);
            }

            // Calculate pull position based on player look direction
            float headYaw = player.getYaw();
            float pitch = player.getPitch();
            double yawRad = Math.toRadians(headYaw);
            double pitchRad = Math.toRadians(pitch);

            double distance = range(player);

            double dirX = -Math.sin(yawRad) * Math.cos(pitchRad);
            double dirY = -Math.sin(pitchRad);
            double dirZ = Math.cos(yawRad) * Math.cos(pitchRad);

            double targetX = player.getX() + dirX * distance;
            double targetY = player.getY() + player.getEyeHeight(livingTarget.getPose()) + dirY * distance;
            double targetZ = player.getZ() + dirZ * distance;

            double width = livingTarget.getWidth();
            double height = livingTarget.getHeight();

            Box targetBox = new Box(
                    targetX - width / 2, targetY, targetZ - width / 2,
                    targetX + width / 2, targetY + height, targetZ + width / 2
            );

            boolean wouldSuffocate = livingTarget.getWorld().getBlockCollisions(livingTarget, targetBox).iterator().hasNext();

            if (!wouldSuffocate) {
                livingTarget.setPosition(targetX, targetY, targetZ);
            } else {
                for (int i = 1; i <= 3; i++) {
                    double safeDistance = distance - i;
                    if (safeDistance < 2) break;

                    double safeX = player.getX() + dirX * safeDistance;
                    double safeY = player.getY() + player.getEyeHeight(livingTarget.getPose()) + dirY * safeDistance;
                    double safeZ = player.getZ() + dirZ * safeDistance;

                    Box safeBox = new Box(
                            safeX - width / 2, safeY, safeZ - width / 2,
                            safeX + width / 2, safeY + height, safeZ + width / 2
                    );

                    if (!livingTarget.getWorld().getBlockCollisions(livingTarget, safeBox).iterator().hasNext()) {
                        livingTarget.setPosition(safeX, safeY, safeZ);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void press(PlayerEntity playerEntity, ItemStack armorStack) {
        super.press(playerEntity, armorStack);

        if (isBlocked(playerEntity)) return;
        if (this.getCooldown(playerEntity) > 0) return;

        Entity target = this.getTarget(playerEntity);
        if (!(target instanceof LivingEntity)) return;

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
            return;
        }

        if (livingTarget.isDead()) {
            setActive(playerEntity, this.getId(), cooldown(playerEntity), false);
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
