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
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnakinsLightsaber extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public AnakinsLightsaber(Settings settings) {
        super(settings);
        GeoItem.registerSyncedAnimatable(this);
    }

    public void playIgniteAnimation(PlayerEntity user, ItemStack stack, World world) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            long animId = GeoItem.getOrAssignId(stack, serverWorld);
            System.out.println("[AnakinsLightsaber] Anim ID: " + animId);

            if (animId > 0) {
                GeoItem animatable = (GeoItem) stack.getItem();
                System.out.println("[AnakinsLightsaber] Triggering 'ignite' animation");
                animatable.triggerAnim(user, animId, "controller", "ignite");
            } else {
                System.out.println("[AnakinsLightsaber] Failed to assign animation ID for stack: " + stack);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.isSneaking()) {
            playIgniteAnimation(user, stack, world);
            return TypedActionResult.success(stack, world.isClient());
        }

        return TypedActionResult.pass(stack);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<AnakinsLightsaber> state) {
        AnimationController<?> controller = state.getController();
        // Default to not playing animation unless one is triggered
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
