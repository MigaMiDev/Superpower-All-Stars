package ttv.migami.spas.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Config;
import ttv.migami.spas.Reference;
import ttv.migami.spas.client.handler.ActionHandler;
import ttv.migami.spas.common.ActionMode;
import ttv.migami.spas.common.ActionType;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.MoveManager;

import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class CooldownBarRenderer {
    protected static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 10;
    private static final int PADDING = 25;
    private static final int FADE_OUT_TIME = 20;
    private static final int SEGMENT_PADDING = 0;

    private static final Map<ActionType, Integer> fadeTimers = new EnumMap<>(ActionType.class);
    private static final Map<ActionType, Integer> yPositions = new EnumMap<>(ActionType.class);

    static {
        Minecraft minecraft = Minecraft.getInstance();
        int baseY = PADDING;
        for (ActionType action : ActionType.values()) {
            fadeTimers.put(action, 0);
            yPositions.put(action, baseY);
            baseY += BAR_HEIGHT + PADDING;
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.hasResult()) return;

        if(!Config.CLIENT.display.displayCooldownGUI.get())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ActionHandler movesetHandler = ActionHandler.get();
        MoveManager moveManager = movesetHandler.getCooldownManager();

        int x = minecraft.getWindow().getGuiScaledWidth() - BAR_WIDTH - PADDING + Config.CLIENT.display.displayCooldownGUIXOffset.get();
        int y = minecraft.getWindow().getGuiScaledHeight() - (BAR_HEIGHT * 5) - (PADDING * 5);

        if (movesetHandler.getFruit() != null) {
            for (ActionType action : ActionType.values()) {
                Fruit.Action effectAction = movesetHandler.getFruitAction(action);
                int cooldown = moveManager.getCooldown(action);
                int interval = moveManager.getInterval(action);
                int amount = moveManager.getAmount(action);
                int maxCooldown = effectAction.getCooldown();
                int attackAmount = moveManager.getAmount(action);
                MutableComponent name = effectAction.getName();

                if (cooldown > 0 || Config.CLIENT.display.vanishCooldownGUI.get()) {
                    fadeTimers.put(action, FADE_OUT_TIME);
                    drawCooldownBar(event.getGuiGraphics(), x, yPositions.get(action) + Config.CLIENT.display.displayCooldownGUIYOffset.get(), cooldown, maxCooldown, name, 255);
                } else if (fadeTimers.get(action) > 0) {
                    int alpha = (int) (255 * (fadeTimers.get(action) / (float) FADE_OUT_TIME));
                    drawCooldownBar(event.getGuiGraphics(), x, yPositions.get(action) + Config.CLIENT.display.displayCooldownGUIYOffset.get(), 0, maxCooldown, name, alpha);
                }

                if (!effectAction.getActionMode().equals(ActionMode.HOLD) && attackAmount > 0 && effectAction.getAttackAmount() > 1) {
                    drawSegmentedBar(event.getGuiGraphics(), x, yPositions.get(action) + Config.CLIENT.display.displayCooldownGUIYOffset.get(), attackAmount, effectAction.getAttackAmount(), name, 255);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ActionType action : ActionType.values()) {
            if (fadeTimers.get(action) > 0) {
                fadeTimers.put(action, fadeTimers.get(action) - 1);
            }
        }
    }

    private static void drawCooldownBar(GuiGraphics guiGraphics, int x, int y, int cooldown, int maxCooldown, MutableComponent actionName, int alpha) {
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);

        int barColor;
        int backgroundColor;
        if (cooldown == 0) {
            barColor = (alpha << 24) | 0xFFFFFF;
            backgroundColor = (alpha << 24) | 0x3F3F3F;
        }
        else {
            barColor = (alpha << 24) | 0x868686;
            backgroundColor = (alpha << 24) | 0x000000;
        }

        guiGraphics.fill(x, y + 1, x + BAR_WIDTH - 1, y + BAR_HEIGHT, backgroundColor);

        // This line calculates the width of the cooldown bar as it fills up.
        int barWidth = (int) ((BAR_WIDTH - 2) * (1 - ((double) cooldown / maxCooldown)));

        // This line ensures the bar grows from left to right.
        guiGraphics.fill(x, y + 1, x + 1 + barWidth, y + BAR_HEIGHT - 1, barColor);

        // Draw the action's name
        guiGraphics.drawString(Minecraft.getInstance().font, actionName, x, y - 10, 0xFFFFFF, true);
    }

    private static void drawSegmentedBar(GuiGraphics guiGraphics, int x, int y, int attackAmount, int maxAmount, MutableComponent actionName, int alpha) {
        RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);

        int segmentWidth = (BAR_WIDTH - (maxAmount - 1) * SEGMENT_PADDING) / maxAmount;
        int barColor = (alpha << 24) | 0xFFFFFF;
        int backgroundColor = (alpha << 24) | 0x3F3F3F;

        guiGraphics.fill(x, y + 1, x + BAR_WIDTH - 1, y + BAR_HEIGHT, backgroundColor);

        for (int i = 0; i < maxAmount; i++) {
            int segmentX = x - 1 + i * (segmentWidth + SEGMENT_PADDING);
            if (i < attackAmount) {
                if (i == 2) {
                    guiGraphics.fill(segmentX + 1, y + 1, segmentX + segmentWidth + 1, y + BAR_HEIGHT - 1, barColor);
                }
                else {
                    guiGraphics.fill(segmentX + 1, y + 1, segmentX + segmentWidth, y + BAR_HEIGHT - 1, barColor);
                }
            }
        }

        // Draw the action name with drop shadow
        guiGraphics.drawString(Minecraft.getInstance().font, actionName, x, y - 10, 0xFFFFFF, true);
    }

}

