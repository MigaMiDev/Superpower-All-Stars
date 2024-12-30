package ttv.migami.mdf.entity.client.flower;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.entity.fruit.flower.FlowerSpear;
import ttv.migami.mdf.entity.fruit.flower.VineTrap;

public class VineTrapGeoModel extends GeoModel<VineTrap> {

	@Override
	public ResourceLocation getModelResource(VineTrap animatable) {
		return new ResourceLocation(Reference.MOD_ID, "geo/entity/vine_trap.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(VineTrap animatable) {
		return new ResourceLocation(Reference.MOD_ID, "textures/fruit/flower/vine_trap.png");
	}

	@Override
	public ResourceLocation getAnimationResource(VineTrap animatable) {
		return new ResourceLocation(Reference.MOD_ID, "animations/entity/vine_trap.animation.json");
	}
}