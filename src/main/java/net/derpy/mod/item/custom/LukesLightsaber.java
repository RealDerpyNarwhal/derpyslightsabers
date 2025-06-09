package net.derpy.mod.item.custom;

import net.derpy.mod.item.client.LukesLightsaberRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import software.bernie.geckolib.animatable.client.RenderProvider;

import java.util.function.Consumer;

public class LukesLightsaber extends BaseLightsaber {
    public LukesLightsaber(Settings settings) {
        super(settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final LukesLightsaberRenderer renderer = new LukesLightsaberRenderer();

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
