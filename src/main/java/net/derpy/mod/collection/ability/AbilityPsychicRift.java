package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AbilityPsychicRift extends ThematicAbility {

    public AbilityPsychicRift(String name) {
        super(name, AbilityType.PRESS);
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        if (isBlocked(player) || getCooldown(player) > 0) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 2.0F, 1.2F);

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(10.0D),
                e -> e != player && e.isAlive()
        );

        for (LivingEntity entity : nearby) {
            Vec3d direction = entity.getPos().subtract(player.getPos()).normalize().multiply(2.5);
            entity.addVelocity(direction.x, 1.0, direction.z);
            entity.velocityModified = true;
            entity.damage(world.getDamageSources().magic(), 4.0F);
        }

        // Cooldown
        setCooldown(player, 160);
    }
}
