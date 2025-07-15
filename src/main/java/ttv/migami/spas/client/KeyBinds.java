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
    public static final KeyMapping FRUIT_ACTION = new KeyMapping("key.spas.fruit_action", GLFW.GLFW_KEY_R, "key.categories.spas");
    public static final KeyMapping MOBILITY_MOVE = new KeyMapping("key.spas.mobility_move", GLFW.GLFW_KEY_G, "key.categories.spas");
    public static final KeyMapping FRUIT_MENU = new KeyMapping("key.spas.fruit_menu", GLFW.GLFW_KEY_B, "key.categories.spas");

    public static void registerKeyMappings(RegisterKeyMappingsEvent event)
    {
        event.register(FRUIT_ACTION);
        event.register(MOBILITY_MOVE);
        event.register(FRUIT_MENU);
    }

    public static KeyMapping getShootMapping()
    {
        Minecraft mc = Minecraft.getInstance();
        return Config.CLIENT.controls.flipControls.get() ? mc.options.keyAttack : mc.options.keyUse;
    }
}
