package ttv.migami.mdf.entity.client.flower;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.flower.FlowerSpear;

public class FlowerSpearGeoModel extends GeoModel<FlowerSpear> {

	@Override
	public ResourceLocation getModelResource(FlowerSpear animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/plant_spear.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FlowerSpear animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/plant_spear.png");
	}

	@Override
	public ResourceLocation getAnimationResource(FlowerSpear animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/plant_spear.animation.json");
	}

	@Override
	public void setCustomAnimations(FlowerSpear animatable, long instanceId, AnimationState<FlowerSpear> animationState) {
		CoreGeoBone spear = getAnimationProcessor().getBone("spear");

		if (spear != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			//spear.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			spear.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}