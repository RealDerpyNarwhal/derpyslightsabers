package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.DerpyArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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

        // Play Force Push sound effect
        player.playSound(SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0F, 1.2F);

        // Push entities in a cone in front of the player
        List<Entity> targets = player.getWorld().getEntitiesByClass(Entity.class, new Box(origin, origin.add(lookVec.multiply(pushRange))).expand(2.0),
                e -> e != player && e.isAlive() && player.canSee(e));

        for (Entity target : targets) {
            Vec3d direction = target.getPos().subtract(player.getPos()).normalize();
            Vec3d velocity = direction.multiply(2.5).add(0, 0.5, 0); // push upward and outward
            target.setVelocity(velocity);
            target.velocityModified = true;

            if (target instanceof LivingEntity living) {
                living.fallDistance = 0f; // prevent fall damage
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