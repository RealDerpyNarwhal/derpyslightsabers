package net.derpy.mod.item.client;

import net.derpy.mod.item.custom.BaseLightsaber;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.model.GeoModel;

public class BaseLightsaberRenderer<T extends BaseLightsaber, M extends GeoModel<T>> extends GeoItemRenderer<T> {

    protected final M model;

    public BaseLightsaberRenderer(M model) {
        super(model);
        this.model = model;
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, T animatable, GeoBone bone, RenderLayer renderType,
                                  VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {

        if (bone != null && bone.getName() != null &&
                (bone.getName().equals("blade") ||
                        (bone.getParent() != null && "blade".equals(bone.getParent().getName())))) {

            // Replace with your actual glow texture path
            Identifier glowTexture = new Identifier("derpyslightsabers", "textures/item/test.png");
            RenderLayer glowLayer = RenderLayer.getBeaconBeam(glowTexture, true);

            VertexConsumer glowBuffer = bufferSource.getBuffer(glowLayer);
            super.renderRecursively(poseStack, animatable, bone, glowLayer, bufferSource, glowBuffer,
                    isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

            // Reset buffer to original render type
            buffer = bufferSource.getBuffer(renderType);
            return;
        }

        // Default rendering
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
