package net.derpy.mod.collection.ability;

import bond.thematic.api.callbacks.LivingTickCallback;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.collection.armor.PsychicArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityMindCrush extends ThematicAbility {

    private final Map<UUID, Integer> activeDurations = new HashMap<>();
    private final Map<UUID, Integer> damageCooldowns = new HashMap<>();

    public AbilityMindCrush(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register((livingEntity) -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof PsychicArmor)) return;

            if (!isActive(player, this.getId())) {
                activeDurations.remove(player.getUuid());
                damageCooldowns.remove(player.getUuid());
                return;
            }

            UUID id = player.getUuid();
            int duration = activeDurations.getOrDefault(id, 0);

            if (duration <= 0) {
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(id);
                damageCooldowns.remove(id);
                return;
            } else {
                activeDurations.put(id, duration - 1);
            }

            Entity target = getTarget(player);
            if (!(target instanceof LivingEntity livingTarget)) {
                setActive(player, this.getId(), cooldown(player), false);
                activeDurations.remove(id);
                damageCooldowns.remove(id);
                return;
            }

            int cd = damageCooldowns.getOrDefault(id, 0);
            if (cd <= 0) {
                livingTarget.damage(livingTarget.getDamageSources().magic(), 3.5f);
                livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40, 1));
                livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 2));

                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.8f, 1.4f);

                damageCooldowns.put(id, 20);
            } else {
                damageCooldowns.put(id, cd - 1);
            }
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        if (isBlocked(player) || this.getCooldown(player) > 0) return;

        Entity target = this.getTarget(player);
        if (!(target instanceof LivingEntity)) return;

        activeDurations.put(player.getUuid(), 100); // lasts 5 seconds
        damageCooldowns.put(player.getUuid(), 0);
        setActive(player, this.getId(), cooldown(player), true);
    }

    @Override
    public boolean isBlocked(LivingEntity user) {
        Entity target = this.getTarget(user);
        if (target == null || target.distanceTo(user) > range(user)) return true;

        ItemStack armorStack = user.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof PsychicArmor)) return true;

        return super.isBlocked(user);
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
