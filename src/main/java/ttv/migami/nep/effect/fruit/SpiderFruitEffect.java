package ttv.migami.nep.effect.fruit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import ttv.migami.nep.common.ActionType;
import ttv.migami.nep.effect.Action;
import ttv.migami.nep.effect.FruitEffect;

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
        actions.put(ActionType.Z, new Action(5.0F, false, 120, 3, 3, Component.translatable("info.nep.spider_fruit.z_action"), false, true, 1.7F));
        actions.put(ActionType.X, new Action(5.0F, true, 220, 3, 8, Component.translatable("info.nep.spider_fruit.x_action"), false, true, 2.0F));
        actions.put(ActionType.C, new Action(5.0F, true, 300, 5, 20, Component.translatable("info.nep.spider_fruit.c_action"), false, false, 0.0F));
        actions.put(ActionType.V, new Action(5.0F, false, 450, 0, 1, Component.translatable("info.nep.spider_fruit.v_action"), false, true, 2.5F));
        actions.put(ActionType.F, new Action(5.0F, true, 65, 0, 1, Component.translatable("info.nep.spider_fruit.r_action"), false, true, 1.0F));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
