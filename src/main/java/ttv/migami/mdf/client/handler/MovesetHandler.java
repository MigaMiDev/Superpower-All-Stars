package ttv.migami.mdf.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import ttv.migami.jeg.item.GunItem;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.client.KeyBinds;
import ttv.migami.mdf.common.ActionType;
import ttv.migami.mdf.common.FruitType;
import ttv.migami.mdf.common.MoveManager;
import ttv.migami.mdf.effect.Action;
import ttv.migami.mdf.effect.FruitEffect;
import ttv.migami.mdf.event.FruitFireEvent;
import ttv.migami.mdf.init.ModEffects;
import ttv.migami.mdf.network.PacketHandler;
import ttv.migami.mdf.network.message.C2SFruitMessage;

import java.util.Locale;

import static ttv.migami.mdf.DevilFruits.jegLoaded;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class MovesetHandler {

    private static MovesetHandler instance;

    public FruitEffect effect;
    public FruitType currentFruit;
    private FruitType previousFruit;

    private final MoveManager moveManager = new MoveManager();

    public static MovesetHandler get() {
        if (instance == null) {
            instance = new MovesetHandler();
        }
        return instance;
    }

    private MovesetHandler() {}

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        updateCurrentFruit(player);

        if (effect != null) {
            for (ActionType action : ActionType.values()) {
                if (moveManager.getCooldown(action) > 0) {
                    moveManager.decrementCooldown(action);
                }
                if (moveManager.getInterval(action) > 0) {
                    moveManager.decrementInterval(action);
                }
                if (moveManager.getCooldown(action) == 0 && moveManager.getAmount(action) == 0) {
                    updateActionAmount(action);
                    notifyPlayer(action);
                }
            }
        }
    }

    private void updateCurrentFruit(Player player) {
        FruitType newFruit = null;
        FruitEffect newEffect = null;

        if (player.hasEffect(ModEffects.FIREWORK_FRUIT.get())) {
            newEffect = ModEffects.FIREWORK_FRUIT.get();
            newFruit = FruitType.FIREWORK_FRUIT;
        } else if (player.hasEffect(ModEffects.CREEPER_FRUIT.get())) {
            newEffect = ModEffects.CREEPER_FRUIT.get();
            newFruit = FruitType.CREEPER_FRUIT;
        } else if (player.hasEffect(ModEffects.SKELETON_FRUIT.get())) {
            newEffect = ModEffects.SKELETON_FRUIT.get();
            newFruit = FruitType.SKELETON_FRUIT;
        } else if (player.hasEffect(ModEffects.SQUID_FRUIT.get())) {
            newEffect = ModEffects.SQUID_FRUIT.get();
            newFruit = FruitType.SQUID_FRUIT;
        } else if (player.hasEffect(ModEffects.BUSTER_FRUIT.get())) {
            newEffect = ModEffects.BUSTER_FRUIT.get();
            newFruit = FruitType.BUSTER_FRUIT;
        } else if (player.hasEffect(ModEffects.FLOWER_FRUIT.get())) {
            newEffect = ModEffects.FLOWER_FRUIT.get();
            newFruit = FruitType.FLOWER_FRUIT;
        } else if (player.hasEffect(ModEffects.FIRE_FRUIT.get())) {
            newEffect = ModEffects.FIRE_FRUIT.get();
            newFruit = FruitType.FIRE_FRUIT;
        } else if (player.hasEffect(ModEffects.RUBBER_FRUIT.get())) {
            newEffect = ModEffects.RUBBER_FRUIT.get();
            newFruit = FruitType.RUBBER_FRUIT;
        } else if (player.hasEffect(ModEffects.SPIDER_FRUIT.get())) {
            newEffect = ModEffects.SPIDER_FRUIT.get();
            newFruit = FruitType.SPIDER_FRUIT;
        }


        if (newFruit != currentFruit) {
            resetCooldownsAndAmounts();
            previousFruit = currentFruit;
            currentFruit = newFruit;
            effect = newEffect;
        }
    }

    private void resetCooldownsAndAmounts() {
        for (ActionType action : ActionType.values()) {
            moveManager.setCooldown(action, 100);
            moveManager.setInterval(action, 0);
            moveManager.setAmount(action, 0);
        }
    }

    private void updateActionAmount(ActionType action) {
        Action fruitAction = effect.getAction(action);
        moveManager.setAmount(action, fruitAction.getAttackAmount());
    }

    private void notifyPlayer(ActionType action) {
        String keyName = switch (action) {
            case Z -> KeyBinds.KEY_Z_ACTION.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH);
            case X -> KeyBinds.KEY_X_ACTION.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH);
            case C -> KeyBinds.KEY_C_ACTION.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH);
            case V -> KeyBinds.KEY_V_ACTION.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH);
            case F -> KeyBinds.KEY_R_ACTION.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH);
        };
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (jegLoaded && ttv.migami.jeg.client.KeyBinds.KEY_ATTACHMENTS.getKey() == KeyBinds.KEY_Z_ACTION.getKey()) {
            if (event.getKey() == GLFW.GLFW_KEY_Z && event.getAction() == GLFW.GLFW_PRESS) {
                if (player.getMainHandItem().getItem() instanceof GunItem && player.isCrouching()) {
                    return;
                }
            }
        }

        //if (effect != null && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
        if (effect != null && !(player.getUseItem().getItem() == Items.SHIELD)) {
            for (ActionType action : ActionType.values()) {
                if (isActionTriggered(action, event)) {
                    performAction(action);
                }
                if (isActionReleased(action, event)) {
                    looseAmounts(action);
                }
            }
        }
    }

    private boolean isActionTriggered(ActionType action, InputEvent.Key event) {
        return switch (action) {
            case Z -> KeyBinds.KEY_Z_ACTION.isDown() && moveManager.getCooldown(action) == 0;
            case X -> KeyBinds.KEY_X_ACTION.isDown() && moveManager.getCooldown(action) == 0;
            case C -> KeyBinds.KEY_C_ACTION.isDown() && moveManager.getCooldown(action) == 0;
            case V -> KeyBinds.KEY_V_ACTION.isDown() && moveManager.getCooldown(action) == 0;
            case F -> KeyBinds.KEY_R_ACTION.isDown() && moveManager.getCooldown(action) == 0;
        } && (actionCanBeHeld(action) || event.getAction() == GLFW.GLFW_PRESS);
    }

    private boolean isActionReleased(ActionType action, InputEvent.Key event) {
        return switch (action) {
            case Z -> !KeyBinds.KEY_Z_ACTION.isDown() && moveManager.getAmount(action) != getActionAmount(action) &&
                    moveManager.getCooldown(action) == 0;
            case X -> !KeyBinds.KEY_X_ACTION.isDown() && moveManager.getAmount(action) != getActionAmount(action) &&
                    moveManager.getCooldown(action) == 0;
            case C -> !KeyBinds.KEY_C_ACTION.isDown() && moveManager.getAmount(action) != getActionAmount(action) &&
                    moveManager.getCooldown(action) == 0;
            case V -> !KeyBinds.KEY_V_ACTION.isDown() && moveManager.getAmount(action) != getActionAmount(action) &&
                    moveManager.getCooldown(action) == 0;
            case F -> !KeyBinds.KEY_R_ACTION.isDown() && moveManager.getAmount(action) != getActionAmount(action) &&
                    moveManager.getCooldown(action) == 0;
        } && (actionCanBeHeld(action));
    }

    private void performAction(ActionType action) {
        Player player = Minecraft.getInstance().player;

        if (player.hasEffect(ModEffects.STUNNED.get()))
            return;

        if (moveManager.getInterval(action) == 0 && moveManager.getAmount(action) > 0) {
            PacketHandler.getPlayChannel().sendToServer(new C2SFruitMessage(currentFruit.ordinal() + 1, action.ordinal() + 1, moveManager.getAmount(action)));
            moveManager.setInterval(action, getActionInterval(action));
            moveManager.setAmount(action, moveManager.getAmount(action) - 1);

            if (hasRecoil(action)) {
                if(MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Pre(player, getRecoilKick(action))))
                    return;
                MinecraftForge.EVENT_BUS.post(new FruitFireEvent.Post(player, getRecoilKick(action)));
            }
        }
        if (moveManager.getAmount(action) == 0) {
            moveManager.setCooldown(action, getActionCooldown(action));
        }
    }

    private void looseAmounts(ActionType action) {
        if (moveManager.getAmount(action) != 0) {
            moveManager.setAmount(action, 0);
            moveManager.setCooldown(action, getActionCooldown(action));
        }
    }

    public int getActionAmount(ActionType action) {
        return effect.getAction(action).getAttackAmount();
    }

    private int getActionInterval(ActionType action) {
        return effect.getAction(action).getAttackInterval();
    }

    private boolean actionCanBeHeld(ActionType action) {
        return effect.getAction(action).canBeHeld();
    }

    public int getActionCooldown(ActionType action) {
        return effect.getAction(action).getCooldown();
    }

    public MutableComponent getActionName(ActionType action) {
        return effect.getAction(action).getName();
    }

    public boolean isDisabled(ActionType action) {
        return effect.getAction(action).isDisabled();
    }

    public boolean hasRecoil(ActionType action) {
        return effect.getAction(action).hasRecoil();
    }

    public float getRecoilKick(ActionType action) {
        return effect.getAction(action).getRecoilKick();
    }

    public MoveManager getCooldownManager() {
        return moveManager;
    }
}
