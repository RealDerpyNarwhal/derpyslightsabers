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

    private static final double RADIUS = 13.0;
    private static final int DURATION_TICKS = 20 * 12;
    private static final int TICK_INTERVAL = 20 * 3;
    private static final float DAMAGE_TOTAL = 24f;
    private static final double SHARD_START_OFFSET = 4.0;
    private static final double PARTICLE_RADIUS = 1.5;
    private static final double SPHERE_RADIUS = 20.0;
    private static final int SPHERE_DENSITY = 600;

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
        spawnIceSphere(world, player);
    }

    private void startDamageAndFreezeTask(PlayerEntity player, ServerWorld world, List<TargetState> states) {
        float damagePerTick = DAMAGE_TOTAL / (DURATION_TICKS / TICK_INTERVAL);

        for (int tick = 0; tick < DURATION_TICKS; tick += TICK_INTERVAL) {
            world.getServer().execute(() -> {
                Iterator<TargetState> it = states.iterator();
                while (it.hasNext()) {
                    TargetState ts = it.next();
                    LivingEntity target = ts.entity;
                    if (!target.isAlive() || target.isRemoved()) {
                        it.remove();
                        continue;
                    }

                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, TICK_INTERVAL + 5, 4, false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, TICK_INTERVAL + 5, 2, false, false));
                    target.setVelocity(Vec3d.ZERO);
                    target.velocityModified = true;
                    target.damage(world.getDamageSources().magic(), damagePerTick);

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

    private void spawnIceSphere(ServerWorld world, PlayerEntity player) {
        Vec3d center = player.getPos();

        for (int tick = 0; tick < DURATION_TICKS; tick += 5) {
            world.getServer().execute(() -> {
                for (int i = 0; i < SPHERE_DENSITY; i++) {
                    double theta = world.random.nextDouble() * 2 * Math.PI;
                    double phi = world.random.nextDouble() * Math.PI;
                    double radius = SPHERE_RADIUS * (0.6 + world.random.nextDouble() * 0.4);
                    double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
                    double y = center.y + radius * Math.cos(phi);
                    double z = center.z + radius * Math.sin(phi) * Math.sin(theta);

                    world.spawnParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0, 0, 0, 0.01);
                }
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
