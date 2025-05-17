package net.derpy.mod.item.client;

import net.derpy.mod.item.custom.AnakinsLightsaber;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AnakinsLightsaberRenderer extends GeoItemRenderer<AnakinsLightsaber> {
    public AnakinsLightsaberRenderer() {
        super(new AnakinsLightsaberModel());
    }
}
