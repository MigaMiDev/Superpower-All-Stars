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
        actions.put(ActionType.Z, new Action(5.0F, true, 200, 2, 16, Component.translatable("info.spas.fire_fruit.z_action"), false, true, 0.2F));
        actions.put(ActionType.X, new Action(5.0F, false, 250, 0, 1, Component.translatable("info.spas.fire_fruit.x_action"), false, true, 0.5F));
        actions.put(ActionType.C, new Action(5.0F, false, 350, 0, 1, Component.translatable("info.spas.fire_fruit.c_action"), false, true, 1.0F));
        actions.put(ActionType.V, new Action(5.0F, false, 300, 0, 1, Component.translatable("info.spas.fire_fruit.v_action"), false, true, 2.0F));
        actions.put(ActionType.R, new Action(5.0F, false, 150, 0, 3, Component.translatable("info.spas.fire_fruit.r_action"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
