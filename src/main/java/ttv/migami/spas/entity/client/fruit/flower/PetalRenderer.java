package ttv.migami.spas.entity.client.fruit.flower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.client.ModModelLayers;
import ttv.migami.spas.entity.fruit.flower.Petal;

public class PetalRenderer extends EntityRenderer<Petal> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/petal.png");
    private final PetalModel<Petal> model;

    public PetalRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new PetalModel<>(pContext.bakeLayer(ModModelLayers.PETAL_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(Petal pEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(Petal pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pEntityYaw));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(pEntity.getXRot()));
        //pPoseStack.scale(1, -1, 1);
        pPoseStack.translate(0.0D, -1.500D, 0.0D);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}