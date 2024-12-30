package ttv.migami.mdf.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import ttv.migami.mdf.common.ActionType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Author: MigaMi
 */
public abstract class FruitEffect extends MobEffect {
    protected final Map<ActionType, Action> actions = new EnumMap<>(ActionType.class);

    protected FruitEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public Action getAction(ActionType actionType) {
        return actions.get(actionType);
    }

    public abstract void initializeActions();
}
