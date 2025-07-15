package ttv.migami.spas.common;

import net.minecraft.resources.ResourceLocation;
import ttv.migami.spas.Reference;

import java.util.HashMap;
import java.util.Map;

public class ActionMode {

    /**
     * An action mode that shoots once per trigger press
     */
    public static final ActionMode PRESS = new ActionMode(new ResourceLocation(Reference.MOD_ID, "press"));

    /**
     * An action mode that shoots as long as the trigger is held down
     */
    public static final ActionMode HOLD = new ActionMode(new ResourceLocation(Reference.MOD_ID, "hold"));


    /**
     * A action mode that shoots in bursts
     */
    public static final ActionMode BURST = new ActionMode(new ResourceLocation(Reference.MOD_ID, "burst"));

    /**
     * The action mode map.
     */
    private static final Map<ResourceLocation, ActionMode> actionModeMap = new HashMap<>();
    private final ResourceLocation id;

    public static void registerType(ActionMode mode) {
        actionModeMap.putIfAbsent(mode.getId(), mode);
    }

    public static ActionMode getType(ResourceLocation id) {
        return (ActionMode) actionModeMap.getOrDefault(id, PRESS);
    }

    public ActionMode(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    static {
        registerType(PRESS);
        registerType(HOLD);
        registerType(BURST);
    }
}