package net.derpy.mod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AstromechEntity extends TameableEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AstromechEntity(EntityType<? extends TameableEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAstromechAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.5F, true)); // was 1.25
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("sit");

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> state) {
        if (this.isRemoved() || this.isDead()) return PlayState.STOP;

        if (this.isSitting()) {
            state.getController().setAnimation(SIT_ANIM);
        } else if (this.isOnGround() && this.getVelocity().horizontalLengthSquared() > 1.0E-5) {
            state.getController().setAnimation(WALK_ANIM);
        } else {
            state.getController().setAnimation(IDLE_ANIM);
        }

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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (this.isTamed()) {
            if (this.isOwner(player) && !this.getWorld().isClient()) {
                this.setSitting(!this.isSitting());
                return ActionResult.success(this.getWorld().isClient());
            }
        } else if (stack.getItem() == Items.IRON_INGOT) {
            if (!player.getAbilities().creativeMode) stack.decrement(1);

            if (!this.getWorld().isClient()) {
                if (this.random.nextInt(3) == 0) {
                    this.setOwnerUuid(player.getUuid());
                    this.setTamed(true);
                    this.getWorld().sendEntityStatus(this, (byte) 7); // hearts
                } else {
                    this.getWorld().sendEntityStatus(this, (byte) 6); // smoke
                }
            }

            return ActionResult.success(this.getWorld().isClient());
        }

        return super.interactMob(player, hand);
    }

    // === Miscellaneous ===
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 EntityData entityData, NbtCompound entityTag) {
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.6f, 1.2f);
    }

    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound(); // Replace with your custom astromech beep sound
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        return null;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
}
