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
public class FireFruitEffect extends FruitEffect {

    public FireFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.MOVE_A, new Action(5.0F, true, 200, 2, 16, Component.translatable("info.spas.fire_fruit.move_a"), false, true, 0.2F));
        actions.put(ActionType.MOVE_B, new Action(5.0F, false, 250, 0, 1, Component.translatable("info.spas.fire_fruit.move_b"), false, true, 0.5F));
        actions.put(ActionType.SPECIAL, new Action(5.0F, false, 350, 0, 1, Component.translatable("info.spas.fire_fruit.special"), false, true, 1.0F));
        actions.put(ActionType.ULTIMATE, new Action(5.0F, false, 300, 0, 1, Component.translatable("info.spas.fire_fruit.ultimate"), false, true, 2.0F));
        actions.put(ActionType.MOBILITY, new Action(5.0F, false, 150, 0, 3, Component.translatable("info.spas.fire_fruit.mobility"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
