package ttv.migami.spas.client.render.fruit;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import ttv.migami.spas.item.AnimatedFruitItem;

public class AnimatedFruitRenderer extends GeoItemRenderer<AnimatedFruitItem> {
    public AnimatedFruitRenderer() {
        super(new AnimatedFruitModel());
    }
}