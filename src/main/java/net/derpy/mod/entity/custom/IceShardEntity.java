package net.derpy.mod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import net.minecraft.entity.MovementType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.damage.DamageSource;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class IceShardEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private LivingEntity owner;
    private boolean reachedTarget = false;
    private double targetY; // where the shard should stop (middle of entity)

    public IceShardEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setInvisible(true); // start underground
    }

    public void setOwner(LivingEntity owner) { this.owner = owner; }
    public LivingEntity getOwner() { return owner; }

    public void setTargetY(double y) { this.targetY = y; }

    public static DefaultAttributeContainer.Builder createIceShardAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1);
    }

    @Override
    protected void initGoals() { }

    @Override
    public void tick() {
        super.tick();

        if (!reachedTarget) {
            // Rise slowly
            this.setVelocity(0, 0.5, 0);
            this.move(MovementType.SELF, getVelocity());

            // Stop when the shard's bottom reaches targetY
            if (this.getY() >= targetY) {
                this.setVelocity(0, 0, 0);
                reachedTarget = true;
                this.setInvisible(false); // become visible
            }
        } else {
            // Spawn particles above surface
            if (getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                        getX(), getY(), getZ(), 2,
                        0.1, 0.1, 0.1, 0.0);
            }

            // Deal AoE damage once
            if (this.age == 1) {
                dealAoEDamage();
            }
        }
    }

    private void dealAoEDamage() {
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;

        double radius = 1.5;
        List<LivingEntity> targets = getWorld().getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(radius),
                e -> e.isAlive() && e != owner);

        for (LivingEntity target : targets) {
            target.damage(getWorld().getDamageSources().magic(), 6.0f);
            target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SLOWNESS, 60, 2));
        }

        // Impact particles
        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 1.5;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 1.5;
            serverWorld.spawnParticles(ParticleTypes.ITEM_SNOWBALL,
                    getX() + offsetX, getY() + offsetY, getZ() + offsetZ,
                    1, 0, 0, 0, 0);
        }

        // Impact sound
        serverWorld.playSound(null, getX(), getY(), getZ(),
                net.minecraft.sound.SoundEvents.BLOCK_GLASS_BREAK,
                net.minecraft.sound.SoundCategory.PLAYERS,
                1.0f, 1.0f);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) { }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public double getTick(Object o) { return age; }

    @Override
    public boolean damage(DamageSource source, float amount) { return false; }
    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) { return false; }
    @Override
    public boolean isPushable() { return false; }
    @Override
    public boolean isCollidable() { return false; }
}
