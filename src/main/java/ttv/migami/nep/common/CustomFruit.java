package ttv.migami.nep.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class CustomFruit implements INBTSerializable<CompoundTag> {
    public Fruit fruit;

    public CustomFruit() {
    }

    public Fruit getFruit() {
        return this.fruit;
    }

    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.put("Gun", this.fruit.serializeNBT());
        return compound;
    }

    public void deserializeNBT(CompoundTag compound) {
        this.fruit = Fruit.create(compound.getCompound("Fruit"));
    }
}