package net.derpy.mod.entity.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.entity.custom.IceShardEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceShardRenderer extends GeoEntityRenderer<IceShardEntity> {

    public IceShardRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(
                Identifier.of(Derpyslightsabers.MOD_ID, "ice_shard_entity")));
        this.shadowRadius = 0f;
    }

    @Override
    public RenderLayer getRenderType(IceShardEntity animatable, Identifier texture,
                                     net.minecraft.client.render.VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityCutoutNoCull(getTextureLocation(animatable));
    }

    @Override
    protected int getBlockLight(IceShardEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(IceShardEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public boolean shouldRender(IceShardEntity entity,
                                net.minecraft.client.render.Frustum frustum,
                                double x, double y, double z) {
        return !entity.isRemoved();
    }
}
