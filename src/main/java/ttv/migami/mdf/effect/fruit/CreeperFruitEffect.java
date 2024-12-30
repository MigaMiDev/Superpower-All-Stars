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
public class CreeperFruitEffect extends FruitEffect {

    public CreeperFruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        initializeActions();
    }

    @Override
    public void initializeActions() {
        actions.put(ActionType.Z, new Action(2.0F, false, 250, 0, 1, Component.translatable("info.mdf.creeper_fruit.z_action"), false, false, 0.0F));
        actions.put(ActionType.X, new Action(1.0F, false, 250, 0, 1, Component.translatable("info.mdf.creeper_fruit.x_action"), false, false, 0.0F));
        actions.put(ActionType.C, new Action(0.0F, false, 200, 0, 1, Component.translatable("info.mdf.creeper_fruit.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(0.0F, false, 700, 0, 1, Component.translatable("info.mdf.creeper_fruit.v_action"), false, false, 0.0F));
        actions.put(ActionType.F, new Action(0.0F, false, 100, 0, 1, Component.translatable("info.mdf.creeper_fruit.r_action"), true, false, 0.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
