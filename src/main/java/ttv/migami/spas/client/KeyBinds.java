package ttv.migami.spas.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import ttv.migami.spas.Config;

/**
 * Author: MrCrayfish
 */
public class KeyBinds
{
    public static final KeyMapping KEY_Z_ACTION = new KeyMapping("key.spas.z_action", GLFW.GLFW_KEY_Z, "key.categories.spas");
    public static final KeyMapping KEY_X_ACTION = new KeyMapping("key.spas.x_action", GLFW.GLFW_KEY_X, "key.categories.spas");
    public static final KeyMapping KEY_C_ACTION = new KeyMapping("key.spas.c_action", GLFW.GLFW_KEY_C, "key.categories.spas");
    public static final KeyMapping KEY_V_ACTION = new KeyMapping("key.spas.v_action", GLFW.GLFW_KEY_V, "key.categories.spas");
    public static final KeyMapping KEY_R_ACTION = new KeyMapping("key.spas.r_action", GLFW.GLFW_KEY_R, "key.categories.spas");
    public static final KeyMapping FRUIT_MENU = new KeyMapping("key.spas.fruit_menu", GLFW.GLFW_KEY_B, "key.categories.spas");

    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(KEY_Z_ACTION);
        event.register(KEY_X_ACTION);
        event.register(KEY_C_ACTION);
        event.register(KEY_V_ACTION);
        event.register(KEY_R_ACTION);
        event.register(FRUIT_MENU);
    }

    public static KeyMapping getShootMapping()
    {
        Minecraft mc = Minecraft.getInstance();
        return Config.CLIENT.controls.flipControls.get() ? mc.options.keyAttack : mc.options.keyUse;
    }
}
