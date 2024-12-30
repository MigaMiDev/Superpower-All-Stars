package ttv.migami.mdf.capanility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface IFruitEffectCapability {
    List<MobEffectInstance> getFruitEffects();
    void addFruitEffect(MobEffectInstance effect);
    void removeFruitEffect(MobEffect effect);
    void clearFruitEffects();
    void setFruitEffects(List<MobEffectInstance> effects);
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}