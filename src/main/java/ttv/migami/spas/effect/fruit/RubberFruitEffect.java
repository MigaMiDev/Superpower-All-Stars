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
        actions.put(ActionType.MOVE_A, new Action(5.0F, false, 200, 30, 2, Component.translatable("info.spas.rubber_fruit.move_a"), false, true, 1.7F));
        actions.put(ActionType.MOVE_B, new Action(5.0F, false, 250, 0, 1, Component.translatable("info.spas.rubber_fruit.move_b"), false, true, 2.5F));
        actions.put(ActionType.SPECIAL, new Action(5.0F, true, 350, 4, 30, Component.translatable("info.spas.rubber_fruit.special"), false, false, 0.0F));
        actions.put(ActionType.ULTIMATE, new Action(5.0F, false, 500, 0, 1, Component.translatable("info.spas.rubber_fruit.ultimate"), true, true, 2.0F));
        actions.put(ActionType.MOBILITY, new Action(5.0F, false, 200, 50, 2, Component.translatable("info.spas.rubber_fruit.mobility"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
