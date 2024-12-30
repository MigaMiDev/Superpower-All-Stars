package ttv.migami.mdf.capanility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import ttv.migami.mdf.DevilFruits;

import java.util.ArrayList;
import java.util.List;

public class FruitEffectCapability implements IFruitEffectCapability {
    private final List<MobEffectInstance> fruitEffects = new ArrayList<>();

    @Override
    public List<MobEffectInstance> getFruitEffects() {
        return new ArrayList<>(fruitEffects);
    }

    @Override
    public void setFruitEffects(List<MobEffectInstance> effects) {
        fruitEffects.clear();
        fruitEffects.addAll(effects);
    }

    public void addFruitEffect(MobEffectInstance effect) {
        boolean exists = fruitEffects.stream().anyMatch(e -> e.getEffect().equals(effect.getEffect()));
        if (!exists) {
            fruitEffects.add(new MobEffectInstance(effect)); // Ensure we're adding a copy of the effect
            DevilFruits.LOGGER.atInfo().log("Added effect: " + effect.getEffect().getDescriptionId());
        } else {
            DevilFruits.LOGGER.atInfo().log("Effect already exists: " + effect.getEffect().getDescriptionId());
        }
    }

    @Override
    public void removeFruitEffect(MobEffect effect) {
        fruitEffects.removeIf(e -> e.getEffect() == effect);
    }

    @Override
    public void clearFruitEffects() {
        fruitEffects.clear();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag effectsList = new ListTag();

        for (MobEffectInstance effect : this.fruitEffects) {
            CompoundTag effectTag = new CompoundTag();
            effectTag.putInt("EffectID", MobEffect.getId(effect.getEffect()));
            effectTag.putInt("Duration", effect.getDuration());
            effectTag.putInt("Amplifier", effect.getAmplifier());
            effectTag.putBoolean("Ambient", effect.isAmbient());
            effectTag.putBoolean("Visible", effect.isVisible());

            effectsList.add(effectTag);
        }

        tag.put("FruitEffects", effectsList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag effectsList = nbt.getList("FruitEffects", CompoundTag.TAG_COMPOUND);

        this.fruitEffects.clear();

        for (int i = 0; i < effectsList.size(); i++) {
            CompoundTag effectTag = effectsList.getCompound(i);

            MobEffect effect = MobEffect.byId(effectTag.getInt("EffectID"));
            int duration = effectTag.getInt("Duration");
            int amplifier = effectTag.getInt("Amplifier");
            boolean ambient = effectTag.getBoolean("Ambient");
            boolean visible = effectTag.getBoolean("Visible");

            if (effect != null) {
                MobEffectInstance effectInstance = new MobEffectInstance(effect, duration, amplifier, ambient, visible);
                this.fruitEffects.add(effectInstance);
            }
        }
    }
}