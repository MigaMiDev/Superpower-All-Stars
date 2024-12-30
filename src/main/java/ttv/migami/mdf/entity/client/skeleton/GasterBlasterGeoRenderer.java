package ttv.migami.mdf.entity.client.skeleton;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.skeleton.GasterBlaster;

public class GasterBlasterGeoRenderer extends GeoEntityRenderer<GasterBlaster> {
    public GasterBlasterGeoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(Reference.MOD_ID, "gaster_blaster"), true));
    }

    @Override
    public ResourceLocation getTextureLocation(GasterBlaster animatable) {
        return new ResourceLocation(Reference.MOD_ID, "textures/fruit/skeleton/gaster_blaster.png");
    }

    @Override
    public void render(GasterBlaster entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        //float xRot = (float) (angleFromVec3(entity.getLookAngle()) / 180F * (float)Math.PI);
        //poseStack.rotateAround(new Quaternionf().rotationX(xRot), 0, 0, 0);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    /*@Override
    protected void applyRotations(GasterBlaster animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        float xRot = (float) xanglefromvec3(animatable.getLookAngle());
        float yRot = (float) yanglefromvec3(animatable.getLookAngle());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(270));

    }

    public static double yanglefromvec3(Vec3 vec3){
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

    public static double xanglefromvec3(Vec3 vec3){
        Vec3 vec2 = new Vec3(vec3.x, 0, vec3.z);
        double L = vec3.length();
        double Lxz = vec2.length();
        double x = vec3.x;
        double z = vec3.z;
        return Math.acos(Lxz/L) * 180/Math.PI * Math.signum(vec3.y);
    }*/
}
