package ttv.migami.spas.entity.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public abstract class AbstractMarkRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation textureLocation;
    private final RenderType renderType;

    public AbstractMarkRenderer(EntityRendererProvider.Context context, ResourceLocation textureLocation) {
        super(context);
        this.textureLocation = textureLocation;
        this.renderType = RenderType.entityTranslucent(textureLocation);
    }

    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float scale = getScale(entity);
        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.XP.rotationDegrees(90F));
        poseStack.translate(0.0, -0.25, -0.0075);
        PoseStack.Pose posestackPose = poseStack.last();
        Matrix4f matrix4f = posestackPose.pose();
        Matrix3f matrix3f = posestackPose.normal();
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        renderShape(vertexConsumer, matrix4f, matrix3f, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected abstract float getScale(T entity);

    private void renderShape(VertexConsumer vertexConsumer, Matrix4f pose, Matrix3f normal, int lightmapUV) {
        vertex(vertexConsumer, pose, normal, lightmapUV, 0.0F, 0, 0, 1);
        vertex(vertexConsumer, pose, normal, lightmapUV, 1.0F, 0, 1, 1);
        vertex(vertexConsumer, pose, normal, lightmapUV, 1.0F, 1, 1, 0);
        vertex(vertexConsumer, pose, normal, lightmapUV, 0.0F, 1, 0, 0);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, int lightmapUV, float x, int y, int u, int v) {
        consumer.vertex(pose, x - 0.5F, (float)y - 0.25F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float)u, (float)v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(lightmapUV)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureLocation;
    }
}
