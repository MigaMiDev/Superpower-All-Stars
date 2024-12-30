package ttv.migami.mdf.client.render.fruit;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import ttv.migami.mdf.item.AnimatedFruitItem;

public class AnimatedFruitRenderer extends GeoItemRenderer<AnimatedFruitItem> {
    public AnimatedFruitRenderer() {
        super(new AnimatedFruitModel());
    }
}