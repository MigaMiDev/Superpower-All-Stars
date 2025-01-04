package ttv.migami.nep.client.render.fruit;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import ttv.migami.nep.Reference;
import ttv.migami.nep.item.AnimatedFruitItem;

public class AnimatedFruitModel extends GeoModel<AnimatedFruitItem> {
    @Override
    public ResourceLocation getModelResource(AnimatedFruitItem fruitItem) {
        return new ResourceLocation(Reference.MOD_ID, "geo/item/fruit/" + fruitItem.getFruitEffect() + ".geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(AnimatedFruitItem fruitItem) {
        return new ResourceLocation(Reference.MOD_ID, "textures/animated/fruit/" + fruitItem.toString() + ".png");
    }
    @Override
    public ResourceLocation getAnimationResource(AnimatedFruitItem fruitItem) {
        return new ResourceLocation(Reference.MOD_ID, "animations/item/" + fruitItem.getFruitEffect() + ".animation.json");
    }
}