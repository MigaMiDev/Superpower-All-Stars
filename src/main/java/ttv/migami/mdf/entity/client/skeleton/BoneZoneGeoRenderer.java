package ttv.migami.mdf.entity.client.skeleton;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.skeleton.BoneZone;

public class BoneZoneGeoRenderer extends GeoEntityRenderer<BoneZone> {
    public BoneZoneGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BoneZoneGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(BoneZone animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/skeleton/bone.png");
    }

    @Override
    public void render(BoneZone entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
