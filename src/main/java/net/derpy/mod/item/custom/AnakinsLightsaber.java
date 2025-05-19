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

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AnakinsLightsaber extends Item implements GeoItem {
    private static final RawAnimation IGNITE_ANIM = RawAnimation.begin().then("ignite", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation RETRACT_ANIM = RawAnimation.begin().then("retract", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final long DOUBLE_CLICK_THRESHOLD_MS = 300; // 300ms window for double sneak

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    // Stores last sneak-right-click time per player
    private static final HashMap<UUID, Long> lastSneakClick = new HashMap<>();

    public AnakinsLightsaber(Settings settings) {
        super(settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            UUID playerId = user.getUuid();
            long currentTime = System.currentTimeMillis();

            if (user.isSneaking()) {
                long lastClick = lastSneakClick.getOrDefault(playerId, 0L);
                long timeSinceLast = currentTime - lastClick;
                lastSneakClick.put(playerId, currentTime); // update every time

                long id = GeoItem.getOrAssignId(stack, serverWorld);

                if (timeSinceLast < DOUBLE_CLICK_THRESHOLD_MS) {
                    // Double sneak-click -> retract
                    triggerAnim(user, id, "controller", "retract");
                    System.out.println("[AnakinsLightsaber] Double sneak-click -> RETRACT");
                } else {
                    // Single sneak-click -> ignite
                    triggerAnim(user, id, "controller", "ignite");
                    System.out.println("[AnakinsLightsaber] Single sneak-click -> IGNITE");
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
        return PlayState.STOP;
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
