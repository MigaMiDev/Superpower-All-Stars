package ttv.migami.nep.client.render.fruit;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import ttv.migami.nep.item.AnimatedFruitItem;

public class AnimatedFruitRenderer extends GeoItemRenderer<AnimatedFruitItem> {
    public AnimatedFruitRenderer() {
        super(new AnimatedFruitModel());
    }
}