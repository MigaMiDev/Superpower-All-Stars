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
public class CreeperFruitEffect extends FruitEffect {

    public CreeperFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.MOVE_A, new Action(2.0F, false, 250, 0, 1, Component.translatable("info.spas.creeper_fruit.move_a"), false, false, 0.0F));
        actions.put(ActionType.MOVE_B, new Action(1.0F, false, 250, 0, 1, Component.translatable("info.spas.creeper_fruit.move_b"), false, false, 0.0F));
        actions.put(ActionType.SPECIAL, new Action(0.0F, false, 200, 0, 1, Component.translatable("info.spas.creeper_fruit.special"), false, false, 0.0F));
        actions.put(ActionType.ULTIMATE, new Action(0.0F, false, 700, 0, 1, Component.translatable("info.spas.creeper_fruit.ultimate"), false, false, 0.0F));
        actions.put(ActionType.MOBILITY, new Action(0.0F, false, 100, 0, 1, Component.translatable("info.spas.creeper_fruit.mobility"), true, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
