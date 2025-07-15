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
public class FireworkFruitEffect extends FruitEffect {

    public FireworkFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.MOVE_A, new Action(5.0F, true, 160, 0, 1, Component.translatable("info.spas.firework_fruit.move_a"), false, true, 5.0F));
        actions.put(ActionType.MOVE_B, new Action(5.0F, true, 200, 0, 1, Component.translatable("info.spas.firework_fruit.move_b"), false, false, 0.0F));
        actions.put(ActionType.SPECIAL, new Action(5.0F, true, 250, 5, 8, Component.translatable("info.spas.firework_fruit.special"), false, false, 0.0F));
        actions.put(ActionType.ULTIMATE, new Action(5.0F, true, 300, 4, 24, Component.translatable("info.spas.firework_fruit.ultimate"), false, false, 0.0F));
        actions.put(ActionType.MOBILITY, new Action(5.0F, false, 150, 0, 1, Component.translatable("info.spas.firework_fruit.mobility"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
