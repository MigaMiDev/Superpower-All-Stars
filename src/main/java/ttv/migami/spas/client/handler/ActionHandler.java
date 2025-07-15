package ttv.migami.spas.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import ttv.migami.jeg.item.GunItem;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.client.KeyBinds;
import ttv.migami.spas.common.*;
import ttv.migami.spas.compat.PlayerReviveHelper;
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
    private Fruit fruit;
    private Fruit.Action action;
    private int move;
    private int amount = 1;

    public void updateMove(int move) {
        if (this.fruit == null) return;

        switch (move) {
            case 0:
                this.action = this.fruit.getMoveA();
                this.move = 1;
                break;
            case 3:
                this.action = this.fruit.getMoveB();
                this.move = 2;
                break;
            case 1:
                this.action = this.fruit.getSpecial();
                this.move = 3;
                break;
            case 2:
                this.action = this.fruit.getUltimate();
                this.move = 4;
                break;
            case 4:
                this.action = this.fruit.getMobility();
                this.move = 5;
                break;
        }
    }

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
        return shooting;
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
            if (keyCode == KeyBinds.MOBILITY_MOVE.getKey().getValue()) {
                this.action = this.fruit.getMobility();
                this.move = 5;
            }
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

        if(player.isDeadOrDying())
            return;

        boolean disabled = false;
        if (SuperpowerAllStars.jegLoaded) {
            if (player.getMainHandItem().getItem() instanceof GunItem) {
                disabled = true;
            }
        }

        if(!disabled && FruitDataHandler.getCurrentEffect(player) != null && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect && !PlayerReviveHelper.isBleeding(player))
        {
            this.fruit = fruitEffect.getFruit();
            if (FruitDataHandler.getCurrentEffect(player) != null) {
                if (!player.hasEffect(FruitDataHandler.getCurrentEffect(player))) {
                    FruitDataHandler.clearCurrentEffect(player);
                    this.fruit = null;
                }
            }

            boolean shooting = KeyBinds.getShootMapping().isDown();

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

            if(player.isDeadOrDying())
                return;

            if (fruit != null) {
                moveManager.setFruit(this.fruit);
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
                    if (moveManager.getCooldown(action) > moveManager.getActionCooldown(action)) {
                        moveManager.setCooldown(action, moveManager.getActionCooldown(action));
                    }
                }
            }

            if(FruitDataHandler.getCurrentEffect(player) != null && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect)
            {
                boolean disabled = false;
                if (SuperpowerAllStars.jegLoaded) {
                    if (player.getMainHandItem().getItem() instanceof GunItem) {
                        disabled = true;
                    }
                }

                //if(this.activeKey.isDown())
                if(this.action != null && !disabled) {

                    //SuperpowerAllStars.LOGGER.atInfo().log("CurrentMove: " + action.getActionType() + " ZCooldown: " + moveManager.getCooldown(ActionType.Z) + " XCooldown: " + moveManager.getCooldown(ActionType.X) + " CCooldown: " + moveManager.getCooldown(ActionType.C) + " CAmounts: " + moveManager.getAmount(ActionType.C));

                    if(moveManager.getCooldown(action.getActionType()) == 0) {
                        if(KeyBinds.getShootMapping().isDown() || KeyBinds.MOBILITY_MOVE.isDown())
                        {
                            amount = moveManager.getAmount(action.getActionType());
                            if (this.action.getActionMode() != ActionMode.PRESS)
                            {
                                this.fire(player, fruitEffect, move, amount);
                                boolean doAutoFire = this.action.getActionMode() == ActionMode.HOLD || this.action.getActionMode() == ActionMode.BURST;
                                if(!doAutoFire)
                                {
                                    KeyBinds.getShootMapping().setDown(false);
                                }
                            } else {
                                this.fire(player, fruitEffect, move, amount);
                                KeyBinds.getShootMapping().setDown(false);
                            }
                        }
                        else
                        {
                            if (this.action.getActionMode() != ActionMode.PRESS && moveManager.getAmount(action.getActionType()) < action.getAttackAmount()) {
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

            float pushBack = action.getShooterPushback();
            if (player.isCrouching() && player.level().getBlockState(player.getOnPos()).isSolid()) {
                pushBack = pushBack / 2;
            }
            recoil(player, pushBack);
        }

        if (moveManager.getAmount(action.getActionType()) == 0) {
            moveManager.setCooldown(action.getActionType(), action.getCooldown());
        }
    }

    public Fruit.Action getFruitAction(ActionType action) {
        List<Fruit.Action> actions = List.of(
                fruit.getMoveA(),
                fruit.getMoveB(),
                fruit.getSpecial(),
                fruit.getUltimate(),
                fruit.getMobility()
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

    public static void recoil(Player player, double force) {
        Vec3 lookVec = player.getLookAngle();
        double opposite = force;
        player.push(lookVec.x * opposite, lookVec.y * opposite, lookVec.z * opposite);
    }
}