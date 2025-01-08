package ttv.migami.spas.common.container;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.init.ModContainers;

public class FruitMenu extends AbstractContainerMenu {
    private final MobEffect currentFruit;

    public FruitMenu(int windowId, Inventory playerInventory) {
        super(ModContainers.FRUIT_MENU.get(), windowId);

        this.currentFruit = FruitDataHandler.getCurrentEffect(playerInventory.player);
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