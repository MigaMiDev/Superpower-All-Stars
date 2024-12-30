package ttv.migami.mdf.entity.client.buster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.client.ModModelLayers;
import ttv.migami.mdf.entity.fruit.buster.Piano;

public class PianoRenderer extends EntityRenderer<Piano> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/fruit/buster/piano.png");
    private final PianoModel<Piano> model;

    public PianoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new PianoModel<>(pContext.bakeLayer(ModModelLayers.PIANO_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(Piano pEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(Piano pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntityYaw));
        VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
