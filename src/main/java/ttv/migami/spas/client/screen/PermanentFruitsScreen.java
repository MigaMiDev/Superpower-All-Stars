package ttv.migami.spas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
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
import ttv.migami.spas.common.container.PermanentFruitsMenu;
import ttv.migami.spas.item.FruitItem;
import ttv.migami.spas.network.PacketHandler;
import ttv.migami.spas.network.message.C2SMessageFruitScreen;
import ttv.migami.spas.network.message.C2SSwapPermanentFruit;

import java.util.Arrays;
import java.util.List;

public class PermanentFruitsScreen extends AbstractContainerScreen<PermanentFruitsMenu> {
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/gui/fruit_menu.png");

    private int currentEffectIndex = 0;
    private List<MobEffect> fruitEffects;
    private ItemStack displayStack;
    private int previousEffectIndex = -1;
    private PermanentFruitsMenu permanentFruitsMenu;

    private float displayedIndex = 0f;
    private int targetEffectIndex = 0;
    private final float transitionSpeed = 0.1f;

    public PermanentFruitsScreen(PermanentFruitsMenu permanentFruitsMenu, Inventory playerInventory, Component title) {
        super(permanentFruitsMenu, playerInventory, title);
        this.permanentFruitsMenu = permanentFruitsMenu;
        this.fruitEffects = this.permanentFruitsMenu.getFruitEffects();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        int buttonWidth = 20;
        int buttonHeight = 20;
        int swapButtonWidth = 80;
        int swapButtonHeight = 20;
        int permButtonWidth = 100;
        int permButtonHeight = 20;

        int buttonOffsetX = (int) (0.2 * this.width);
        int centerY = this.height / 2;
        int effectDisplayOffsetY = (int) (0.15 * this.height);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.spas.current_fruit"), button -> {
            this.currentFruitScreen();
        }).pos(buttonOffsetX - (permButtonWidth / 2), effectDisplayOffsetY + 3).size(permButtonWidth, permButtonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (!fruitEffects.isEmpty()) {
                currentEffectIndex = (currentEffectIndex - 1 + fruitEffects.size()) % fruitEffects.size();
                updateFruit();
            }
        }).pos(buttonOffsetX, centerY).size(buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (!fruitEffects.isEmpty()) {
                currentEffectIndex = (currentEffectIndex + 1) % fruitEffects.size();
                updateFruit();
            }
        }).pos(this.width - buttonOffsetX - buttonWidth, centerY).size(buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.spas.choose_fruit"), button -> {
            if (!fruitEffects.isEmpty()) {
                MobEffect selectedEffect = fruitEffects.get(currentEffectIndex);
                this.swapToEffect(selectedEffect);
            }
        }).pos((this.width - swapButtonWidth) / 2, (int) ((centerY * 1.5) + (int) (buttonHeight * 1.5))).size(swapButtonWidth, swapButtonHeight).build());

        updateFruit();
    }

    private void currentFruitScreen() {
        PacketHandler.getPlayChannel().sendToServer(new C2SMessageFruitScreen());
    }

    private void swapToEffect(MobEffect effect) {
        if (effect != null) {
            PacketHandler.getPlayChannel().sendToServer(new C2SSwapPermanentFruit(MobEffect.getId(effect)));
        } else {
            LogUtils.getLogger().error("No matching FruitType found for effect: {}", effect.getDescriptionId());
        }
        this.onClose();
    }

    private void updateFruit() {
        if (!fruitEffects.isEmpty()) {
            MobEffect effectType = fruitEffects.get(currentEffectIndex);
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
        if (!fruitEffects.isEmpty()) {
            MobEffect effect = fruitEffects.get(currentEffectIndex);

            if (this.displayStack.getItem() instanceof FruitItem) {
                int centerX = this.width / 2;

                String modId = ForgeRegistries.ITEMS.getKey(this.displayStack.getItem()).getNamespace();
                String modName = ModList.get().getModContainerById(modId)
                        .map(container -> container.getModInfo().getDisplayName())
                        .orElse("SPAS: Add-on");

                //int effectDisplayY = (int) (0.1 * this.height);
                int effectDisplayY = (int) (0.15 * this.height);
                pGuiGraphics.drawCenteredString(this.font, effect.getDisplayName(), centerX, effectDisplayY + 3, this.displayStack.getRarity().color.getColor());
                pGuiGraphics.drawCenteredString(this.font, modName, centerX, effectDisplayY + 15, 6843377);
            }
        } else {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            pGuiGraphics.drawCenteredString(this.font, Component.translatable("gui.spas.empty_permanent_fruits"), centerX, centerY, 16777215);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int effectDisplayOffsetY = (int) (0.15 * this.height);
        int offSet = (int) (0.2 * this.width);

        float itemSize = this.width / 20.0f;
        int separation = (int) (0.1 * this.width);

        if (currentEffectIndex != previousEffectIndex) {
            updateFruit();
            previousEffectIndex = currentEffectIndex;
        }

        if (!fruitEffects.isEmpty()) {
            renderItem(graphics, centerX, centerY, this.displayStack, 100, itemSize * 4, false);

            int previousEffectIndex = (currentEffectIndex - 1 + fruitEffects.size()) % fruitEffects.size();
            ItemStack previousItemStack = getItemStackForEffect(fruitEffects.get(previousEffectIndex));
            renderItem(graphics, (int) (centerX - separation * 1.75), centerY, previousItemStack, 30, itemSize * 2, true);

            int nextEffectIndex = (currentEffectIndex + 1) % fruitEffects.size();
            ItemStack nextItemStack = getItemStackForEffect(fruitEffects.get(nextEffectIndex));
            renderItem(graphics, (int) (centerX + separation * 1.75), centerY, nextItemStack, 30, itemSize * 2, true);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if(RenderUtil.isMouseWithin(mouseX, mouseY, this.width - 30 - offSet, effectDisplayOffsetY, 39, 22)) {
            graphics.renderComponentTooltip(this.font, Arrays.asList(Component.translatable("cutesy.spas.thanks"), Component.literal("- MigaMi ♡").withStyle(ChatFormatting.BLUE)), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);

        int effectDisplayOffsetY = (int) (0.15 * this.height);
        int offSet = (int) (0.2 * this.width);

        guiGraphics.blit(GUI_TEXTURES, this.width - 30 - offSet, effectDisplayOffsetY + 3, 0, 0, 39, 22);
    }

    private ItemStack getItemStackForEffect(MobEffect effectInstance) {
        ResourceLocation effectLocation = ForgeRegistries.MOB_EFFECTS.getKey(effectInstance);
        if (effectLocation != null) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Reference.MOD_ID, effectLocation.getPath()));
            return (item != Items.AIR) ? item.getDefaultInstance() : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0) {
            currentEffectIndex = (currentEffectIndex - 1 + fruitEffects.size()) % fruitEffects.size();
        } else if (delta < 0) {
            currentEffectIndex = (currentEffectIndex + 1) % fruitEffects.size();
        }
        updateFruit();

        return true;
    }
}