package net.derpy.mod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.damage.DamageSource;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class DroneEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private UUID ownerUuid;
    private PlayerEntity cachedOwner;
    private int lifetimeTicks = 0;

    public DroneEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.moveControl = new MoveControl(this);
    }

    public static DefaultAttributeContainer.Builder createDroneAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    public void setOwner(PlayerEntity owner) {
        if (owner != null && !getWorld().isClient()) {
            this.ownerUuid = owner.getUuid();
            this.cachedOwner = owner;
        }
    }

    public PlayerEntity getOwner() {
        if (cachedOwner == null && ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
            cachedOwner = serverWorld.getPlayerByUuid(ownerUuid);
        }
        return cachedOwner;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (ownerUuid != null) nbt.putUuid("OwnerUUID", ownerUuid);
        nbt.putInt("LifetimeTicks", lifetimeTicks);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) ownerUuid = nbt.getUuid("OwnerUUID");
        if (nbt.contains("LifetimeTicks")) lifetimeTicks = nbt.getInt("LifetimeTicks");
    }

    @Override
    protected void initGoals() {
        if (!this.getWorld().isClient()) {
            this.goalSelector.add(1, new DroneAttackGoal(this));
            this.goalSelector.add(2, new DroneFollowOwnerGoal(this));
        }
    }

    @Override
    public void tick() {
        super.tick();

        lifetimeTicks++;
        if (lifetimeTicks >= 240) {
            this.discard();
            return;
        }

        if (!getWorld().isClient() && cachedOwner == null && ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
            cachedOwner = serverWorld.getPlayerByUuid(ownerUuid);
        }

        if (!getWorld().isClient() && (getOwner() == null || getOwner().isDead())) {
            this.discard();
            return;
        }

        if (getWorld().isClient()) {
            int particleCount = 200;
            double radius = 2.5;
            for (int i = 0; i < particleCount; i++) {
                double theta = random.nextDouble() * 2 * Math.PI;
                double phi = random.nextDouble() * Math.PI;
                double xOffset = radius * Math.sin(phi) * Math.cos(theta);
                double yOffset = radius * Math.sin(phi) * Math.sin(theta);
                double zOffset = radius * Math.cos(phi);
                this.getWorld().addParticle(
                        ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        this.getX() + xOffset,
                        this.getY() + 0.5 + yOffset,
                        this.getZ() + zOffset,
                        0.0, 0.02, 0.0
                );
            }
        }

        if (!getWorld().isClient()) {
            List<Entity> nearby = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(1.0));
            for (Entity e : nearby) {
                if (e instanceof DroneEntity) {
                    double dx = e.getX() - this.getX();
                    double dz = e.getZ() - this.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > 0.0) {
                        double push = 0.05;
                        this.setVelocity(this.getVelocity().add(-dx / dist * push, 0, -dz / dist * push));
                        this.velocityModified = true;
                    }
                }
            }
        }
    }

    private static class DroneAttackGoal extends Goal {
        private final DroneEntity drone;
        private LivingEntity target;

        public DroneAttackGoal(DroneEntity drone) {
            this.drone = drone;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (target == null || !target.isAlive()) target = findNearestTarget();
            return target != null;
        }

        @Override
        public boolean shouldContinue() {
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) {
                target = findNearestTarget();
                if (target == null) return;
            }

            double dx = target.getX() - drone.getX();
            double dy = target.getBodyY(0.5) - drone.getY();
            double dz = target.getZ() - drone.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > 0.5) {
                double speed = 0.6;
                drone.setVelocity(dx / dist * speed, dy / dist * speed, dz / dist * speed);
                drone.velocityModified = true;
            }

            if (dist < 1.5) {
                if (!isOwner(target)) {
                    target.damage(drone.getWorld().getDamageSources().mobAttack(drone), 12.0f);
                }

                if (drone.getWorld() instanceof ServerWorld serverWorld && !isOwner(target)) {
                    serverWorld.spawnParticles(
                            ParticleTypes.ELECTRIC_SPARK,
                            target.getX(), target.getY() + 1, target.getZ(),
                            30, 0.4, 0.4, 0.4, 0.1
                    );
                }

                List<LivingEntity> nearby = drone.getWorld().getEntitiesByClass(
                        LivingEntity.class,
                        drone.getBoundingBox().expand(3.5),
                        e -> e.isAlive() && e != drone && !isOwner(e)
                );

                for (LivingEntity entity : nearby) {
                    if (drone.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(
                                ParticleTypes.ELECTRIC_SPARK,
                                entity.getX(), entity.getY() + 1, entity.getZ(),
                                10, 0.2, 0.2, 0.2, 0.05
                        );
                    }
                }
            }
        }

        private LivingEntity findNearestTarget() {
            return drone.getWorld().getEntitiesByClass(LivingEntity.class, drone.getBoundingBox().expand(32),
                            e -> e.isAlive() && e != drone && !(e instanceof DroneEntity) && !isOwner(e))
                    .stream().min((a, b) -> Double.compare(a.squaredDistanceTo(drone), b.squaredDistanceTo(drone)))
                    .orElse(null);
        }

        private boolean isOwner(LivingEntity entity) {
            PlayerEntity owner = drone.getOwner();
            return entity != null && owner != null && entity.getUuid().equals(owner.getUuid());
        }
    }

    private static class DroneFollowOwnerGoal extends Goal {
        private final DroneEntity drone;

        public DroneFollowOwnerGoal(DroneEntity drone) {
            this.drone = drone;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            PlayerEntity owner = drone.getOwner();
            return owner != null && !owner.isDead();
        }

        @Override
        public void tick() {
            PlayerEntity owner = drone.getOwner();
            if (owner == null) return;
            double desiredDist = 3.0;
            double dx = owner.getX() - drone.getX();
            double dy = owner.getY() + 1.5 - drone.getY();
            double dz = owner.getZ() - drone.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > desiredDist) {
                double speed = 0.35;
                drone.setVelocity(dx / dist * speed, dy / dist * speed, dz / dist * speed);
                drone.velocityModified = true;
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean isPushable() { return false; }
    @Override
    public boolean isCollidable() { return false; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override
    public double getTick(Object o) { return age + o.hashCode(); }
}
