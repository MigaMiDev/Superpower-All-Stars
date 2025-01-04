package ttv.migami.spas.entity.client.skeleton;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.skeleton.GasterBlaster;

public class GasterBlasterGeoModel extends GeoModel<GasterBlaster> {

	@Override
	public ResourceLocation getModelResource(GasterBlaster animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/gaster_blaster.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GasterBlaster animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/skeleton/gaster_blaster.png");
	}

	@Override
	public ResourceLocation getAnimationResource(GasterBlaster animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/gaster_blaster.animation.json");
	}
}