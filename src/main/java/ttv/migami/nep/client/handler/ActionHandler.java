package ttv.migami.nep.client.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import ttv.migami.jeg.compat.PlayerReviveHelper;
import ttv.migami.jeg.item.GunItem;
import ttv.migami.nep.client.KeyBinds;
import ttv.migami.nep.common.ActionMode;
import ttv.migami.nep.common.Fruit;
import ttv.migami.nep.common.FruitDataHandler;
import ttv.migami.nep.effect.FruitEffect;
import ttv.migami.nep.event.FruitFireEvent;
import ttv.migami.nep.network.PacketHandler;
import ttv.migami.nep.network.message.C2SMessageAction;
import ttv.migami.nep.network.message.C2SMessageShooting;

public class ActionHandler
{
    private static ActionHandler instance;
    private KeyMapping activeKey = KeyBinds.KEY_Z_ACTION;
    private Fruit fruit;
    private Fruit.Action action;
    private int move;
    private int amount = 1;

    public static ActionHandler get()
    {
        if(instance == null)
        {
            instance = new ActionHandler();
        }
        return instance;
    }

    private boolean shooting;

    private ActionHandler() {}

    public boolean isShooting() {
        return  shooting;
    }

    private boolean isInGame()
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.getOverlay() != null)
            return false;
        if(mc.screen != null)
            return false;
        if(!mc.mouseHandler.isMouseGrabbed())
            return false;
        return mc.isWindowActive();
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_REPEAT) {
            int keyCode = event.getKey();
            if (keyCode == KeyBinds.KEY_Z_ACTION.getKey().getValue()) {
                this.activeKey = KeyBinds.KEY_Z_ACTION;
                this.action = this.fruit.getZAction();
                this.move = 1;
                resetOtherKeys(KeyBinds.KEY_Z_ACTION);
            } else if (keyCode == KeyBinds.KEY_X_ACTION.getKey().getValue()) {
                this.activeKey = KeyBinds.KEY_X_ACTION;
                this.action = this.fruit.getXAction();
                this.move = 2;
                resetOtherKeys(KeyBinds.KEY_X_ACTION);
            } else if (keyCode == KeyBinds.KEY_C_ACTION.getKey().getValue()) {
                this.activeKey = KeyBinds.KEY_C_ACTION;
                this.action = this.fruit.getCAction();
                this.move = 3;
                resetOtherKeys(KeyBinds.KEY_C_ACTION);
            } else if (keyCode == KeyBinds.KEY_V_ACTION.getKey().getValue()) {
                this.activeKey = KeyBinds.KEY_V_ACTION;
                this.action = this.fruit.getVAction();
                this.move = 4;
                resetOtherKeys(KeyBinds.KEY_V_ACTION);
            } else if (keyCode == KeyBinds.KEY_R_ACTION.getKey().getValue()) {
                this.activeKey = KeyBinds.KEY_R_ACTION;
                this.action = this.fruit.getRAction();
                this.move = 5;
                resetOtherKeys(KeyBinds.KEY_R_ACTION);
            }
        }
    }

    private void resetOtherKeys(KeyMapping activeKey) {
        KeyMapping[] keyMappings = {
                KeyBinds.KEY_Z_ACTION,
                KeyBinds.KEY_X_ACTION,
                KeyBinds.KEY_C_ACTION,
                KeyBinds.KEY_V_ACTION,
                KeyBinds.KEY_R_ACTION
        };

        for (KeyMapping key : keyMappings) {
            if (key != activeKey) {
                KeyMapping.set(key.getKey(), false);
                key.setDown(false);
            }
        }
    }

    private void resetAllKeys() {
        KeyMapping[] keyMappings = {
                KeyBinds.KEY_Z_ACTION,
                KeyBinds.KEY_X_ACTION,
                KeyBinds.KEY_C_ACTION,
                KeyBinds.KEY_V_ACTION,
                KeyBinds.KEY_R_ACTION
        };

        for (KeyMapping key : keyMappings) {
            KeyMapping.set(key.getKey(), false);
            key.setDown(false);
        }
    }

    @SubscribeEvent
    public void onHandlePress(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        if(!this.isInGame())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) {
            this.shooting = false;
            return;
        }

        if(FruitDataHandler.getCurrentEffect(player) != null && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect && !PlayerReviveHelper.isBleeding(player))
        {
            this.fruit = fruitEffect.getFruit();

            boolean shooting = activeKey.isDown();

            if(shooting)
            {
                if(!this.shooting)
                {
                    this.shooting = true;
                    PacketHandler.getPlayChannel().sendToServer(new C2SMessageShooting(true));
                }
            }
            else if(this.shooting)
            {
                this.shooting = false;
                PacketHandler.getPlayChannel().sendToServer(new C2SMessageShooting(false));
            }
        }
        else if(this.shooting)
        {
            this.shooting = false;
            PacketHandler.getPlayChannel().sendToServer(new C2SMessageShooting(false));
        }
    }

    @SubscribeEvent
    public void onPostClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        if(!isInGame())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player != null)
        {
            if(PlayerReviveHelper.isBleeding(player))
                return;

            if(FruitDataHandler.getCurrentEffect(player) != null && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect)
            {
                //if(this.activeKey.isDown())
                if(mc.options.keyAttack.isDown() && this.action != null && !(player.getMainHandItem().getItem() instanceof GunItem))
                {
                    if (this.action.getActionMode() != ActionMode.SINGLE)
                    {
                        this.fire(player, fruitEffect, move, amount);
                        boolean doAutoFire = this.action.getActionMode() == ActionMode.HOLD || this.action.getActionMode() == ActionMode.BURST;
                        if(!doAutoFire)
                        {
                            mc.options.keyAttack.setDown(false);
                            resetAllKeys();
                        }
                    } else {
                        this.fire(player, fruitEffect, move, amount);
                        mc.options.keyAttack.setDown(false);
                        resetAllKeys();
                    }
                }
            }
        }
    }

    public void fire(Player player, MobEffect effect, int move, int amount)
    {
        if(player.isSpectator())
            return;
        if(MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Pre(player, effect, move)))
            return;

        PacketHandler.getPlayChannel().sendToServer(new C2SMessageAction(player, MobEffect.getId(effect), move, amount));
        MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Post(player, effect, move));
    }
}