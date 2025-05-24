package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// ... (rest of your imports and class header remain unchanged)

public class AbilityForceGrab extends ThematicAbility {

    private final Map<UUID, FallingBlockEntity> grabbedBlocks = new HashMap<>();

    public AbilityForceGrab(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public int cooldown(LivingEntity entity) {
        return 200;
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register(livingEntity -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return;

            UUID playerId = player.getUuid();

            if (!isActive(player, this.getId())) {
                FallingBlockEntity grabbed = grabbedBlocks.remove(playerId);
                if (grabbed != null && grabbed.isAlive()) {
                    grabbed.discard();
                }
                return;
            }

            FallingBlockEntity grabbed = grabbedBlocks.get(playerId);
            if (grabbed == null || !grabbed.isAlive()) {
                setActive(player, this.getId(), cooldown(player), false);
                grabbedBlocks.remove(playerId);
                return;
            }

            World world = player.getWorld();
            Vec3d eyePos = player.getEyePos();
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d reachVec = eyePos.add(lookVec.multiply(range(player)));

            HitResult hitResult = world.raycast(new RaycastContext(
                    eyePos, reachVec,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            Vec3d targetPos = (hitResult.getType() == HitResult.Type.BLOCK || hitResult.getType() == HitResult.Type.MISS)
                    ? hitResult.getPos().add(0, 0.5, 0)
                    : reachVec;

            Vec3d currentPos = grabbed.getPos();

            // Reduced pull speed for smoother movement:
            double pullSpeed = 0.1;

            double velX = (targetPos.x - currentPos.x) * pullSpeed;
            double velY = (targetPos.y - currentPos.y) * pullSpeed;
            double velZ = (targetPos.z - currentPos.z) * pullSpeed;

            grabbed.setVelocity(velX, velY, velZ);
            grabbed.setNoGravity(true);
            grabbed.fallDistance = 0f;
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack armorStack) {
        if (isBlocked(player)) return;
        if (getCooldown(player) > 0) return;

        UUID playerId = player.getUuid();

        if (isActive(player, this.getId())) {
            FallingBlockEntity grabbed = grabbedBlocks.get(playerId);

            if (grabbed == null || !grabbed.isAlive()) {
                setActive(player, this.getId(), cooldown(player), false);
                grabbedBlocks.remove(playerId);
                return;
            }

            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d launchVelocity = lookVec.multiply(2.5).add(0, 0.2, 0);

            grabbed.setVelocity(launchVelocity);
            grabbed.setNoGravity(false);

            grabbedBlocks.remove(playerId);
            setActive(player, this.getId(), cooldown(player), false);

            return;
        }

        World world = player.getWorld();
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d reachVec = eyePos.add(lookVec.multiply(range(player)));

        BlockHitResult hit = world.raycast(new RaycastContext(
                eyePos, reachVec,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                player
        ));

        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos targetPos = hit.getBlockPos();
        BlockState blockState = world.getBlockState(targetPos);

        if (blockState.isAir() || blockState.getBlock().getHardness() < 0) return;

        if (!world.isClient()) {
            FallingBlockEntity fallingBlock = FallingBlockEntity.spawnFromBlock(world, targetPos, blockState);
            world.removeBlock(targetPos, false);

            fallingBlock.setNoGravity(true);
            fallingBlock.timeFalling = 1;

            grabbedBlocks.put(playerId, fallingBlock);
            setActive(player, this.getId(), cooldown(player), true);
        }
    }

    @Override
    public boolean isBlocked(LivingEntity livingEntity) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        return stack.isEmpty() || !(stack.getItem() instanceof DerpyArmor) || super.isBlocked(livingEntity);
    }

    @Override
    public Entity getTarget(LivingEntity user) {
        return null;
    }
}

