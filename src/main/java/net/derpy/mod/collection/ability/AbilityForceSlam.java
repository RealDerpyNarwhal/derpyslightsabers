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

public class AbilityForceSlam extends ThematicAbility {

    private final Map<UUID, Map<UUID, Integer>> slamTicks = new HashMap<>();

    public AbilityForceSlam(String abilityId) {
        super(abilityId, AbilityType.PRESS);
    }

    @Override
    public void serverEvents() {
        super.serverEvents();

        LivingTickCallback.EVENT.register((livingEntity) -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            ItemStack armorStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return;

            if (!isActive(player, this.getId())) {
                slamTicks.remove(player.getUuid());
                return;
            }

            UUID playerId = player.getUuid();

            double radius = 10.0;
            var targets = player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius),
                    e -> e != player && e.isAlive() && player.canSee(e));

            Map<UUID, Integer> playerSlamTicks = slamTicks.computeIfAbsent(playerId, k -> new HashMap<>());

            for (LivingEntity target : targets) {
                UUID targetId = target.getUuid();
                int ticks = playerSlamTicks.getOrDefault(targetId, 0);

                if (ticks < 20) {
                    target.setVelocity(0, 0.5, 0); // reduced upward velocity for ~10 blocks height
                    target.fallDistance = 0;
                    playerSlamTicks.put(targetId, ticks + 1);
                } else if (ticks == 20) {
                    target.setVelocity(0, -4, 0);
                    target.damage(target.getDamageSources().mobAttack(player), 20.0f); // 10 hearts damage
                    playerSlamTicks.put(targetId, ticks + 1);
                }
            }

            boolean allSlamsDone = playerSlamTicks.values().stream().allMatch(t -> t > 20);
            if (allSlamsDone) {
                setActive(player, this.getId(), cooldown(player), false);
                slamTicks.remove(playerId);
            }
        });
    }

    @Override
    public void press(PlayerEntity player, ItemStack armorStack) {
        super.press(player, armorStack);

        if (isBlocked(player)) return;
        if (getCooldown(player) > 0) return;

        slamTicks.put(player.getUuid(), new HashMap<>());
        setActive(player, getId(), cooldown(player), true);
    }

    @Override
    public boolean isBlocked(LivingEntity user) {
        double radius = 10.0;
        var targets = user.getWorld().getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(radius),
                e -> e != user && e.isAlive() && user.canSee(e));

        if (targets.isEmpty()) return true;

        ItemStack armorStack = user.getEquippedStack(EquipmentSlot.CHEST);
        if (armorStack.isEmpty() || !(armorStack.getItem() instanceof DerpyArmor)) return true;

        return super.isBlocked(user);
    }

    @Override
    public Entity getTarget(LivingEntity user) {
        return null;
    }
}
