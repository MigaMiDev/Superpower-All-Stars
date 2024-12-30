package ttv.migami.mdf.entity.client.skeleton;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.skeleton.BoneZone;

public class BoneZoneGeoModel extends GeoModel<BoneZone> {
	@Override
	public ResourceLocation getModelResource(BoneZone animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/bone_zone.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BoneZone animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/skeleton/bone.png");
	}

	@Override
	public ResourceLocation getAnimationResource(BoneZone animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/bone_zone.animation.json");
	}

	@Override
	public void setCustomAnimations(BoneZone animatable, long instanceId, AnimationState<BoneZone> animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");

		if (head != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}