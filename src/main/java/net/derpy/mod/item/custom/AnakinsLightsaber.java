package net.derpy.mod.item.custom;

import bond.thematic.api.registries.item.IAttackUse;
import com.funalex.themAnim.api.layered.AnimationStack;
import com.funalex.themAnim.api.layered.KeyframeAnimationPlayer;
import com.funalex.themAnim.core.data.KeyframeAnimation;
import com.funalex.themAnim.minecraftApi.PlayerAnimationAccess;
import com.funalex.themAnim.minecraftApi.PlayerAnimationRegistry;
import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.item.client.AnakinsLightsaberRenderer;
import net.derpy.mod.util.LightingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnakinsLightsaber extends Item implements GeoItem, IAttackUse {
    private static final RawAnimation IGNITE_ANIM = RawAnimation.begin().then("ignite", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation RETRACT_ANIM = RawAnimation.begin().then("retract", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation BLOCK_ANIM = RawAnimation.begin().then("block", Animation.LoopType.LOOP);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    // Used to toggle between swing_saber and swing_saber_2
    private static boolean lastSwingWasFirst = false;

    public AnakinsLightsaber(Settings settings) {
        super(settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            long id = GeoItem.getOrAssignId(stack, serverWorld);
            boolean isOn = stack.getOrCreateNbt().getBoolean("LightsaberOn");

            if (user.isSneaking()) {
                if (isOn) {
                    // Lightsaber ON + Shift + Right Click = retract
                    triggerAnim(user, id, "controller", "retract");
                    stack.getOrCreateNbt().putBoolean("LightsaberOn", false);
                    System.out.println("[AnakinsLightsaber] Shift + Right Click -> RETRACT");
                } else {
                    // Lightsaber OFF + Shift + Right Click = ignite
                    triggerAnim(user, id, "controller", "ignite");
                    stack.getOrCreateNbt().putBoolean("LightsaberOn", true);
                    System.out.println("[AnakinsLightsaber] Shift + Right Click -> IGNITE");
                }
                return TypedActionResult.success(stack, world.isClient());
            } else {
                // Not sneaking: start blocking and play block animation
                triggerAnim(user, id, "controller", "block");
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public boolean attackUse(World world, PlayerEntity playerEntity, Hand hand) {
        if (world.isClient) {
            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) playerEntity);

            if (animationStack != null) {
                // Alternate between "swing_saber" and "swing_saber_2"
                String animName = lastSwingWasFirst ? "swing_saber_2" : "swing_saber";
                lastSwingWasFirst = !lastSwingWasFirst;

                Identifier animationLocation = new Identifier(Derpyslightsabers.MOD_ID, animName);
                KeyframeAnimation animation = PlayerAnimationRegistry.getAnimation(animationLocation);

                if (animation != null) {
                    KeyframeAnimationPlayer animPlayer = new KeyframeAnimationPlayer(animation);
                    animationStack.addAnimLayer(5, animPlayer);
                } else {
                    System.err.println("[AnakinsLightsaber] Animation not found: " + animName);
                }
            }
        }
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        // Glow effect
        boolean isOn = stack.getOrCreateNbt().getBoolean("LightsaberOn");
        LightingHelper.tick(entity, isOn);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>(this, "controller", 0, this::predicate)
                        .triggerableAnim("ignite", IGNITE_ANIM)
                        .triggerableAnim("retract", RETRACT_ANIM)
                        .triggerableAnim("block", BLOCK_ANIM)
        );
    }

    private PlayState predicate(AnimationState<AnakinsLightsaber> state) {
        return PlayState.STOP; // Prevent auto-playing animations
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final AnakinsLightsaberRenderer renderer = new AnakinsLightsaberRenderer();

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
