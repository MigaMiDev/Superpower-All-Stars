package ttv.migami.mdf.common.container;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import ttv.migami.mdf.init.ModCapabilities;
import ttv.migami.mdf.init.ModContainers;

import java.util.List;

public class BlessingMenu extends AbstractContainerMenu {
    private final List<MobEffectInstance> fruitEffects;

    public BlessingMenu(int windowId, Inventory playerInventory) {
        super(ModContainers.BLESSINGS.get(), windowId);

        this.fruitEffects = playerInventory.player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY)
                .orElseThrow(() -> new IllegalStateException("Fruit effect capability not found"))
                .getFruitEffects();

        //DevilFruits.LOGGER.info("BlessingMenu - fruitEffects initialized: {}", this.fruitEffects);

    }

    public List<MobEffectInstance> getFruitEffects() {
        return this.fruitEffects;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}