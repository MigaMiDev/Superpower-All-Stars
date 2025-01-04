package ttv.migami.nep.common.container;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import ttv.migami.nep.common.FruitDataHandler;
import ttv.migami.nep.init.ModContainers;

import java.util.List;

public class BlessingMenu extends AbstractContainerMenu {
    private final List<MobEffect> fruitEffects;

    public BlessingMenu(int windowId, Inventory playerInventory) {
        super(ModContainers.BLESSINGS.get(), windowId);

        this.fruitEffects = FruitDataHandler.getPreviousEffects(playerInventory.player);

        //DevilFruits.LOGGER.info("BlessingMenu - fruitEffects initialized: {}", this.fruitEffects);
    }

    public void updateFruitEffects(List<MobEffect> effects) {
        this.fruitEffects.clear();
        this.fruitEffects.addAll(effects);
    }

    public List<MobEffect> getFruitEffects() {
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