package ttv.migami.spas.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import ttv.migami.spas.common.ActionType;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.NetworkFruitManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Author: MigaMi
 */
public abstract class FruitEffect extends MobEffect {
    private final WeakHashMap<CompoundTag, Fruit> modifiedFruitCache = new WeakHashMap<>();
    protected final Map<ActionType, Action> actions = new EnumMap<>(ActionType.class);
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

    public Action getAction(ActionType actionType) {
        return actions.get(actionType);
    }

    public abstract void initializeActions();
}
