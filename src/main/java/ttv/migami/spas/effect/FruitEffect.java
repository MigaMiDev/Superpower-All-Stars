package ttv.migami.spas.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.NetworkFruitManager;

import java.util.Collections;
import java.util.List;

/**
 * Author: MigaMi
 */
public abstract class FruitEffect extends MobEffect {
    private Fruit fruit = new Fruit();

    protected FruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public void setFruit(NetworkFruitManager.Supplier supplier) {
        this.fruit = supplier.getFruit();
    }

    public Fruit getFruit() {
        return this.fruit;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
