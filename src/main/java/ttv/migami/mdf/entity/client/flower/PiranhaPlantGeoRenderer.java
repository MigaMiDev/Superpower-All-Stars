package ttv.migami.mdf.entity.client.flower;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.flower.PiranhaPlant;


public class PiranhaPlantGeoRenderer extends GeoEntityRenderer<PiranhaPlant> {
    public PiranhaPlantGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PiranhaPlantGeoModel());
    }

    @Override
    public ResourceLocation getTextureLocation(PiranhaPlant animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/piranha_plant.png");
    }

    @Override
    public void render(PiranhaPlant entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        float xRot = (float) (angleFromVec3(entity.getLookAngle()) / 180F * (float)Math.PI);
        poseStack.rotateAround(new Quaternionf().rotationY(xRot), 0, 0, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public static double angleFromVec3(Vec3 vec3){
        Vec3 vec2 = new Vec3(vec3.x, 0, vec3.z);
        double L = vec3.length();
        double Lxz = vec2.length();
        double x = vec3.x;
        double z = vec3.z;
        double beta = -Math.atan(x/z)* 180/Math.PI* Mth.sign(x);
        if (z > 0 && beta < 0) beta = 180+beta;
        else if (z < 0 && beta < 0) beta = -180 - beta;
        else if (z < 0 && beta > 0) beta = -beta;
        if ((z > 0 && x < 0) || (z < 0 && x > 0) && beta < 0) beta = -beta;
        return beta;
    }
}
