package net.derpy.mod.entity.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.IceShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceShardRenderer extends GeoEntityRenderer<IceShardEntity> {

    public IceShardRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(net.minecraft.util.Identifier.of(Derpyslightsabers.MOD_ID, "ice_shard_entity")));
        this.shadowRadius = 0.2f;
    }
}
