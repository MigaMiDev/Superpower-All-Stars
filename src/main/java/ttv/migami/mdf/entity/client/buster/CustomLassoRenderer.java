package ttv.migami.mdf.entity.client.buster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.client.ModModelLayers;
import ttv.migami.mdf.entity.fruit.buster.Lasso;

public class CustomLassoRenderer extends EntityRenderer<Lasso> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Reference.MOD_ID, "textures/fruit/buster/lasso.png");

    private final LassoModel<Lasso> model;

    public CustomLassoRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LassoModel<>(context.bakeLayer(ModModelLayers.LASSO_LAYER));
    }

    @Override
    public void render(Lasso entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        if (entity.tickCount >= 5 && !entity.isLaunched()) {
            poseStack.pushPose();
            Vector3f ownerPosition = entity.getOwnerPos();
            Vec3 lassoPosition = entity.getPosition(partialTicks);

            float x1 = (float) (ownerPosition.x - lassoPosition.x);
            float y1 = (float) (ownerPosition.y - lassoPosition.y) + 1;
            float z1 = (float) (ownerPosition.z - lassoPosition.z);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
            PoseStack.Pose pose = poseStack.last();
            for (int i = 0; i <= 24; ++i) {
                addVertexPair(vertexConsumer, pose, x1, y1, z1, i, 24, 0.08F, 0.08F);
            }

            poseStack.popPose();



            VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        }

    }

    private void addVertexPair(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x1, float y1, float z1, int segmentIndex, int totalSegments, float leashWidth, float leashHeight) {
        float progress = (float) segmentIndex / (float) totalSegments;
        float x = x1 * progress;
        float y = y1 * progress;
        float z = z1 * progress;

        vertexConsumer.vertex(pose.pose(), x - leashWidth, y + leashHeight, z).color(129, 72, 34, 255).uv2(OverlayTexture.NO_OVERLAY).endVertex();
        vertexConsumer.vertex(pose.pose(), x + leashWidth, y + leashHeight, z).color(129, 72, 34, 255).uv2(OverlayTexture.NO_OVERLAY).endVertex();
        vertexConsumer.vertex(pose.pose(), x + leashWidth, y - leashHeight, z).color(129, 72, 34, 255).uv2(OverlayTexture.NO_OVERLAY).endVertex();
        vertexConsumer.vertex(pose.pose(), x - leashWidth, y - leashHeight, z).color(129, 72, 34, 255).uv2(OverlayTexture.NO_OVERLAY).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(Lasso pEntity) {
        return TEXTURE_LOCATION;
    }

}