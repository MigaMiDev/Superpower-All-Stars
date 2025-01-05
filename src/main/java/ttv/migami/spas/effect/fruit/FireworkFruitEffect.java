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
        actions.put(ActionType.Z, new Action(5.0F, true, 160, 0, 1, Component.translatable("info.spas.firework_fruit.z_action"), false, true, 5.0F));
        actions.put(ActionType.X, new Action(5.0F, true, 200, 0, 1, Component.translatable("info.spas.firework_fruit.x_action"), false, false, 0.0F));
        actions.put(ActionType.C, new Action(5.0F, true, 250, 5, 8, Component.translatable("info.spas.firework_fruit.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(5.0F, true, 300, 4, 24, Component.translatable("info.spas.firework_fruit.v_action"), false, false, 0.0F));
        actions.put(ActionType.R, new Action(5.0F, false, 150, 0, 1, Component.translatable("info.spas.firework_fruit.r_action"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
