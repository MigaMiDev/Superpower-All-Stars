package ttv.migami.spas.entity.client.fruit.flower;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.flower.FlowerSpear;

import java.util.Random;

public class FlowerSpearGeoRenderer extends GeoEntityRenderer<FlowerSpear> {
    private Random random = new Random();

    public FlowerSpearGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FlowerSpearGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FlowerSpear animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/plant_spear.png");
    }

    @Override
    public void render(FlowerSpear entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
