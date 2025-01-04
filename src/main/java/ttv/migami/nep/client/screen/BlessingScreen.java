package ttv.migami.nep.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
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
import ttv.migami.nep.Reference;
import ttv.migami.nep.client.util.RenderUtil;
import ttv.migami.nep.common.container.BlessingMenu;
import ttv.migami.nep.item.FruitItem;
import ttv.migami.nep.network.PacketHandler;
import ttv.migami.nep.network.message.C2SSwapBlessing;

import java.util.List;

public class BlessingScreen extends AbstractContainerScreen<BlessingMenu> {
    private int currentEffectIndex = 0;
    private List<MobEffect> fruitEffects;
    private ItemStack displayStack;
    private int previousEffectIndex = -1;
    private BlessingMenu blessingMenu;

    private float displayedIndex = 0f;
    private int targetEffectIndex = 0;
    private final float transitionSpeed = 0.1f;

    public BlessingScreen(BlessingMenu blessingMenu, Inventory playerInventory, Component title) {
        super(blessingMenu, playerInventory, title);
        this.blessingMenu = blessingMenu;
        this.fruitEffects = this.blessingMenu.getFruitEffects();

        //DevilFruits.LOGGER.info("BlessingScreen - fruitEffects received from BlessingMenu: {}", fruitEffects);
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

        int buttonOffsetX = (int) (0.2 * this.width);
        int centerY = this.height / 2;
        int effectDisplayOffsetY = (int) (0.1 * this.height);

        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            currentEffectIndex = (currentEffectIndex - 1 + fruitEffects.size()) % fruitEffects.size();
            updateFruit();
        }).pos(buttonOffsetX, centerY).size(buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            currentEffectIndex = (currentEffectIndex + 1) % fruitEffects.size();
            updateFruit();
        }).pos(this.width - buttonOffsetX - buttonWidth, centerY).size(buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.nep.choose_blessing"), button -> {
            MobEffect selectedEffect = fruitEffects.get(currentEffectIndex);
            this.swapToEffect(selectedEffect);
        }).pos((this.width - swapButtonWidth) / 2, (int) ((centerY * 1.5) + (int) (buttonHeight * 1.5))).size(swapButtonWidth, swapButtonHeight).build());

        updateFruit();
    }

    private void swapToEffect(MobEffect effect) {
        if (effect != null) {
            PacketHandler.getPlayChannel().sendToServer(new C2SSwapBlessing(MobEffect.getId(effect)));
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
        MobEffect effect = fruitEffects.get(currentEffectIndex);

        if (this.displayStack.getItem() instanceof FruitItem) {
            int centerX = this.width / 2;

            String modId = ForgeRegistries.ITEMS.getKey(this.displayStack.getItem()).getNamespace();
            String modName = ModList.get().getModContainerById(modId)
                    .map(container -> container.getModInfo().getDisplayName())
                    .orElse("NEP: Add-on");

            //int effectDisplayY = (int) (0.1 * this.height);
            int effectDisplayY = (int) (0.15 * this.height);
            pGuiGraphics.drawCenteredString(this.font, effect.getDisplayName(), centerX, effectDisplayY + 3, this.displayStack.getRarity().color.getColor());
            pGuiGraphics.drawCenteredString(this.font, modName, centerX, effectDisplayY + 15, 6843377);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;


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
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        renderBackground(guiGraphics);
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