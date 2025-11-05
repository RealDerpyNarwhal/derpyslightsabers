package net.derpy.mod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.MovementType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class IceShardEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private LivingEntity owner;
    private double startY;
    private boolean effectApplied = false;

    public IceShardEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setInvisible(true);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return owner;
    }

    public void setStartY(double y) {
        this.startY = y;
    }

    public static DefaultAttributeContainer.Builder createIceShardAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1);
    }

    @Override
    protected void initGoals() {}

    @Override
    public void tick() {
        super.tick();
        this.noClip = true;
        this.setInvisible(false);
        if (getWorld().isClient()) return;

        ServerWorld serverWorld = (ServerWorld) getWorld();

        double groundY = getY();
        for (int i = 0; i < 10; i++) {
            if (!getWorld().isAir(getBlockPos().down(i))) {
                groundY = getBlockY() - i;
                break;
            }
        }

        double targetY = groundY + 1.0;

        if (getY() < targetY) {
            this.setVelocity(0, 0.25, 0);
            this.move(MovementType.SELF, getVelocity());
        } else {
            if (!effectApplied) {
                effectApplied = true;
                serverWorld.spawnParticles(ParticleTypes.SNOWFLAKE, getX(), getY(), getZ(), 20, 1, 1, 1, 0.1);
                serverWorld.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.PLAYERS, 1.0f, 0.8f);
            }
            this.setVelocity(0, 0, 0);
        }

        applyFreezeEffect();

        if (this.age > 20 * 12) {
            resetFrozenEntities();
            this.discard();
        }
    }

    private void applyFreezeEffect() {
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;

        double radius = 1.5;
        List<LivingEntity> targets = getWorld().getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(radius),
                e -> e.isAlive() && e != owner);

        for (LivingEntity target : targets) {
            target.setVelocity(0, 0, 0);
            target.velocityModified = true;
            target.noClip = true;
            target.setNoGravity(true);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 10, false, false));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 10, false, false));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 60, 250, false, false));
            if (target instanceof PathAwareEntity mob) {
                mob.getNavigation().stop();
                mob.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 0);
            }
        }

        for (int i = 0; i < 3; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 1.5;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 1.5;
            serverWorld.spawnParticles(ParticleTypes.ITEM_SNOWBALL, getX() + offsetX, getY() + offsetY, getZ() + offsetZ, 1, 0, 0, 0, 0);
        }
    }

    private void resetFrozenEntities() {
        List<LivingEntity> targets = getWorld().getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(1.5),
                e -> e.isAlive() && e != owner);

        for (LivingEntity target : targets) {
            target.noClip = false;
            target.setNoGravity(false);

            target.setVelocity(0, -0.1, 0);
            target.velocityModified = true;

            target.removeStatusEffect(StatusEffects.SLOWNESS);
            target.removeStatusEffect(StatusEffects.MINING_FATIGUE);
            target.removeStatusEffect(StatusEffects.JUMP_BOOST);

            if (target instanceof PathAwareEntity mob) {
                mob.getNavigation().startMovingTo(mob.getX(), mob.getY(), mob.getZ(), 1.0);
            }
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return age;
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, net.minecraft.entity.damage.DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    protected void pushAway(Entity entity) {}
}
