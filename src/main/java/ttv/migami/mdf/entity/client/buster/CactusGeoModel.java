package ttv.migami.mdf.entity.client.buster;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.buster.Cactus;

public class CactusGeoModel extends GeoModel<Cactus> {

	@Override
	public ResourceLocation getModelResource(Cactus animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/cactus.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Cactus animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/buster/cactus.png");
	}

	@Override
	public ResourceLocation getAnimationResource(Cactus animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/cactus.animation.json");
	}
}