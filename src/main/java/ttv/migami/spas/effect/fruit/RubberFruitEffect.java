package ttv.migami.spas.effect.fruit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import ttv.migami.spas.common.ActionType;
import ttv.migami.spas.effect.Action;
import ttv.migami.spas.effect.FruitEffect;

import java.util.Collections;
import java.util.List;

/**
 * Author: MigaMi
 */
public class RubberFruitEffect extends FruitEffect {

    public RubberFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.Z, new Action(5.0F, false, 200, 30, 2, Component.translatable("info.spas.rubber_fruit.z_action"), false, true, 1.7F));
        actions.put(ActionType.X, new Action(5.0F, false, 250, 0, 1, Component.translatable("info.spas.rubber_fruit.x_action"), false, true, 2.5F));
        actions.put(ActionType.C, new Action(5.0F, true, 350, 4, 30, Component.translatable("info.spas.rubber_fruit.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(5.0F, false, 500, 0, 1, Component.translatable("info.spas.rubber_fruit.v_action"), true, true, 2.0F));
        actions.put(ActionType.R, new Action(5.0F, false, 200, 50, 2, Component.translatable("info.spas.rubber_fruit.r_action"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
