package ttv.migami.spas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
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
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import ttv.migami.spas.Reference;
import ttv.migami.spas.client.handler.ActionHandler;
import ttv.migami.spas.client.util.RenderUtil;
import ttv.migami.spas.common.Fruit;
import ttv.migami.spas.common.container.MoveSelectionMenu;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.item.FruitItem;

public class MoveSelectionScreen extends AbstractContainerScreen<MoveSelectionMenu> {
    private MobEffect currentFruit;
    private ItemStack displayStack;
    private MoveSelectionMenu selectionMenuMenu;

    private final double DEADZONE = 10.0;
    private final String[] powers = {
            "Power A",    // 0 - Up
            "Power C",    // 1 - Right
            "Ultimate",   // 2 - Down
            "Power B"     // 3 - Left
    };
    private int selectedIndex = -1;

    public MoveSelectionScreen(MoveSelectionMenu selectionMenuMenu, Inventory playerInventory, Component title) {
        super(selectionMenuMenu, playerInventory, title);
        this.selectionMenuMenu = selectionMenuMenu;
        this.currentFruit = this.selectionMenuMenu.getCurrentFruit();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        updateFruit();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        MobEffect effect = currentFruit;

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        //float guiSize = this.width / 400.0f;

        if (this.displayStack.getItem() instanceof FruitItem) {
            String modId = ForgeRegistries.ITEMS.getKey(this.displayStack.getItem()).getNamespace();
            String modName = ModList.get().getModContainerById(modId)
                    .map(container -> container.getModInfo().getDisplayName())
                    .orElse("SPAS: Add-on");

            int effectDisplayY = (int) (0.15 * this.height) - 25;
            graphics.drawCenteredString(this.font, effect.getDisplayName(), centerX, effectDisplayY + 3, this.displayStack.getRarity().color.getColor());
            graphics.drawCenteredString(this.font, modName, centerX, effectDisplayY + 15, 6843377);
        } else {
            graphics.drawCenteredString(this.font, Component.translatable("gui.spas.empty_fruit"), centerX, centerY, 16777215);
        }

        if (this.currentFruit instanceof FruitEffect fruitEffect) {
            Fruit fruit = fruitEffect.getFruit();

            graphics.drawCenteredString(this.font, Component.translatable(fruit.getMoveA().getName()), centerX, (int) (centerY - 110), getColor(0));

            String powerB = Component.translatable(fruit.getMoveB().getName()).getString();
            int textWidth = this.font.width(powerB);
            graphics.drawString(this.font, powerB, (int) ((centerX - 102) - textWidth), centerY, getColor(3));

            graphics.drawString(this.font, Component.translatable(fruit.getSpecial().getName()), (int) (centerX + 102), centerY, getColor(1));
            graphics.drawCenteredString(this.font, Component.translatable(fruit.getUltimate().getName()), centerX, (int) (centerY + 102), getColor(2));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        //float guiSize = this.width / 400.0f;

        // Calculate direction
        double dx = mouseX - centerX;
        double dy = centerY - mouseY; // Inverted Y axis for atan2
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > DEADZONE) {
            double angle = Math.atan2(dy, dx);
            if (angle < 0) angle += 2 * Math.PI;

            selectedIndex = getIndexFromAngle(angle);
        } else {
            selectedIndex = -1;
        }

        float itemSize = (this.width / 20.0f) * 2;
        if (currentFruit != null) {
            renderItem(graphics, centerX, centerY, this.displayStack, 100, itemSize, false);

            String modId = ForgeRegistries.MOB_EFFECTS.getKey(this.currentFruit).getNamespace();
            String fruitID = ForgeRegistries.MOB_EFFECTS.getKey(this.currentFruit).getPath();
            ResourceLocation resourceLocation = new ResourceLocation(modId, "textures/gui/fruit/" + fruitID + ".png");

            int x = (int) (centerX - (13 * 1));
            int y = (int) (this.height * 0.65);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            graphics.blit(resourceLocation, centerX - 15, (int) (centerY - 90), 0, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (centerX - (75) - 15), centerY - 12, 30, 0, 30, 30);
            graphics.blit(resourceLocation, (int) (centerX + (75) - 15), centerY - 12, 60, 0, 30, 30);
            graphics.blit(resourceLocation, centerX - 15, (int) (centerY + 60), 90, 0, 30, 30);
            //graphics.blit(resourceLocation, centerX + 100 - 15, centerY + 100, 120, 0, 30, 30);

            RenderSystem.disableBlend();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        //renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        for (int j = 0; j < 4; j++) {
            drawTrapezoid(guiGraphics, centerX, centerY, j == selectedIndex, j);
        }
    }

    private int getColor(int index) {
        return index == selectedIndex ? 0xFFFFFF00 : 0xFFFFFFFF;
    }

    private int getIndexFromAngle(double angle) {
        if (angle >= 7 * Math.PI / 4 || angle < Math.PI / 4) return 1; // Right
        if (angle >= Math.PI / 4 && angle < 3 * Math.PI / 4) return 0; // Up
        if (angle >= 3 * Math.PI / 4 && angle < 5 * Math.PI / 4) return 3; // Left
        return 2; // Down
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            onSelectFruit(selectedIndex);
            this.onClose();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        onSelectFruit(selectedIndex);
        this.onClose();
        KeyBinds.FRUIT_ACTION.setDown(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }*/

    private void onSelectFruit(int index) {
        if (index < 0 || index >= powers.length) return;

        /*Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("Activated: " + powers[index])
        );*/

        ActionHandler.get().updateMove(index);

        // TODO: Actually trigger your power logic here (e.g. send to server or call handler)
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

    private void drawTrapezoid(GuiGraphics graphics, int cx, int cy, boolean selected, int index) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float r = selected ? 1.0f : 0.1f;
        float g = selected ? 1.0f : 0.1f;
        float b = selected ? 1.0f : 0.1f;
        float a = selected ? 1.0f : 0.6f;

        float size = 100;
        float inner = 50;
        float side = 50;

        Vec2[] pts = switch (index) {
            case 0 -> new Vec2[] { // UP
                    new Vec2(cx - side, cy - inner),
                    new Vec2(cx + side, cy - inner),
                    new Vec2(cx + size, cy - size),
                    new Vec2(cx - size, cy - size)
            };
            case 3 -> new Vec2[] { // LEFT
                    new Vec2(cx - size, cy - size),
                    new Vec2(cx - size, cy + size),
                    new Vec2(cx - inner, cy + side),
                    new Vec2(cx - inner, cy - side)
            };
            case 1 -> new Vec2[] { // RIGHT
                    new Vec2(cx + inner, cy - side),
                    new Vec2(cx + inner, cy + side),
                    new Vec2(cx + size, cy + size),
                    new Vec2(cx + size, cy - size)
            };
            case 2 -> new Vec2[] { // DOWN
                    new Vec2(cx + size, cy + size),
                    new Vec2(cx + side, cy + inner),
                    new Vec2(cx - side, cy + inner),
                    new Vec2(cx - size, cy + size)
            };
            default -> new Vec2[0];
        };

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (Vec2 p : pts) {
            buffer.vertex(p.x, p.y, 0).color(r, g, b, a).endVertex();
        }
        tess.end();
    }
}