package net.derpy.mod.item.client;

import net.derpy.mod.Derpyslightsabers;
import net.derpy.mod.item.custom.AnakinsLightsaber;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AnakinsLightsaberRenderer extends GeoItemRenderer<AnakinsLightsaber> {
    public AnakinsLightsaberRenderer() {
        super(new AnakinsLightsaberModel());
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, AnakinsLightsaber animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone != null && bone.getName() != null && (bone.getName().equals("blade") || (bone.getParent() != null && bone.getParent().getName() != null && bone.getParent().getName().equals("blade")))) {
            Identifier textureLocation = Identifier.of(Derpyslightsabers.MOD_ID, "textures/item/test.png");
            assert textureLocation != null;
            RenderLayer otherType = RenderLayer.getBeaconBeam(textureLocation, true);
            buffer = bufferSource.getBuffer(otherType);
            super.renderRecursively(poseStack, animatable, bone, otherType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

            buffer = bufferSource.getBuffer(renderType);
            return;
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
