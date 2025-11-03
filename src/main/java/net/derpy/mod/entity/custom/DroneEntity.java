package net.derpy.mod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.EnumSet;
import java.util.UUID;

public class DroneEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private UUID ownerUuid;

    public DroneEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.moveControl = new MoveControl(this);
    }

    /** Sets the player who spawned this drone (server-side!) */
    public void setOwner(PlayerEntity owner) {
        if (owner != null && !getWorld().isClient()) {
            this.ownerUuid = owner.getUuid();
        }
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public static DefaultAttributeContainer.Builder createDroneAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }

    @Override
    protected void initGoals() {
        if (!this.getWorld().isClient()) {
            this.goalSelector.add(1, new DroneAttackGoal(this));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return age + o.hashCode(); // dummy tick value for GeckoLib
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (ownerUuid != null) nbt.putUuid("OwnerUUID", ownerUuid);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) ownerUuid = nbt.getUuid("OwnerUUID");
    }

    // -------------------- Drone AI --------------------
    private static class DroneAttackGoal extends Goal {
        private final DroneEntity drone;
        private LivingEntity target;

        public DroneAttackGoal(DroneEntity drone) {
            this.drone = drone;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            target = findNearestTarget();
            return target != null;
        }

        @Override
        public boolean shouldContinue() {
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) return;

            double dx = target.getX() - drone.getX();
            double dy = target.getBodyY(0.5) - drone.getY();
            double dz = target.getZ() - drone.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Fly toward the target
            if (dist > 0.5) {
                double speed = 0.6;
                drone.setVelocity(dx / dist * speed, dy / dist * speed, dz / dist * speed);
                drone.velocityModified = true;
            }

            // Deal damage when close
            if (dist < 1.5) {
                target.damage(drone.getWorld().getDamageSources().mobAttack(drone), 6.0f);
                target.addVelocity(dx * 0.6, 0.4, dz * 0.6);
                target = null;
            }
        }

        private LivingEntity findNearestTarget() {
            return drone.getWorld().getEntitiesByClass(LivingEntity.class, drone.getBoundingBox().expand(32),
                            e -> e.isAlive()
                                    && e != drone
                                    && !(e instanceof DroneEntity)
                                    && !isOwner(e)
                    ).stream()
                    .min((a, b) -> Double.compare(a.squaredDistanceTo(drone), b.squaredDistanceTo(drone)))
                    .orElse(null);
        }

        private boolean isOwner(LivingEntity entity) {
            return entity != null && entity.getUuid().equals(drone.ownerUuid);
        }
    }
}
