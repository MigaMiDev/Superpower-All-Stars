package ttv.migami.mdf.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import ttv.migami.mdf.DevilFruits;
import ttv.migami.mdf.Reference;
import ttv.migami.mdf.effect.FruitEffect;
import ttv.migami.mdf.init.ModCapabilities;
import ttv.migami.mdf.capanility.SyncFruitEffectCapabilityPacket;

import java.util.*;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class AfterDeathHandler {
    public static final Map<UUID, Map<MobEffectInstance, Integer>> playerEffects = new HashMap<>();
    public static final Map<UUID, List<MobEffectInstance>> fruitEffects = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.Clone event) {
        //if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            ServerPlayer original = (ServerPlayer) event.getOriginal();
            ServerPlayer player = (ServerPlayer) event.getEntity();

            original.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(oldCap -> {
                DevilFruits.LOGGER.atInfo().log("Original does exist");
                player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(newCap -> {
                    for (MobEffectInstance effect : oldCap.getFruitEffects()) {
                        DevilFruits.LOGGER.atInfo().log("Transferring effect: " + effect.getEffect().getDescriptionId());
                        newCap.addFruitEffect(effect);
                    }
                });
            });

            original.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(oldCap -> {
                for (MobEffectInstance effect : oldCap.getFruitEffects()) {
                    DevilFruits.LOGGER.atInfo().log("Original effect: " + effect.getEffect().getDescriptionId());
                }
            });

            UUID playerId = original.getUUID();
            Map<MobEffectInstance, Integer> effects = new HashMap<>();
            List<MobEffectInstance> fruitEffectInstances = new ArrayList<>();

            for (MobEffectInstance effect : original.getActiveEffects()) {
                if (effect.getEffect() instanceof FruitEffect) {
                    effects.put(effect, effect.getDuration());
                    fruitEffectInstances.add(effect);
                }
            }

        if (event.getEntity() instanceof ServerPlayer serverPlayerNew && event.getOriginal() instanceof ServerPlayer serverPlayerOld) {
            if (!event.isWasDeath()) {
                serverPlayerNew.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(capabilityNew ->
                        serverPlayerOld.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(capabilityOld ->
                                capabilityNew.deserializeNBT(capabilityOld.serializeNBT())));

                serverPlayerOld.invalidateCaps();
            }
        }

            playerEffects.put(playerId, effects);
            fruitEffects.put(playerId, fruitEffectInstances);
            event.getOriginal().invalidateCaps();
        //}
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUUID();

        if (playerEffects.containsKey(playerId)) {
            Map<MobEffectInstance, Integer> effects = playerEffects.get(playerId);
            for (Map.Entry<MobEffectInstance, Integer> entry : effects.entrySet()) {
                MobEffectInstance effect = new MobEffectInstance(entry.getKey().getEffect(), entry.getValue(), entry.getKey().getAmplifier(), entry.getKey().isAmbient(), entry.getKey().isVisible());
                player.addEffect(effect);
                DevilFruits.LOGGER.atInfo().log("Restoring effect on respawn: " + effect.getEffect().getDescriptionId());
            }
            playerEffects.remove(playerId);
        }

        if (fruitEffects.containsKey(playerId)) {
            List<MobEffectInstance> storedFruitEffects = fruitEffects.get(playerId);
            player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
                for (MobEffectInstance effect : storedFruitEffects) {
                    cap.addFruitEffect(effect);
                    DevilFruits.LOGGER.atInfo().log("Restoring fruit effect on respawn: " + effect.getEffect().getDescriptionId());
                }
            });
            fruitEffects.remove(playerId);
        }

        player.getCapability(ModCapabilities.FRUIT_EFFECT_CAPABILITY).ifPresent(cap -> {
            if (!player.level().isClientSide) {
                ModNetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                        new SyncFruitEffectCapabilityPacket(cap.getFruitEffects()));
            }
        });
    }
}