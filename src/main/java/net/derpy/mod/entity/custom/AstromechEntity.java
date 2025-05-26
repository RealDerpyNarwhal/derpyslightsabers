package net.derpy.mod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AstromechEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AstromechEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAstromechAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 EntityData entityData, NbtCompound entityTag) {
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.astromech.idle");

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> state) {
        state.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound(); // You can return a custom sound here
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source); // Or your custom sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound(); // Or your custom sound
    }

    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.6f, 1.2f);
    }
}
