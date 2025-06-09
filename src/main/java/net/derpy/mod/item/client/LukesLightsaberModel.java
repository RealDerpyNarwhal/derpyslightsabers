package net.derpy.mod.item.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.item.custom.LukesLightsaber;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class LukesLightsaberModel extends GeoModel<LukesLightsaber> {
    @Override
    public Identifier getModelResource(LukesLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "geo/lukes_lightsaber.geo.json");
    }

    @Override
    public Identifier getTextureResource(LukesLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "textures/item/lukes_lightsaber.png");
    }

    @Override
    public Identifier getAnimationResource(LukesLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "animations/lukes_lightsaber.animation.json");
    }
}
