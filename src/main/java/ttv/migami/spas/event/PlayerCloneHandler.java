package ttv.migami.spas.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ttv.migami.spas.Reference;
import ttv.migami.spas.SuperpowerAllStars;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.common.network.ServerPlayHandler;
import ttv.migami.spas.effect.FruitEffect;

import java.util.*;

import static ttv.migami.spas.event.EntityKillEventHandler.syncMasteryData;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PlayerCloneHandler {
    public static final Map<UUID, Map<MobEffectInstance, Integer>> playerEffects = new HashMap<>();
    public static final Map<UUID, List<MobEffectInstance>> fruitEffects = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();

        if (oldData.contains(FruitDataHandler.CURRENT_EFFECT_KEY)) {
            newData.putInt(FruitDataHandler.CURRENT_EFFECT_KEY,
                    oldData.getInt(FruitDataHandler.CURRENT_EFFECT_KEY));
        }

        if (oldData.contains(FruitDataHandler.PREVIOUS_EFFECTS_KEY)) {
            newData.put(FruitDataHandler.PREVIOUS_EFFECTS_KEY,
                    oldData.get(FruitDataHandler.PREVIOUS_EFFECTS_KEY));
        }

        for (String key : oldData.getAllKeys()) {
            if (key.endsWith("_MasteryExperience") || key.endsWith("_MasteryLevel")) {
                newData.putInt(key, oldData.getInt(key));
            }
        }

        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            ServerPlayHandler.syncToClient(player);

            for (String key : newData.getAllKeys()) {
                if (key.endsWith("_MasteryExperience") || key.endsWith("_MasteryLevel")) {
                    ResourceLocation effectLocation = new ResourceLocation(key.substring(0, key.lastIndexOf("_")));
                    int masteryExperience = newData.getInt(effectLocation + "_MasteryExperience");
                    int masteryLevel = newData.getInt(effectLocation + "_MasteryLevel");

                    syncMasteryData(player, effectLocation, masteryExperience, masteryLevel);
                }
            }
        }

        UUID playerId = event.getOriginal().getUUID();
        Map<MobEffectInstance, Integer> effects = new HashMap<>();
        List<MobEffectInstance> fruitEffectInstances = new ArrayList<>();

        for (MobEffectInstance effect : event.getOriginal().getActiveEffects()) {
            if (effect.getEffect() instanceof FruitEffect) {
                effects.put(effect, effect.getDuration());
                fruitEffectInstances.add(effect);
            }
        }

        playerEffects.put(playerId, effects);
        //fruitEffects.put(playerId, fruitEffectInstances);
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayHandler.syncToClient(player);

            CompoundTag data = player.getPersistentData();
            for (String key : data.getAllKeys()) {
                if (key.endsWith("_MasteryExperience") || key.endsWith("_MasteryLevel")) {
                    ResourceLocation effectLocation = new ResourceLocation(key.substring(0, key.lastIndexOf("_")));
                    int masteryExperience = data.getInt(effectLocation + "_MasteryExperience");
                    int masteryLevel = data.getInt(effectLocation + "_MasteryLevel");

                    syncMasteryData(player, effectLocation, masteryExperience, masteryLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayHandler.syncToClient(player);

            CompoundTag data = player.getPersistentData();
            for (String key : data.getAllKeys()) {
                if (key.endsWith("_MasteryExperience") || key.endsWith("_MasteryLevel")) {
                    ResourceLocation effectLocation = new ResourceLocation(key.substring(0, key.lastIndexOf("_")));
                    int masteryExperience = data.getInt(effectLocation + "_MasteryExperience");
                    int masteryLevel = data.getInt(effectLocation + "_MasteryLevel");

                    syncMasteryData(player, effectLocation, masteryExperience, masteryLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayHandler.syncToClient(player);

            UUID playerId = player.getUUID();
            CompoundTag data = player.getPersistentData();
            for (String key : data.getAllKeys()) {
                if (key.endsWith("_MasteryExperience") || key.endsWith("_MasteryLevel")) {
                    ResourceLocation effectLocation = new ResourceLocation(key.substring(0, key.lastIndexOf("_")));
                    int masteryExperience = data.getInt(effectLocation + "_MasteryExperience");
                    int masteryLevel = data.getInt(effectLocation + "_MasteryLevel");

                    syncMasteryData(player, effectLocation, masteryExperience, masteryLevel);
                }
            }

            if (playerEffects.containsKey(playerId)) {
                Map<MobEffectInstance, Integer> effects = playerEffects.get(playerId);
                for (Map.Entry<MobEffectInstance, Integer> entry : effects.entrySet()) {
                    MobEffectInstance effect = new MobEffectInstance(entry.getKey().getEffect(), entry.getValue(), entry.getKey().getAmplifier(), entry.getKey().isAmbient(), entry.getKey().isVisible());
                    player.addEffect(effect);
                    SuperpowerAllStars.LOGGER.atInfo().log("Restoring effect on respawn: " + effect.getEffect().getDescriptionId());
                }
                playerEffects.remove(playerId);
            }
        }
    }
}