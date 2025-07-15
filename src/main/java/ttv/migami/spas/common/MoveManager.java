package ttv.migami.spas.common;

import java.util.EnumMap;
import java.util.Map;

public class MoveManager {
    private final Map<ActionType, Integer> cooldowns = new EnumMap<>(ActionType.class);
    private final Map<ActionType, Integer> intervals = new EnumMap<>(ActionType.class);
    private final Map<ActionType, Integer> amounts = new EnumMap<>(ActionType.class);
    private Fruit fruit;

    public MoveManager() {
        for (ActionType action : ActionType.values()) {
            cooldowns.put(action, 0);
            intervals.put(action, 0);
            amounts.put(action, 0);
        }
    }

    public void setFruit(Fruit fruit) {
        this.fruit = fruit;
    }

    public int getCooldown(ActionType action) {
        return cooldowns.get(action);
    }

    public void setCooldown(ActionType action, int value) {
        cooldowns.put(action, value);
    }

    public void decrementCooldown(ActionType action) {
        cooldowns.put(action, cooldowns.get(action) - 1);
    }

    public int getInterval(ActionType action) {
        return intervals.get(action);
    }

    public void setRate(ActionType action, int value) {
        intervals.put(action, value);
    }

    public void decrementInterval(ActionType action) {
        intervals.put(action, intervals.get(action) - 1);
    }

    public int getAmount(ActionType action) {
        return amounts.get(action);
    }

    public void setAmount(ActionType action, int value) {
        amounts.put(action, value);
    }

    public void resetAmountsAndCooldowns() {
        for (ActionType action : ActionType.values()) {
            intervals.put(action, 0);
            amounts.put(action, 0);
            cooldowns.put(action, getActionCooldown(action));
        }
    }

    public int getActionCooldown(ActionType action) {
        if (action.equals(ActionType.MOVE_A)) {
            return fruit.getMoveA().getCooldown();
        } else if (action.equals(ActionType.MOVE_B)) {
            return fruit.getMoveB().getCooldown();
        } else if (action.equals(ActionType.SPECIAL)) {
            return fruit.getSpecial().getCooldown();
        } else if (action.equals(ActionType.ULTIMATE)) {
            return fruit.getUltimate().getCooldown();
        } else if (action.equals(ActionType.MOBILITY)) {
            return fruit.getMobility().getCooldown();
        }
        return 100;
    }
}
