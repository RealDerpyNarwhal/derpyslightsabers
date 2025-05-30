package net.derpy.mod.entity.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.C3POEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class C3PORenderer extends GeoEntityRenderer<C3POEntity> {
    public C3PORenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Identifier.of(Derpyslightsabers.MOD_ID, "c3po")));
    }
}
