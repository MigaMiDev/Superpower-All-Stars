package ttv.migami.spas.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class FruitDataHandler {

    public static final String CURRENT_EFFECT_KEY = "currentPotionEffect";
    public static final String PREVIOUS_EFFECTS_KEY = "previousPotionEffects";

    public static void setCurrentEffect(Player player, MobEffect effect) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.putInt(CURRENT_EFFECT_KEY, MobEffect.getId(effect));
    }

    public static MobEffect getCurrentEffect(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        int effectId = persistentData.getInt(CURRENT_EFFECT_KEY);
        return effectId == 0 ? null : MobEffect.byId(effectId);
    }

    public static void clearCurrentEffect(Player player) {
        MobEffect currentEffect = getCurrentEffect(player);
        if (currentEffect != null) {
            player.removeEffect(currentEffect);
        }
    }

    public static void addPreviousEffect(Player player, MobEffect effect) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag previousEffects = persistentData.getList(PREVIOUS_EFFECTS_KEY, Tag.TAG_INT);

        int effectId = MobEffect.getId(effect);
        boolean alreadyExists = false;

        // Check if the effect is already in the list
        for (Tag tag : previousEffects) {
            if (tag instanceof IntTag && ((IntTag) tag).getAsInt() == effectId) {
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) {
            previousEffects.add(IntTag.valueOf(effectId));
            persistentData.put(PREVIOUS_EFFECTS_KEY, previousEffects);
        }
    }

    public static List<MobEffect> getPreviousEffects(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag previousEffects = persistentData.getList(PREVIOUS_EFFECTS_KEY, Tag.TAG_INT);

        List<MobEffect> effects = new ArrayList<>();
        for (Tag tag : previousEffects) {
            if (tag instanceof IntTag) {
                int effectId = ((IntTag) tag).getAsInt();
                MobEffect effect = MobEffect.byId(effectId);
                if (effect != null) {
                    effects.add(effect);
                }
            }
        }
        return effects;
    }

    public static void clearPreviousEffects(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        persistentData.remove(PREVIOUS_EFFECTS_KEY);
    }
}