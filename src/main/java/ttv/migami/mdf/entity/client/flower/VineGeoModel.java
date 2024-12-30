package ttv.migami.mdf.entity.client.flower;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.flower.Vine;

public class VineGeoModel extends GeoModel<Vine> {

	@Override
	public ResourceLocation getModelResource(Vine animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/vine.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Vine animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/vine.png");
	}

	@Override
	public ResourceLocation getAnimationResource(Vine animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/vine.animation.json");
	}

	@Override
	public void setCustomAnimations(Vine animatable, long instanceId, AnimationState<Vine> animationState) {
		CoreGeoBone spear = getAnimationProcessor().getBone("vine");

		if (spear != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			//spear.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			spear.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}