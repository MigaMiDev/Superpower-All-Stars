package ttv.migami.spas.client.handler;

import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;

import java.util.HashMap;
import java.util.Map;

public class ActionType {

    /**
     * The primary/most common action of the fruit
     */
    public static final ActionType PRIMARY = new ActionType(new ResourceLocation(Reference.MOD_ID, "primary"));

    /**
     * A secondary action, commonly being a range move
     */
    public static final ActionType SECONDARY = new ActionType(new ResourceLocation(Reference.MOD_ID, "secondary"));

    /**
     * A heavy action, commonly requiring the user to hold the key down
     */
    public static final ActionType HEAVY = new ActionType(new ResourceLocation(Reference.MOD_ID, "heavy"));

    /**
     * The ultimate action of the fruit
     */
    public static final ActionType ULTIMATE = new ActionType(new ResourceLocation(Reference.MOD_ID, "ultimate"));

    /**
     * The support action of the fruit, most commonly being for used transportation
     */
    public static final ActionType SUPPORT = new ActionType(new ResourceLocation(Reference.MOD_ID, "support"));

    /**
     * The action mode map.
     */
    private static final Map<ResourceLocation, ActionType> actionTypeMap = new HashMap<>();
    private final ResourceLocation id;

    public static void registerType(ActionType mode) {
        actionTypeMap.putIfAbsent(mode.getId(), mode);
    }

    public static ActionType getType(ResourceLocation id) {
        return (ActionType) actionTypeMap.getOrDefault(id, PRIMARY);
    }

    public ActionType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    static {
        registerType(PRIMARY);
        registerType(SECONDARY);
        registerType(HEAVY);
        registerType(ULTIMATE);
        registerType(SUPPORT);
    }
}