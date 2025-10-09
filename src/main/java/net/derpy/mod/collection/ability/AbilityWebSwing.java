package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.*;

public class AbilityWebSwing extends ThematicAbility {

    private static final double MAX_WEB_DISTANCE = 30.0;
    private static final double GRAVITY_PULL = 0.03;
    private static final double RELEASE_BOOST = 1.25;
    private static final double MIN_ROPE_LENGTH = 4.0;
    private static final double SWING_BOOST = 0.02; // forward swing propulsion

    private final Map<UUID, Vec3d> anchorPoints = new HashMap<>();
    private final Map<UUID, Double> ropeLengths = new HashMap<>();

    public AbilityWebSwing(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register(livingEntity -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return;

            UUID id = player.getUuid();

            if (!isActive(player, getId())) {
                anchorPoints.remove(id);
                ropeLengths.remove(id);
                return;
            }

            if (!anchorPoints.containsKey(id)) return;

            Vec3d anchor = anchorPoints.get(id);
            double ropeLength = ropeLengths.getOrDefault(id, 10.0);
            Vec3d playerPos = player.getPos();
            Vec3d rope = playerPos.subtract(anchor);
            double distance = rope.length();

            // Swing physics with tangential boost
            if (distance > ropeLength) {
                Vec3d ropeDir = rope.normalize();

                // Tangential velocity
                Vec3d vel = player.getVelocity();
                Vec3d tangential = vel.subtract(ropeDir.multiply(vel.dotProduct(ropeDir)));

                // Apply gravity
                tangential = tangential.add(0, -GRAVITY_PULL, 0);

                // Forward swing propulsion
                Vec3d forwardBoost = tangential.normalize().multiply(SWING_BOOST);
                tangential = tangential.add(forwardBoost);

                // Update player velocity
                player.setVelocity(tangential);

                // Rope length correction
                Vec3d correction = ropeDir.multiply(distance - ropeLength);
                player.setVelocity(player.getVelocity().subtract(correction.multiply(0.25)));
            }

            // Rope retraction while sneaking
            if (player.isSneaking() && distance > MIN_ROPE_LENGTH) {
                ropeLengths.put(id, ropeLength - 0.1);
            }

            // Auto-release if landing almost vertical
            if (player.isOnGround() && Math.abs(rope.y) < 2.0) {
                releaseSwing(player);
            }
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack armorStack) {
        super.press(player, armorStack);

        if (isBlocked(player)) return;
        if (getCooldown(player) > 0) return;

        UUID id = player.getUuid();

        // Release if already swinging
        if (anchorPoints.containsKey(id)) {
            releaseSwing(player);
            return;
        }

        // Auto-target nearest attachable block
        Vec3d anchor = findWebAnchor(player);
        if (anchor != null) {
            anchorPoints.put(id, anchor);
            ropeLengths.put(id, anchor.distanceTo(player.getPos()));

            player.playSound(SoundEvents.ENTITY_SPIDER_AMBIENT, 0.7F, 1.5F);
            setActive(player, getId(), cooldown(player), true);
        }
    }

    private Vec3d findWebAnchor(PlayerEntity player) {
        World world = player.getWorld();
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d look = player.getRotationVec(1.0F);

        Vec3d bestTarget = null;
        double closestDist = Double.MAX_VALUE;

        // Scan a grid in front of player
        for (double dx = -0.5; dx <= 0.5; dx += 0.25) {
            for (double dy = 0; dy <= 1.0; dy += 0.25) {
                for (double dz = -0.5; dz <= 0.5; dz += 0.25) {
                    Vec3d dir = look.add(dx, dy, dz).normalize();
                    BlockHitResult hit = world.raycast(new RaycastContext(
                            start, start.add(dir.multiply(MAX_WEB_DISTANCE)),
                            RaycastContext.ShapeType.OUTLINE,
                            RaycastContext.FluidHandling.NONE,
                            player
                    ));
                    if (hit.getType() == HitResult.Type.BLOCK) {
                        double dist = hit.getPos().distanceTo(player.getPos());
                        if (dist < closestDist) {
                            closestDist = dist;
                            bestTarget = hit.getPos();
                        }
                    }
                }
            }
        }

        return bestTarget;
    }

    private void releaseSwing(PlayerEntity player) {
        UUID id = player.getUuid();

        if (!anchorPoints.containsKey(id)) return;

        anchorPoints.remove(id);
        ropeLengths.remove(id);
        setActive(player, getId(), cooldown(player), false);

        Vec3d vel = player.getVelocity();
        player.setVelocity(vel.x * RELEASE_BOOST, vel.y * RELEASE_BOOST, vel.z * RELEASE_BOOST);
        player.playSound(SoundEvents.ENTITY_SPIDER_HURT, 0.8F, 1.6F);
    }

    @Override
    public boolean isBlocked(LivingEntity user) {
        ItemStack armorStack = user.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return true;
        return super.isBlocked(user);
    }

    @Override
    public net.minecraft.entity.Entity getTarget(LivingEntity user) {
        return null;
    }
}
