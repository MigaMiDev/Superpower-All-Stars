package ttv.migami.spas.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import ttv.migami.spas.Reference;
import ttv.migami.spas.common.FruitDataHandler;
import ttv.migami.spas.effect.FruitEffect;
import ttv.migami.spas.network.persistent.MasterySyncPacket;
import ttv.migami.spas.network.persistent.ModNetworking;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityKillEventHandler {

    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity entity = event.getEntity();
        LivingEntity killer = entity.getKillCredit();

        if (killer == null) {
            return;
        }

        if (killer instanceof Player player && FruitDataHandler.getCurrentEffect(player) instanceof FruitEffect fruitEffect) {
            ResourceLocation effectLocation = ForgeRegistries.MOB_EFFECTS.getKey(fruitEffect);

            if (entity instanceof Enemy || entity instanceof Player) {
                String experienceKey = effectLocation + "_MasteryExperience";
                String levelKey = effectLocation + "_MasteryLevel";

                int currentExperience = player.getPersistentData().getInt(experienceKey);
                int currentLevel = player.getPersistentData().getInt(levelKey);
                int experienceReward = entity.getExperienceReward();

                currentExperience += experienceReward;

                while (currentLevel < 100 && currentExperience >= FruitDataHandler.getExperienceForNextLevel(currentLevel)) {
                    currentExperience -= FruitDataHandler.getExperienceForNextLevel(currentLevel);
                    currentLevel++;
                }

                if (currentLevel >= 100) {
                    currentLevel = 100;
                    currentExperience = 0;
                }

                player.getPersistentData().putInt(experienceKey, currentExperience);
                player.getPersistentData().putInt(levelKey, currentLevel);
                syncMasteryData((ServerPlayer) player, effectLocation, currentExperience, currentLevel);
            }
        }
    }

    public static void syncMasteryData(ServerPlayer player, ResourceLocation effectLocation, int masteryExperience, int masteryLevel) {
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new MasterySyncPacket(effectLocation, masteryExperience, masteryLevel));
    }
}