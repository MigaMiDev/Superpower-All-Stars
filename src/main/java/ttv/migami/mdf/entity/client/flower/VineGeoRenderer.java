package ttv.migami.mdf.entity.client.flower;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.flower.Vine;

import java.util.Random;

public class VineGeoRenderer extends GeoEntityRenderer<Vine> {
    private Random random = new Random();

    public VineGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VineGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(Vine animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/vine.png");
    }

    @Override
    public void render(Vine entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
