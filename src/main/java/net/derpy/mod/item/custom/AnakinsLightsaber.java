package net.derpy.mod.item.custom;

import net.derpy.mod.item.client.AnakinsLightsaberRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import software.bernie.geckolib.animatable.client.RenderProvider;

import java.util.function.Consumer;

public class AnakinsLightsaber extends BaseLightsaber {
    public AnakinsLightsaber(Settings settings) {
        super(settings);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private final AnakinsLightsaberRenderer renderer = new AnakinsLightsaberRenderer();

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
