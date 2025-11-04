package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.DefaultOptions;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.entity.ModEntities;
import net.derpy.mod.entity.custom.IceShardEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityAbsoluteZero extends ThematicAbility {

    private static final double RADIUS = 6.0;
    private static final int DURATION_TICKS = 20 * 12; // 12 seconds
    private static final int TICK_INTERVAL = 20 * 3; // every 3 seconds
    private static final float DAMAGE_TOTAL = 24f; // 12 hearts
    private static final double SHARD_START_OFFSET = 4.0;
    private static final double PARTICLE_RADIUS = 1.5;

    private static class TargetState {
        final LivingEntity entity;
        int ticksElapsed;

        TargetState(LivingEntity entity) {
            this.entity = entity;
            this.ticksElapsed = 0;
        }
    }

    private final Map<UUID, List<TargetState>> frozenTargets = new ConcurrentHashMap<>();

    public AbilityAbsoluteZero(String id) {
        super(id, AbilityType.PRESS);
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        super.press(player, stack);
        if (player.getWorld().isClient() || getCooldown(player) > 0) return;

        ServerWorld world = (ServerWorld) player.getWorld();

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                player.getBoundingBox().expand(RADIUS),
                e -> e.isAlive() && e != player);

        List<TargetState> states = new ArrayList<>();

        for (LivingEntity target : targets) {
            states.add(new TargetState(target));

            IceShardEntity shard = new IceShardEntity(ModEntities.ICE_SHARD, world);
            double startY = target.getY() - SHARD_START_OFFSET;
            shard.refreshPositionAndAngles(target.getX(), startY, target.getZ(), 0, 0);
            shard.setStartY(startY);
            shard.setOwner(player);
            shard.setNoGravity(true);
            shard.noClip = true;
            world.spawnEntity(shard);
        }

        frozenTargets.put(player.getUuid(), states);
        setCooldown(player, cooldown(player));

        startDamageAndFreezeTask(player, world, states);
    }

    private void startDamageAndFreezeTask(PlayerEntity player, ServerWorld world, List<TargetState> states) {
        float damagePerTick = DAMAGE_TOTAL / (DURATION_TICKS / TICK_INTERVAL); // split total damage over intervals

        for (int tick = 0; tick < DURATION_TICKS; tick += TICK_INTERVAL) {
            int delay = tick;

            world.getServer().execute(() -> {
                Iterator<TargetState> it = states.iterator();
                while (it.hasNext()) {
                    TargetState ts = it.next();
                    LivingEntity target = ts.entity;
                    if (!target.isAlive() || target.isRemoved()) {
                        it.remove();
                        continue;
                    }

                    // Apply blindness
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, TICK_INTERVAL + 5, 0, false, false));

                    // Freeze in place
                    target.setVelocity(Vec3d.ZERO);
                    target.velocityModified = true;

                    // Deal damage
                    target.damage(world.getDamageSources().magic(), damagePerTick);

                    // Spawn ice particles
                    Vec3d pos = target.getPos().add(0, target.getHeight() / 2.0, 0);
                    for (int i = 0; i < 6; i++) {
                        double offsetX = (world.random.nextDouble() - 0.5) * PARTICLE_RADIUS;
                        double offsetY = (world.random.nextDouble() - 0.5) * PARTICLE_RADIUS;
                        double offsetZ = (world.random.nextDouble() - 0.5) * PARTICLE_RADIUS;
                        world.spawnParticles(ParticleTypes.SNOWFLAKE,
                                pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                                1, 0, 0, 0, 0);
                    }
                }
                if (states.isEmpty()) frozenTargets.remove(player.getUuid());
            });
        }
    }

    @Override
    public DefaultOptions getDefaultData() {
        return new DefaultOptions.Builder()
                .cooldown(30)
                .build();
    }
}
