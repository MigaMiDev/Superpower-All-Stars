package ttv.migami.spas.entity.client.fruit.flower;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import ttv.migami.spas.Reference;
import ttv.migami.spas.entity.fruit.flower.PiranhaPlant;

public class PiranhaPlantGeoModel extends GeoModel<PiranhaPlant> {

	@Override
	public ResourceLocation getModelResource(PiranhaPlant animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/piranha_plant.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PiranhaPlant animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/piranha_plant.png");
	}

	@Override
	public ResourceLocation getAnimationResource(PiranhaPlant animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/piranha_plant.animation.json");
	}
}