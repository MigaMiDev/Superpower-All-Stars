package ttv.migami.mdf.entity.client.spider;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.spider.SpiderFang;

import java.util.Random;

public class SpiderFangGeoRenderer extends GeoEntityRenderer<SpiderFang> {
    private Random random = new Random();

    public SpiderFangGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpiderFangGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderFang animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/spider/spider_fang.png");
    }

    @Override
    public void render(SpiderFang entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
