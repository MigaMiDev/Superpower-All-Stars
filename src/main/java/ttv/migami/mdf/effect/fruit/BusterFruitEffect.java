package ttv.migami.mdf.effect.fruit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import ttv.migami.mdf.common.ActionType;
import ttv.migami.mdf.effect.Action;
import ttv.migami.mdf.effect.FruitEffect;

import java.util.Collections;
import java.util.List;

/**
 * Author: MigaMi
 */
public class BusterFruitEffect extends FruitEffect {

    public BusterFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.Z, new Action(5.0F, true, 100, 3, 6, Component.translatable("info.mdf.buster_fruit.z_action"), false, true, 2.5F));
        actions.put(ActionType.X, new Action(5.0F, false, 300, 0, 1, Component.translatable("info.mdf.buster_fruit.x_action"), false, true, 3.0F));
        actions.put(ActionType.C, new Action(5.0F, false, 550, 0, 1, Component.translatable("info.mdf.buster_fruit.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(5.0F, true, 600, 4, 1, Component.translatable("info.mdf.buster_fruit.v_action"), false, false, 0.0F));
        actions.put(ActionType.F, new Action(5.0F, false, 800, 0, 1, Component.translatable("info.mdf.buster_fruit.r_action"), false, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
