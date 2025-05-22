package net.derpy.mod.collection.ability;

import bond.thematic.api.abilities.weapon.effect.AbilityShieldThrow;
import bond.thematic.api.registries.item.ThematicWeapon;
import net.derpy.mod.item.ModItems;
import net.derpy.mod.item.custom.AnakinsLightsaber;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AbilityThrowSaber extends AbilityShieldThrow {
    public AbilityThrowSaber(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    public void press(PlayerEntity playerEntity, ItemStack armorStack) {
        super.press(playerEntity, armorStack);
        World var4 = playerEntity.getWorld();

        if (var4 instanceof ServerWorld world) {
            if (this.getCooldown(playerEntity) <= 0) {
                ItemStack weapon = playerEntity.getEquippedStack(EquipmentSlot.MAINHAND);
                Item item = weapon.getItem();

                if (item instanceof AnakinsLightsaber lightsaber) {
                    // Base your entity off of the trident or shield
                    //EntityLightsaber tridentEntity = (EntityShieldThrown)lightsaber.createThrowable(world, weapon, playerEntity);
                    //tridentEntity.throwShield(playerEntity, weapon);
                    playerEntity.getInventory().removeOne(weapon);
                    this.incrementCooldown(playerEntity, this.cooldown(playerEntity));

                    playerEntity.sendMessage(Text.of("THROWN "), true);
                }

            }
        }
    }

    @Override
    public boolean isBlocked(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return false;
        }

        ItemStack mainHandStack = livingEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (mainHandStack == null) {
            return false;
        }

        Item mainHandItem = mainHandStack.getItem();

        return !(mainHandItem instanceof AnakinsLightsaber);
    }
}
