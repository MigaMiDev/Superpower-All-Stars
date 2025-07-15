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
public class SpiderFruitEffect extends FruitEffect {

    public SpiderFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.MOVE_A, new Action(5.0F, false, 120, 3, 3, Component.translatable("info.spas.spider_fruit.move_a"), false, true, 1.7F));
        actions.put(ActionType.MOVE_B, new Action(5.0F, true, 220, 3, 8, Component.translatable("info.spas.spider_fruit.move_b"), false, true, 2.0F));
        actions.put(ActionType.SPECIAL, new Action(5.0F, true, 300, 5, 20, Component.translatable("info.spas.spider_fruit.special"), false, false, 0.0F));
        actions.put(ActionType.ULTIMATE, new Action(5.0F, false, 450, 0, 1, Component.translatable("info.spas.spider_fruit.ultimate"), false, true, 2.5F));
        actions.put(ActionType.MOBILITY, new Action(5.0F, true, 65, 0, 1, Component.translatable("info.spas.spider_fruit.mobility"), false, true, 1.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
