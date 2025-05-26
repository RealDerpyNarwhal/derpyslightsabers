package net.derpy.mod.entity.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.AstromechEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AstromechModel extends GeoModel<AstromechEntity> {
    @Override
    public Identifier getModelResource(AstromechEntity animatable) {
        return new Identifier(Derpyslightsabers.MOD_ID, "geo/astromech.geo.json");
    }

    @Override
    public Identifier getTextureResource(AstromechEntity animatable) {
        return new Identifier(Derpyslightsabers.MOD_ID, "textures/entity/astromech.png");
    }

    @Override
    public Identifier getAnimationResource(AstromechEntity animatable) {
        return new Identifier(Derpyslightsabers.MOD_ID, "animations/astromech.animation.json");
    }
}
