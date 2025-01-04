package ttv.migami.nep.entity.client.spider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;
import ttv.migami.nep.Reference;
import ttv.migami.nep.entity.fruit.spider.SpiderFang;

public class SpiderFangGeoModel extends GeoModel<SpiderFang> {

	@Override
	public ResourceLocation getModelResource(SpiderFang animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/spider_fang.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SpiderFang animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/spider/spider_fang.png");
	}

	@Override
	public ResourceLocation getAnimationResource(SpiderFang animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/spider_fang.animation.json");
	}

	@Override
	public void setCustomAnimations(SpiderFang animatable, long instanceId, AnimationState<SpiderFang> animationState) {
		CoreGeoBone spear = getAnimationProcessor().getBone("spear");

		if (spear != null) {
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

			//spear.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			spear.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}