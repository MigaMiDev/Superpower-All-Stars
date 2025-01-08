package ttv.migami.spas.common.container;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.init.ModContainers;

import java.util.List;

public class PermanentFruitsMenu extends AbstractContainerMenu {
    private final List<MobEffect> fruitEffects;
    private final MobEffect currentFruit;

    public PermanentFruitsMenu(int windowId, Inventory playerInventory) {
        super(ModContainers.PERMANENT_FRUITS.get(), windowId);

        this.fruitEffects = FruitDataHandler.getPreviousEffects(playerInventory.player);
        this.currentFruit = FruitDataHandler.getCurrentEffect(playerInventory.player);
    }

    public void updateFruitEffects(List<MobEffect> effects) {
        this.fruitEffects.clear();
        this.fruitEffects.addAll(effects);
    }

    public List<MobEffect> getFruitEffects() {
        return this.fruitEffects;
    }

    public MobEffect getCurrentFruit() {
        return this.currentFruit;
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