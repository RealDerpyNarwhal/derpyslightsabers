package net.derpy.mod.entity.client;

import net.derpy.mod.entity.custom.AstromechEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AstromechRenderer extends GeoEntityRenderer<AstromechEntity> {
    public AstromechRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AstromechModel());
    }
}
