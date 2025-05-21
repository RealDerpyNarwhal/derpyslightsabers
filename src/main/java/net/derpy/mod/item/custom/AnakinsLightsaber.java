package net.derpy.mod.item.custom;

import net.derpy.mod.item.client.AnakinsLightsaberRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
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

public class AnakinsLightsaber extends Item implements GeoItem {
    private static final RawAnimation IGNITE_ANIM = RawAnimation.begin().then("ignite", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation RETRACT_ANIM = RawAnimation.begin().then("retract", Animation.LoopType.HOLD_ON_LAST_FRAME);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

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
            }
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>(this, "controller", 0, this::predicate)
                        .triggerableAnim("ignite", IGNITE_ANIM)
                        .triggerableAnim("retract", RETRACT_ANIM)
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
