package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class AbilityForcePush extends ThematicAbility {

    public AbilityForcePush(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public int cooldown(LivingEntity entity) {
        return 100; // 5 seconds
    }

    @Override
    public void press(PlayerEntity player, ItemStack armorStack) {
        if (isBlocked(player)) return;
        if (getCooldown(player) > 0) return;

        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d origin = player.getEyePos();
        double pushRange = range(player);

        // Sound effect
        player.playSound(SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0F, 1.2F);

        // Push entities
        List<Entity> targets = player.getWorld().getEntitiesByClass(Entity.class, new Box(origin, origin.add(lookVec.multiply(pushRange))).expand(2.0),
                e -> e != player && e.isAlive() && player.canSee(e));

        for (Entity target : targets) {
            Vec3d dir = target.getPos().subtract(player.getPos()).normalize();
            Vec3d velocity = dir.multiply(2.5).add(0, 0.5, 0); // upward + outward push
            target.setVelocity(velocity);
            target.velocityModified = true;

            if (target instanceof LivingEntity living) {
                living.fallDistance = 0f; // prevent fall damage from push
            }
        }

        // Optionally push weak blocks (e.g., sand, gravel)
        World world = player.getWorld();
        Vec3d reachVec = origin.add(lookVec.multiply(pushRange));

        BlockHitResult hit = world.raycast(new RaycastContext(
                origin, reachVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hit.getBlockPos();
            BlockState state = world.getBlockState(targetPos);
            if (!state.isAir() && state.getBlock().getHardness() >= 0 && state.getBlock().getHardness() < 2.0F) {
                if (!world.isClient()) {
                    FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, targetPos, state);
                    world.removeBlock(targetPos, false);
                    Vec3d velocity = lookVec.multiply(2.0).add(0, 0.4, 0);
                    falling.setVelocity(velocity);
                    falling.setNoGravity(false);
                    falling.timeFalling = 1;
                }
            }
        }

        setCooldown(player, cooldown(player));
    }

    @Override
    public boolean isBlocked(LivingEntity livingEntity) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        return stack.isEmpty() || !(stack.getItem() instanceof DerpyArmor);
    }

    public double range(LivingEntity entity) {
        return 6.0D;
    }
}
