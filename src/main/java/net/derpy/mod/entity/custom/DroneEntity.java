package net.derpy.mod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DroneEntity extends FlyingEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DroneEntity(EntityType<? extends FlyingEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 10, true); // smooth flying
        this.setNoGravity(true); // hover in air
    }

    // Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return FlyingEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6);
    }

    // AI goals: simple hover/look
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAroundGoal(this)); // optional, for natural movement
    }

    // GeckoLib animation
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-5) {
            state.setAnimation(FLY);
        } else {
            state.setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.getVelocity().horizontalLengthSquared() < 1.0E-5) {
            double dx = (this.random.nextDouble() - 0.5) * 0.5;
            double dy = (this.random.nextDouble() - 0.5) * 0.5;
            double dz = (this.random.nextDouble() - 0.5) * 0.5;
            this.setVelocity(dx, dy, dz);
        }
    }
}
