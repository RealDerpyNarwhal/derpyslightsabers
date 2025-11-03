package net.derpy.mod.collection.ability;

import bond.thematic.api.registries.armors.ability.DefaultOptions;
import bond.thematic.api.registries.armors.ability.ThematicAbility;
import net.derpy.mod.entity.ModEntities;
import net.derpy.mod.entity.custom.DroneEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityDroneSwarm extends ThematicAbility {

    private static final int    NUM_DRONES       = 10;
    private static final double ORBIT_RADIUS     = 3.0;
    private static final double ORBIT_HEIGHT     = 2.0;
    private static final double ORBIT_SPEED_RADS = Math.toRadians(10);
    private static final double ATTACK_SPEED     = 1.6;
    private static final double ATTACK_RANGE_SQ  = 64.0;
    private static final double SEARCH_RANGE     = 12.0;
    private static final int    LIFESPAN_TICKS   = 600;
    private static final float  DAMAGE_ON_HIT    = 12.0f; // buffed damage
    private static final double ORBIT_SMOOTH     = 0.5;

    private static class DroneState {
        final DroneEntity drone;
        double angle;
        int ticksAlive;

        DroneState(DroneEntity drone, double startAngle) {
            this.drone = drone;
            this.angle = startAngle;
            this.ticksAlive = 0;
        }
    }

    private final Map<UUID, List<DroneState>> swarms = new ConcurrentHashMap<>();

    public AbilityDroneSwarm(String id) {
        super(id, AbilityType.PRESS);
    }

    @Override
    public void press(PlayerEntity player, ItemStack stack) {
        super.press(player, stack);
        if (player.getWorld().isClient() || getCooldown(player) > 0) return;
        spawnSwarm(player);
        setCooldown(player, cooldown(player));
    }

    private void spawnSwarm(PlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        removeSwarm(player);
        List<DroneState> list = new ArrayList<>(NUM_DRONES);
        double base = player.getRandom().nextDouble() * Math.PI * 2;
        Vec3d center = player.getPos().add(0, ORBIT_HEIGHT, 0);

        for (int i = 0; i < NUM_DRONES; i++) {
            double angle = base + i * (2 * Math.PI / NUM_DRONES);
            float cos = MathHelper.cos((float) angle);
            float sin = MathHelper.sin((float) angle);
            Vec3d pos = center.add(cos * ORBIT_RADIUS, 0, sin * ORBIT_RADIUS);

            DroneEntity drone = ModEntities.DRONE.create(world);
            if (drone == null) continue;

            drone.setPosition(pos.x, pos.y, pos.z);
            drone.setNoGravity(true);
            drone.setVelocity(Vec3d.ZERO);

            drone.setOwner(player);

            world.spawnEntity(drone);
            list.add(new DroneState(drone, angle));
        }
        swarms.put(player.getUuid(), list);
    }

    public void tick(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        ServerWorld world = (ServerWorld) player.getWorld();
        List<DroneState> list = swarms.get(player.getUuid());
        if (list == null || list.isEmpty()) return;
        Vec3d center = player.getPos().add(0, ORBIT_HEIGHT, 0);
        Iterator<DroneState> it = list.iterator();
        while (it.hasNext()) {
            DroneState ds = it.next();
            DroneEntity drone = ds.drone;
            if (!drone.isAlive() || drone.isRemoved()) { it.remove(); continue; }

            ds.ticksAlive++;
            ds.angle += ORBIT_SPEED_RADS;
            float cos = MathHelper.cos((float) ds.angle);
            float sin = MathHelper.sin((float) ds.angle);
            Vec3d orbit = center.add(cos * ORBIT_RADIUS, 0, sin * ORBIT_RADIUS);
            Vec3d toOrbit = orbit.subtract(drone.getPos()).multiply(ORBIT_SMOOTH);
            drone.setVelocity(toOrbit);
            drone.velocityModified = true;
            drone.setNoGravity(true);

            LivingEntity target = nearestEnemy(world, player, drone);
            if (target != null && target.isAlive()) {
                double distSq = drone.squaredDistanceTo(target);
                if (distSq <= ATTACK_RANGE_SQ) {
                    Vec3d dir = target.getEyePos().subtract(drone.getPos()).normalize();
                    drone.setVelocity(dir.multiply(ATTACK_SPEED));
                    drone.velocityModified = true;
                    if (distSq <= 1.5) {
                        target.damage(world.getDamageSources().mobAttack(drone), DAMAGE_ON_HIT); // buffed
                        // No knockback
                        drone.discard();
                        it.remove();
                        continue;
                    }
                }
            }

            if (ds.ticksAlive > LIFESPAN_TICKS) {
                drone.discard();
                it.remove();
            }
        }

        if (list.isEmpty()) swarms.remove(player.getUuid());
    }

    private LivingEntity nearestEnemy(ServerWorld world, PlayerEntity owner, DroneEntity drone) {
        Box box = drone.getBoundingBox().expand(SEARCH_RANGE);
        List<LivingEntity> candidates = world.getEntitiesByClass(LivingEntity.class, box, e ->
                e.isAlive() && e != drone && !(e instanceof DroneEntity) &&
                        !owner.getUuid().equals(e.getUuid()) // ignore owner
        );
        if (candidates.isEmpty()) return null;
        candidates.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(drone)));
        return candidates.get(0);
    }

    public void removeSwarm(PlayerEntity player) {
        List<DroneState> list = swarms.remove(player.getUuid());
        if (list == null) return;
        for (DroneState ds : list)
            if (ds.drone.isAlive() && !ds.drone.isRemoved()) ds.drone.discard();
    }

    @Override
    public DefaultOptions getDefaultData() {
        return new DefaultOptions.Builder()
                .cooldown(100)
                .amplifier(NUM_DRONES)
                .damage(DAMAGE_ON_HIT)
                .build();
    }
}
