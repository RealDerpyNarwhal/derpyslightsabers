package net.derpy.mod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class DroneEntity extends PathAwareEntity implements GeoAnimatable {

    private PlayerEntity owner;

    public DroneEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    // AI Goals
    @Override
    protected void initGoals() {
        // Melee attack for combat
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, true));

        // Wander around
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));

        // Look at nearby players
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));

        // Target nearby entities
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, MobEntity.class, true));
    }

    // Attributes
    public static DefaultAttributeContainer.Builder createDroneAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }

    // Owner methods
    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public PlayerEntity getOwner() {
        return owner;
    }

    public boolean canTarget(PlayerEntity target) {
        // Optional: prevent attacking the owner
        return super.canTarget(target) && (owner == null || target != owner);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
