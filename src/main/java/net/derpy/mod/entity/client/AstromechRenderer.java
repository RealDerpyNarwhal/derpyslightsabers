package net.derpy.mod.entity.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.AstromechEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AstromechRenderer extends GeoEntityRenderer<AstromechEntity> {
    public AstromechRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Identifier.of(Derpyslightsabers.MOD_ID,"astromech")));
    }
}
