package ttv.migami.nep.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import ttv.migami.nep.FruitAnimations;
import ttv.migami.nep.client.render.fruit.AnimatedFruitRenderer;

import java.util.List;
import java.util.function.Consumer;

public class AnimatedFruitItem extends FruitItem implements GeoAnimatable, GeoItem {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public AnimatedFruitItem(Properties properties, List<MobEffectInstance> effects, String fruitEffect) {
        super(properties, effects, fruitEffect);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private AnimatedFruitRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(this.renderer == null) {
                    renderer = new AnimatedFruitRenderer();
                }
                return this.renderer;
            }
        });
    }

    private PlayState predicate(AnimationState<AnimatedFruitItem> event)
    {
        if (event.getController().getCurrentAnimation() == null || event.getController().getAnimationState() == AnimationController.State.STOPPED)
        {
            event.getController().tryTriggerAnimation("idle");
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<AnimatedFruitItem> controller = new AnimationController<>(this, "controller", 0, this::predicate)
                .triggerableAnim("idle", FruitAnimations.IDLE);
        controllers.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
