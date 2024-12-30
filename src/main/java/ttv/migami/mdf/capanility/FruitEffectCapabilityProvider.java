package ttv.migami.mdf.capanility;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import ttv.migami.mdf.init.ModCapabilities;

public class FruitEffectCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final IFruitEffectCapability instance = new FruitEffectCapability();
    private final LazyOptional<IFruitEffectCapability> optional = LazyOptional.of(() -> instance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ModCapabilities.FRUIT_EFFECT_CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag effectList = new ListTag();
        for (MobEffectInstance effectInstance : instance.getFruitEffects()) {
            CompoundTag effectTag = new CompoundTag();
            effectInstance.save(effectTag);
            effectList.add(effectTag);
        }
        tag.put("FruitEffects", effectList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.clearFruitEffects();
        ListTag effectList = nbt.getList("FruitEffects", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < effectList.size(); i++) {
            CompoundTag effectTag = effectList.getCompound(i);
            MobEffectInstance effect = MobEffectInstance.load(effectTag);
            if (effect != null) {
                instance.addFruitEffect(effect);
            }
        }
    }
}