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
public class FlowerFruitEffect extends FruitEffect {

    public FlowerFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.Z, new Action(5.0F, false, 100, 20, 2, Component.translatable("info.spas.flower.z_action"), false, false, 0.0F));
        actions.put(ActionType.X, new Action(5.0F, false, 500, 0, 1, Component.translatable("info.spas.flower.x_action"), false, false, 3.0F));
        actions.put(ActionType.C, new Action(5.0F, true, 600, 20, 3, Component.translatable("info.spas.flower.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(5.0F, true, 800, 3, 82, Component.translatable("info.spas.flower.v_action"), false, false, 0.0F));
        actions.put(ActionType.R, new Action(5.0F, false, 800, 0, 1, Component.translatable("info.spas.flower.r_action"), true, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
