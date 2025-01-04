package ttv.migami.spas.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

public class EffectUtils {
    public static List<MobEffect> getCustomPotionEffects(String modid) {
        return ForgeRegistries.MOB_EFFECTS.getValues().stream()
            .filter(effect -> ForgeRegistries.MOB_EFFECTS.getKey(effect).getNamespace().equals(modid))
            .collect(Collectors.toList());
    }
}