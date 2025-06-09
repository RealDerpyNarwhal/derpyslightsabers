// AnakinsLightsaberModel.java
package net.derpy.mod.item.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.item.custom.AnakinsLightsaber;
import net.minecraft.util.Identifier;

public class AnakinsLightsaberModel extends BaseLightsaberModel<AnakinsLightsaber> {
    @Override
    public Identifier getModelResource(AnakinsLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "geo/anakins_lightsaber.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnakinsLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "textures/item/anakins_lightsaber.png");
    }

    @Override
    public Identifier getAnimationResource(AnakinsLightsaber object) {
        return new Identifier(Derpyslightsabers.MOD_ID, "animations/anakins_lightsaber.animation.json");
    }
}
