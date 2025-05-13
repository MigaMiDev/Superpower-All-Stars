package ttv.migami.spas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import ttv.migami.spas.Reference;
import ttv.migami.spas.client.util.RenderUtil;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.container.FruitMenu;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.item.FruitItem;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.message.C2SMessagePermanentFruitsScreen;

import java.util.Arrays;

public class FruitScreen extends AbstractContainerScreen<FruitMenu> {
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/gui/fruit_menu.png");

    private MobEffect currentFruit;
    private ItemStack displayStack;
    private FruitMenu fruitMenu;

    public FruitScreen(FruitMenu fruitMenu, Inventory playerInventory, Component title) {
        super(fruitMenu, playerInventory, title);
        this.fruitMenu = fruitMenu;
        this.currentFruit = this.fruitMenu.getCurrentFruit();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        int permButtonWidth = 100;
        int permButtonHeight = 20;

        int buttonOffsetX = (int) (0.2 * this.width);
        int effectDisplayOffsetY = (int) (0.15 * this.height);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.spas.permanent_fruits"), button -> {
            this.permanentFruitsScreen();
        }).pos(buttonOffsetX - (permButtonWidth / 2), effectDisplayOffsetY + 3).size(permButtonWidth, permButtonHeight).build());

        updateFruit();
    }

    private void permanentFruitsScreen() {
        PacketHandler.getPlayChannel().sendToServer(new C2SMessagePermanentFruitsScreen());
    }

    private void updateFruit() {
        if (currentFruit != null) {
            MobEffect effectType = currentFruit;
            ResourceLocation effectLocation = ForgeRegistries.MOB_EFFECTS.getKey(effectType);

            if (effectLocation != null) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Reference.MOD_ID, effectLocation.getPath()));
                if (item != Items.AIR) {
                    this.displayStack = item.getDefaultInstance();
                } else {
                    this.displayStack = ItemStack.EMPTY;
                }
            }
        } else {
            this.displayStack = ItemStack.EMPTY;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        MobEffect effect = currentFruit;

        if (this.displayStack.getItem() instanceof FruitItem) {
            int centerX = this.width / 2;

            String modId = ForgeRegistries.ITEMS.getKey(this.displayStack.getItem()).getNamespace();
            String modName = ModList.get().getModContainerById(modId)
                    .map(container -> container.getModInfo().getDisplayName())
                    .orElse("SPAS: Add-on");

            int effectDisplayY = (int) (0.15 * this.height) - 25;
            pGuiGraphics.drawCenteredString(this.font, effect.getDisplayName(), centerX, effectDisplayY + 3, this.displayStack.getRarity().color.getColor());
            pGuiGraphics.drawCenteredString(this.font, modName, centerX, effectDisplayY + 15, 6843377);
        } else {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            pGuiGraphics.drawCenteredString(this.font, Component.translatable("gui.spas.empty_fruit"), centerX, centerY, 16777215);
        }

        int effectDisplayOffsetY = (int) (0.15 * this.height);
        int offSet = (int) (0.2 * this.width);
        int centerX = this.width / 2;
        if(RenderUtil.isMouseWithin(mouseX, mouseY, this.width - 30 - offSet, effectDisplayOffsetY + 3, 39, 22)) {
            pGuiGraphics.renderComponentTooltip(this.font, Arrays.asList(Component.translatable("cutesy.spas.thanks"), Component.literal("- MigaMi ♡").withStyle(ChatFormatting.BLUE)), mouseX, mouseY);
        }

        if (effect != null) {
            ResourceLocation effectLocation = ForgeRegistries.MOB_EFFECTS.getKey(effect);

            if(RenderUtil.isMouseWithin(mouseX, mouseY, centerX - 70, effectDisplayOffsetY + 2, 140, 23)) {
                pGuiGraphics.renderComponentTooltip(this.font, Arrays.asList(Component.translatable("gui.spas.mastery_level"), (Component.translatable("gui.spas.mastery_level2"))), mouseX, mouseY);
            }

            // Labels for Attacks
            if (this.currentFruit instanceof FruitEffect fruitEffect) {
                int spacing = (int) (this.width / 6.5f);

                for (int j = 0; j < 5; j++) {
                    int x = centerX - (2 * spacing) + (j * spacing) - 36;
                    int y = (int) (this.height * 0.65) - 6;

                    Fruit.Action action = fruitEffect.getFruit().getZAction();;
                    action = switch (j) {
                        case 0 -> fruitEffect.getFruit().getZAction();
                        case 1 -> fruitEffect.getFruit().getXAction();
                        case 2 -> fruitEffect.getFruit().getCAction();
                        case 3 -> fruitEffect.getFruit().getVAction();
                        case 4 -> fruitEffect.getFruit().getRAction();
                        default -> action;
                    };

                    if(RenderUtil.isMouseWithin(mouseX, mouseY, x, y, 72, 72)) {
                        pGuiGraphics.renderComponentTooltip(this.font, Arrays.asList(
                                Component.translatable(action.getName()).withStyle(ChatFormatting.BLUE),
                                (Component.translatable(action.getName() + "_description")),
                                (Component.translatable("info.spas.action_mode").withStyle(ChatFormatting.GRAY).append(Component.translatable("action_mode." + action.getActionMode().getId().toString()).withStyle(ChatFormatting.WHITE))),
                                (Component.translatable("info.spas.damage").withStyle(ChatFormatting.GRAY).append(Component.literal(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(ServerPlayHandler.calculateCustomDamage(minecraft.player, action.getDamage()))).withStyle(ChatFormatting.WHITE)))
                        ), mouseX, mouseY);
                    }
                }
            }

            if (effectLocation != null) {
                int progressBarWidth = 102;
                int progressBarHeight = 5;
                int progressBarX = centerX - progressBarWidth / 2;
                int progressBarY = (int) (0.15 * this.height) + 15;

                // Mastery progression bar
                String experienceKey = effectLocation + "_MasteryExperience";
                String levelKey = effectLocation + "_MasteryLevel";

                int currentExperience = this.minecraft.player.getPersistentData().getInt(experienceKey);
                int currentLevel = this.minecraft.player.getPersistentData().getInt(levelKey);

                int experienceForNextLevel = FruitDataHandler.getExperienceForNextLevel(currentLevel);
                float progress = (float) currentExperience / experienceForNextLevel;

                pGuiGraphics.drawCenteredString(this.font, Component.translatable("gui.spas.mastery"), centerX, progressBarY - 12, 16777215);

                pGuiGraphics.blit(GUI_TEXTURES, progressBarX, progressBarY, 0,22, progressBarWidth, progressBarHeight);
                pGuiGraphics.blit(GUI_TEXTURES, progressBarX, progressBarY, 0,27, (int) (progress * progressBarWidth), progressBarHeight);

                int separator = 10;
                if (currentLevel >= 100) {
                    separator = 21;
                } else if (currentLevel > 10) {
                    separator = 16;
                }
                pGuiGraphics.drawString(this.font, String.valueOf(currentLevel), progressBarX - separator, progressBarY + (progressBarHeight / 2) - 3, 16777215, true);
                pGuiGraphics.drawString(this.font, String.valueOf(currentLevel + 1), progressBarX + progressBarWidth + 5, progressBarY + (progressBarHeight / 2) - 3, 16777215, true);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = (int) ((this.height / 1.2) / 2);

        float itemSize = (this.width / 20.0f) * 3;
        float guiSize = this.width / 400.0f;

        if (currentFruit != null) {
            String modId = ForgeRegistries.MOB_EFFECTS.getKey(this.currentFruit).getNamespace();
            String fruitID = ForgeRegistries.MOB_EFFECTS.getKey(this.currentFruit).getPath();
            ResourceLocation resourceLocation = new ResourceLocation(modId, "textures/gui/fruit/" + fruitID + ".png");

            int x = (int) (centerX - (13 * guiSize));
            int y = (int) (this.height * 0.65);

            graphics.pose().pushPose();
            graphics.pose().scale(guiSize, guiSize, guiSize);

            graphics.blit(resourceLocation, (int) (-128 + (x / guiSize)), (int) (y / guiSize), 0, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (-64 + (x / guiSize)), (int) (y / guiSize), 30, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (x / guiSize), (int) (y / guiSize), 60, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (64 + (x / guiSize)), (int) (y / guiSize), 90, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (128 + (x / guiSize)), (int) (y / guiSize), 120, 0, 30, 30);

            graphics.pose().popPose();

            renderItem(graphics, centerX, centerY, this.displayStack, 100, itemSize, false);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);

        int effectDisplayOffsetY = (int) (0.15 * this.height);
        int offSet = (int) (0.2 * this.width);

        guiGraphics.blit(GUI_TEXTURES, this.width - 30 - offSet, effectDisplayOffsetY + 3, 0, 0, 38, 22);

        int centerX = this.width / 2;

        float guiSize = this.width / 400.0f;

        int x =  (int) (centerX - (16 * guiSize));
        int y = (int) (this.height * 0.65) - 6;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(guiSize, guiSize, guiSize);

        guiGraphics.blit(GUI_TEXTURES, (int) (-128 + (x / guiSize)), (int) (y / guiSize), 0, 32, 36, 36);
        guiGraphics.blit(GUI_TEXTURES, (int) (-64 + (x / guiSize)), (int) (y / guiSize), 0, 32, 36, 36);
        guiGraphics.blit(GUI_TEXTURES, (int) (x / guiSize), (int) (y / guiSize), 0, 32, 36, 36);
        guiGraphics.blit(GUI_TEXTURES, (int) (64 + (x / guiSize)), (int) (y / guiSize), 0, 32, 36, 36);
        guiGraphics.blit(GUI_TEXTURES, (int) (128 + (x / guiSize)), (int) (y / guiSize), 0, 32, 36, 36);

        guiGraphics.pose().popPose();
    }

    private void renderItem(GuiGraphics graphics, int x, int y, ItemStack itemStack, int offset, float size, boolean darken) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        {
            modelViewStack.translate(x, y, offset);
            modelViewStack.scale(size, -size, size);
            RenderSystem.applyModelViewMatrix();

            MultiBufferSource.BufferSource buffer = this.minecraft.renderBuffers().bufferSource();

            if (darken) {
                RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F); // Darken the item
            } else {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset to default color
            }

            Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.GUI, false, graphics.pose(), buffer, 15728880, OverlayTexture.NO_OVERLAY, RenderUtil.getModel(itemStack));
            buffer.endBatch();

        }
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}