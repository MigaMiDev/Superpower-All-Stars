package ttv.migami.spas.client.handler;

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
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.client.KeyBinds;
import ttv.migami.spas.common.ActionType;
import ttv.migami.spas.common.*;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.event.FruitFireEvent;
import ttv.migami.spas.init.ModEffects;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.message.C2SMessageAction;
import ttv.migami.spas.network.message.C2SMessageShooting;

import java.util.List;

public class ActionHandler
{
    private static ActionHandler instance;
    private final MoveManager moveManager = new MoveManager();
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

        if (this.fruit == null) return;

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
            if (FruitDataHandler.getCurrentEffect(player) != null) {
                if (!player.hasEffect(FruitDataHandler.getCurrentEffect(player))) {
                    FruitDataHandler.clearCurrentEffect(player);
                    this.fruit = null;
                }
            }

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

            if (fruit != null) {
                for (ActionType action : ActionType.values()) {
                    if (moveManager.getCooldown(action) > 0) {
                        moveManager.decrementCooldown(action);
                    }
                    if (moveManager.getInterval(action) > 0) {
                        moveManager.decrementInterval(action);
                    }
                    if (moveManager.getCooldown(action) == 0 && moveManager.getAmount(action) == 0) {
                        if (getFruitAction(action) != null) {
                            moveManager.setAmount(action, getFruitAction(action).getAttackAmount());
                        }
                    }
                }
            }

            if(FruitDataHandler.getCurrentEffect(player) != null && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect)
            {
                //if(this.activeKey.isDown())
                if(this.action != null) {

                    SuperpowerAllStars.LOGGER.atInfo().log("CurrentMove: " + action.getActionType() + " ZCooldown: " + moveManager.getCooldown(ActionType.Z) + " XCooldown: " + moveManager.getCooldown(ActionType.X) + " CCooldown: " + moveManager.getCooldown(ActionType.C) + " CAmounts: " + moveManager.getAmount(ActionType.C));

                    if(moveManager.getCooldown(action.getActionType()) == 0) {
                        if(KeyBinds.getShootMapping().isDown())
                        {
                            if (this.action.getActionMode() != ActionMode.SINGLE)
                            {
                                this.fire(player, fruitEffect, move, amount);
                                boolean doAutoFire = this.action.getActionMode() == ActionMode.HOLD || this.action.getActionMode() == ActionMode.BURST;
                                if(!doAutoFire)
                                {
                                    KeyBinds.getShootMapping().setDown(false);
                                    resetAllKeys();
                                }
                            } else {
                                this.fire(player, fruitEffect, move, amount);
                                KeyBinds.getShootMapping().setDown(false);
                                resetAllKeys();
                            }
                        }
                        else
                        {
                            if (this.action.getActionMode() != ActionMode.SINGLE && moveManager.getAmount(action.getActionType()) < action.getAttackAmount()) {
                                moveManager.setCooldown(action.getActionType(), action.getCooldown());
                                moveManager.setAmount(action.getActionType(), 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void fire(Player player, MobEffect effect, int move, int amount)
    {
        if(player.isSpectator())
            return;
        if (player.hasEffect(ModEffects.STUNNED.get()))
            return;

        if (moveManager.getInterval(action.getActionType()) == 0 && moveManager.getAmount(action.getActionType()) > 0) {
            if(MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Pre(player, effect, move)))
                return;

            moveManager.setRate(action.getActionType(), action.getRate());
            moveManager.setAmount(action.getActionType(), moveManager.getAmount(action.getActionType()) - 1);

            PacketHandler.getPlayChannel().sendToServer(new C2SMessageAction(player, MobEffect.getId(effect), move, amount));
            MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Post(player, effect, move));
        }

        if (moveManager.getAmount(action.getActionType()) == 0) {
            moveManager.setCooldown(action.getActionType(), action.getCooldown());
        }
    }

    public Fruit.Action getFruitAction(ActionType action) {
        List<Fruit.Action> actions = List.of(
                fruit.getZAction(),
                fruit.getXAction(),
                fruit.getCAction(),
                fruit.getVAction(),
                fruit.getRAction()
        );

        for (Fruit.Action currentAction : actions) {
            if (currentAction.getActionType().equals(action)) {
                return currentAction;
            }
        }
        return this.action == null ? null : this.action;
    }

    public MoveManager getCooldownManager() {
        return moveManager;
    }

    public Fruit getFruit() {
        return this.fruit;
    }

    public Fruit.Action getAction() {
        return this.action;
    }
}