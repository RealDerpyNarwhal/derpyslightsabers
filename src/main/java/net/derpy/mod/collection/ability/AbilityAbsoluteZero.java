package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.entity.ModEntities;
import net.derpy.mod.entity.custom.IceShardEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AbilityAbsoluteZero extends ThematicAbility {

    public AbilityAbsoluteZero(String id) {
        super(id, AbilityType.PRESS);
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        if (player.getWorld().isClient()) return; // server only

        ServerWorld world = (ServerWorld) player.getWorld();
        double radius = 6.0; // range around player to hit enemies

        // Find all nearby enemies
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                player.getBoundingBox().expand(radius),
                e -> e.isAlive() && e != player);

        for (LivingEntity target : targets) {
            // Spawn shard 1 block underground
            IceShardEntity shard = new IceShardEntity(ModEntities.ICE_SHARD, world);
            double startY = target.getY() - 1.0; // underground
            double targetY = target.getY() + target.getHeight() / 2.0; // middle of entity
            shard.refreshPositionAndAngles(target.getX(), startY, target.getZ(), 0, 0);
            shard.setTargetY(targetY);

            // Upward velocity
            shard.setVelocity(new Vec3d(0, 0.5, 0));
            shard.velocityModified = true;

            // Owner tracking
            shard.setOwner(player);

            world.spawnEntity(shard);
        }
    }
}
